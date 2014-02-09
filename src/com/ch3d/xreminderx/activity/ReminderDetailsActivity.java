
package com.ch3d.xreminderx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.app.BaseFragmentActivity;
import com.ch3d.xreminderx.fragment.ReminderCreateFragment;
import com.ch3d.xreminderx.fragment.ReminderEditFragment;
import com.ch3d.xreminderx.fragment.ReminderViewFragment;
import com.ch3d.xreminderx.utils.ReminderIntent;

public class ReminderDetailsActivity extends BaseFragmentActivity
{
    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_reminder_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (savedInstanceState == null)
        {
            if (Intent.ACTION_VIEW.equals(action))
            {
                final FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                final ReminderViewFragment viewFragment = new ReminderViewFragment();
                trx.add(R.x_reminder_details.root, viewFragment, ReminderViewFragment.TAG);
                trx.commit();
            }
            else if (Intent.ACTION_EDIT.equals(action))
            {
                final FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                final ReminderEditFragment editFragment = new ReminderEditFragment();
                trx.add(R.x_reminder_details.root, editFragment, ReminderViewFragment.TAG);
                trx.commit();
            }
            else if (ReminderIntent.ACTION_NEW.equals(action))
            {
                final FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                final ReminderCreateFragment createFragment = new ReminderCreateFragment();
                trx.add(R.x_reminder_details.root, createFragment, ReminderCreateFragment.TAG);
                trx.commit();
            }
        }
    }
}
