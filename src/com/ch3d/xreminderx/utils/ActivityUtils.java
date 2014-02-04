
package com.ch3d.xreminderx.utils;

import android.app.ActivityOptions;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
<<<<<<< HEAD
=======
import android.util.DisplayMetrics;
>>>>>>> 07d04a088ac0fb96e871d060734c06dc8d589e50
import android.view.View;

import com.ch3d.xreminderx.activity.ReminderDetailsActivity;
import com.ch3d.xreminderx.adapter.RemindersAdapter;
import com.ch3d.xreminderx.adapter.RemindersAdapter.ViewHolder;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.provider.RemindersProvider;

public class ActivityUtils
{
<<<<<<< HEAD
=======
    public static float convertPixelsToDp(final float px, final Context context)
    {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        final float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

>>>>>>> 07d04a088ac0fb96e871d060734c06dc8d589e50
    public static Intent createReminderViewIntent(final Context context, final long id)
    {
        final Intent intent = new Intent(context, ReminderDetailsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, id));
        return intent;
    }

<<<<<<< HEAD
    private static ActivityOptions getActivityOptions(final View v, final int position) {
=======
    private static ActivityOptions getActivityOptions(final View v, final int position)
    {
>>>>>>> 07d04a088ac0fb96e871d060734c06dc8d589e50
        final int height = v.getHeight();
        final int width = v.getWidth();
        final boolean isFirst = position == 0;
        final int startY = isFirst ? height >> 2 : (int) (v.getY() - (height >> 1));
        final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v,
                width >> 2, startY, width >> 1, height);
        return options;
    }

    public static Intent getContactDetailsIntent(final Context context, final ReminderEntry reminder)
    {
        return getContactDetailsIntent(context, reminder.getContactUri());
    }

    public static Intent getContactDetailsIntent(final Context context, final Uri contactUri)
    {
        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver().query(contactUri,
                    new String[] {
                        ContactsContract.Contacts.LOOKUP_KEY
                    }, null, null, null);
            if ((cursor != null) && cursor.moveToFirst())
            {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_LOOKUP_URI, cursor.getString(0)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
            }
            return null;
<<<<<<< HEAD
        } finally
=======
        }
        finally
>>>>>>> 07d04a088ac0fb96e871d060734c06dc8d589e50
        {
            DBUtils.close(cursor);
        }
    }

    public static boolean isJeallyBean()
    {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static boolean isJeallyBeanMR1()
    {
        return Build.VERSION.SDK_INT >= 17;
    }

<<<<<<< HEAD
    public static void startDetailsActivity(final Context context, final View v, final int position) {
=======
    public static void startDetailsActivity(final Context context, final View v, final int position)
    {
>>>>>>> 07d04a088ac0fb96e871d060734c06dc8d589e50
        final RemindersAdapter.ViewHolder holder = (ViewHolder) v.getTag();
        final Intent intent = new Intent(context,
                ReminderDetailsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(ContentUris.withAppendedId(
                RemindersProvider.REMINDERS_URI, holder.id));
<<<<<<< HEAD
        if (ActivityUtils.isJeallyBean()) {
            context.startActivity(intent, getActivityOptions(v, position).toBundle());
        } else {
=======
        if (ActivityUtils.isJeallyBean())
        {
            context.startActivity(intent, getActivityOptions(v, position).toBundle());
        }
        else
        {
>>>>>>> 07d04a088ac0fb96e871d060734c06dc8d589e50
            context.startActivity(intent);
        }
    }
}
