
package com.ch3d.xreminderx.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.ch3d.xreminderx.adapter.RemindersAdapter;
import com.ch3d.xreminderx.provider.RemindersProvider;

public class ReminderSearchResultActivity extends ListActivity {

    private void handleIntent(final Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            // use the query to search your data somehow
            final Cursor cursor = getContentResolver().query(
                    Uri.withAppendedPath(RemindersProvider.REMINDERS_SEARCH, query),
                    null,
                    null,
                    null,
                    null);
            setListAdapter(new RemindersAdapter(this, cursor, false));
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        handleIntent(intent);
    }

}
