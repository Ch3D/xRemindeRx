package com.ch3d.xreminderx.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.ViewUtils;

public class RemindersActivity extends FragmentActivity implements
		android.view.View.OnClickListener {
	private EditText	mEditQuickText;

	private NdefMessage[] getNdefMessages(final Intent intent) {
		NdefMessage[] msgs = null;
		final Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];
			}
		}
		return msgs;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.x_reminders);
		setTitle(R.string.reminders);

		final ImageButton mBtnQuickAdd = (ImageButton) findViewById(android.R.id.button1);
		mBtnQuickAdd.setOnClickListener(this);
		mEditQuickText = (EditText) findViewById(android.R.id.edit);

		// parse intent in case if
		// application is not already running
		parseNfcIntent(getIntent());
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		// parse intent in case if application is running
		parseNfcIntent(intent);
	}

	private void parseNdefMessages(final NdefMessage[] msgs) {
		if (msgs == null) {
			return;
		}
		for (final NdefMessage msg : msgs) {
			for (final NdefRecord rec : msg.getRecords()) {
				final ReminderEntry reminder = ReminderUtils.parseReminder(rec);
				final Cursor cursor = getContentResolver().query(
						RemindersProvider.REMINDERS_URI,
						null,
						"text = ? AND ts = ? AND alarm_ts = ?",
						new String[] { reminder.getText(),
								Long.toString(reminder.getTimestamp()),
								Long.toString(reminder.getAlarmTimestamp()) },
						null);
				if (cursor.getCount() > 0) {
					final AlertDialog.Builder builder = new Builder(this);
					builder.setTitle(R.string.reminder_is_already_exist);
					builder.setMessage(reminder.getText());
					builder.setPositiveButton(R.string.add,
							new OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
									RemindersProvider.addReminder(
											RemindersActivity.this, reminder);
									dialog.dismiss();
								}
							});
					builder.setNegativeButton(R.string.cancel,
							new OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
									dialog.dismiss();
								}
							});
					builder.show();
				} else {
					RemindersProvider.addReminder(this, reminder, true);
				}
			}
		}
	}

	private void parseNfcIntent(final Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			final NdefMessage[] msgs = getNdefMessages(intent);
			parseNdefMessages(msgs);
		}
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
			case android.R.id.button1:
				final ReminderEntry reminder = ReminderFactory.createNull();
				reminder.setText(mEditQuickText.getText().toString());
				reminder.setOngoing(0);
				reminder.setSilent(1);
				reminder.setColor(0);

				RemindersProvider.addReminder(this, reminder, true);
				mEditQuickText.getText().clear();
				ViewUtils.hideKeyboard(mEditQuickText);
				break;

			default:
				break;
		}
	}
}
