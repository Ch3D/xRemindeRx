package com.ch3d.xreminderx.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.app.BaseFragmentActivity;
import com.ch3d.xreminderx.fragment.ReminderCreateFragment;
import com.ch3d.xreminderx.fragment.ReminderEditFragment;
import com.ch3d.xreminderx.fragment.ReminderViewFragment;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.utils.ReminderIntent;
import com.ch3d.xreminderx.utils.ReminderUtils;

public class ReminderDetailsActivity extends BaseFragmentActivity {
	public static Intent newIntent(final Context c, final String action) {
		final Intent intent = new Intent(c, ReminderDetailsActivity.class);
		intent.setAction(action);
		return intent;
	}

	public static Intent newIntent(final Context c, final String action, final int flags,
	                               final Uri data) {
		final Intent intent = newIntent(c, action, data);
		intent.addFlags(flags);
		return intent;
	}

	public static Intent newIntent(final Context c, final String action, final Uri data) {
		final Intent intent = newIntent(c, action);
		intent.setData(data);
		return intent;
	}

	private void applyColorTheme() {
		final ReminderEntry reminder = getReminder();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	private ReminderEntry getReminder() {
		final Uri data = getIntent().getData();
		if (data == null) {
			return null;
		}

		final Cursor query = getContentResolver().query(
				data, null, null,
				null, null);
		return ReminderUtils.parse(query);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applyColorTheme();
		setContentView(R.layout.x_reminder_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		final Intent intent = getIntent();
		final String action = intent.getAction();

		if (savedInstanceState == null) {
			if (Intent.ACTION_VIEW.equals(action)) {
				final FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
				final ReminderViewFragment viewFragment = new ReminderViewFragment();
				trx.add(R.x_reminder_details.root, viewFragment, ReminderViewFragment.TAG);
				trx.commit();
			} else if (Intent.ACTION_EDIT.equals(action)) {
				final FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
				trx.add(R.x_reminder_details.root, ReminderEditFragment
						.newInstance(getIntent().getData()), ReminderViewFragment.TAG);
				trx.commit();
			} else if (ReminderIntent.ACTION_NEW.equals(action)) {
				final FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
				final ReminderCreateFragment createFragment = new ReminderCreateFragment();
				trx.add(R.x_reminder_details.root, createFragment, ReminderCreateFragment.TAG);
				trx.commit();
			}
		}
	}
}
