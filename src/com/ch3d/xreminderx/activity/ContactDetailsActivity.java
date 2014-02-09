
package com.ch3d.xreminderx.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.ch3d.xreminderx.app.BaseFragmentActivity;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.notifications.RingerService;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.ReminderIntent;
import com.ch3d.xreminderx.utils.ReminderUtils;

public class ContactDetailsActivity extends BaseFragmentActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Uri data = getIntent().getData();
        final Cursor query = getContentResolver().query(data, null, null, null, null);
        final ReminderEntry reminder = ReminderUtils.parse(query);

        final AlarmManager aManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent operation = ReminderUtils.getPendingAlarmOperation(this,
                reminder.getId());

        if (!ReminderIntent.ACTION_NOTIFICATION_ONGOING.equals(getIntent().getAction()))
        {
            // cancel alarm
            aManager.cancel(operation);

            // cancel notification
            nManager.cancel(reminder.getId());

            // stop ringing
            stopService(new Intent(this, RingerService.class));
        }

        final Intent cdIntent = ActivityUtils.getContactDetailsIntent(this, reminder);
        cdIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(cdIntent);
        finish();
    }
}
