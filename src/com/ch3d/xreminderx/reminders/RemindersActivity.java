package com.ch3d.xreminderx.reminders;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.reminders.model.ReminderEntry;
import com.ch3d.xreminderx.reminders.provider.RemindersProvider;
import com.ch3d.xreminderx.reminders.utils.ReminderUtils;

public class RemindersActivity extends FragmentActivity
{
	private NdefMessage[] getNdefMessages(final Intent intent)
	{
		NdefMessage[] msgs = null;
		final Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if(rawMsgs != null)
		{
			msgs = new NdefMessage[rawMsgs.length];
			for(int i = 0; i < rawMsgs.length; i++)
			{
				msgs[i] = (NdefMessage)rawMsgs[i];
			}
		}
		return msgs;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.x_reminders);
		setTitle(R.string.reminders);

		// parse intent in case if
		// application is not already running
		parseNfcIntent(getIntent());
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		super.onNewIntent(intent);
		// parse intent in case if application is running
		parseNfcIntent(intent);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		final int int1 = PreferenceManager.getDefaultSharedPreferences(this).getInt(
				Consts.PREFS.SHOW_REMINDER_POSTPONE_TIME, Consts.POSTPONE_TIME_DEFAULT);
		System.err.println("time = " + int1);
	}

	private void parseNdefMessages(final NdefMessage[] msgs)
	{
		if(msgs == null)
		{
			return;
		}
		for(final NdefMessage msg : msgs)
		{
			for(final NdefRecord rec : msg.getRecords())
			{
				final ReminderEntry reminder = ReminderUtils.parseReminder(rec);
				final Cursor cursor = getContentResolver().query(
						RemindersProvider.REMINDERS_URI,
						null,
						"text = ? AND ts = ? AND alarm_ts = ?",
						new String[] {reminder.getText(), Long.toString(reminder.getTimestamp()),
								Long.toString(reminder.getAlarmTimestamp())}, null);
				if(cursor.getCount() > 0)
				{
					final AlertDialog.Builder builder = new Builder(this);
					builder.setTitle(R.string.reminder_is_already_exist);
					builder.setMessage(reminder.getText());
					builder.setPositiveButton(R.string.add, new OnClickListener()
					{
						@Override
						public void onClick(final DialogInterface dialog, final int which)
						{
							RemindersProvider.addReminder(RemindersActivity.this, reminder);
							dialog.dismiss();
						}
					});
					builder.setNegativeButton(R.string.cancel, new OnClickListener()
					{
						@Override
						public void onClick(final DialogInterface dialog, final int which)
						{
							dialog.dismiss();
						}
					});
					builder.show();
				}
				else
				{
					final Uri uri = RemindersProvider.addReminder(this, reminder);
					final long id = ContentUris.parseId(uri);
					ReminderUtils.setAlarm(this, id, reminder);
				}
			}
		}
	}

	private void parseNfcIntent(final Intent intent)
	{
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			final NdefMessage[] msgs = getNdefMessages(intent);
			parseNdefMessages(msgs);
		}
	}
}
