package com.ch3d.xreminderx.module;

import com.ch3d.xreminderx.activity.ContactDetailsActivity;
import com.ch3d.xreminderx.activity.ReminderDetailsActivity;
import com.ch3d.xreminderx.activity.ReminderSearchResultActivity;
import com.ch3d.xreminderx.activity.RemindersActivity;
import com.ch3d.xreminderx.activity.SettingsActivity;
import com.ch3d.xreminderx.fragment.RemindersListFragment;

import dagger.Module;

@Module(injects = {
		RemindersActivity.class, ReminderDetailsActivity.class, ContactDetailsActivity.class,
		ReminderSearchResultActivity.class, SettingsActivity.class, RemindersListFragment.class
}, complete = false)
public class ActivityModule {

}
