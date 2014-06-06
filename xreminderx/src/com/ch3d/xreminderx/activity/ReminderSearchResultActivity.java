package com.ch3d.xreminderx.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ch3d.xreminderx.adapter.RemindersAdapter;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ActivityUtils;

public class ReminderSearchResultActivity extends ListActivity {

	private void handleIntent(final Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			final String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
			final Cursor cursor = getContentResolver().query(RemindersProvider.REMINDERS_URI,
					null, null, null, null);
			setListAdapter(new RemindersAdapter(this, cursor, false));
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		handleIntent(getIntent());
		getListView().setDividerHeight(0);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> arg0, final View view, final int pos,
			                        final long id) {
				ActivityUtils.startDetailsActivity(ReminderSearchResultActivity.this, view, pos);
			}
		});
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		handleIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
