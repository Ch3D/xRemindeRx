
package com.ch3d.xreminderx.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class ReminderSearchResultActivity extends Activity {

    private void handleIntent(final Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            // use the query to search your data somehow
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView()
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        handleIntent(intent);
    }

}
