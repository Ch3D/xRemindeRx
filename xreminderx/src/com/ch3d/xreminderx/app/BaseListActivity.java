
package com.ch3d.xreminderx.app;

import android.app.ListActivity;
import android.os.Bundle;

public class BaseListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ReminderApplication) getApplication()).inject(this);
    }
}
