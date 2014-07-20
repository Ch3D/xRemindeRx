package com.ch3d.xreminderx.activity.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.StringUtils;

/**
 * Created by ch3d on 20-Jul-14.
 */
public class ShareIntentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final String action = intent.getAction();
		final String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				handleSendText(intent);
			}
		}
		finish();
	}

	private void handleSendText(final Intent intent) {
		final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (!StringUtils.isBlank(sharedText)) {
			ReminderEntry entry = ReminderFactory.createNew();
			entry.setText(sharedText);
			RemindersProvider.addReminder(this, entry);
		}
	}
}
