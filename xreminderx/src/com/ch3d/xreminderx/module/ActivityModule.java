package com.ch3d.xreminderx.module;

import com.ch3d.xreminderx.activity.ContactDetailsActivity;
import com.ch3d.xreminderx.activity.ReminderDetailsActivity;
import com.ch3d.xreminderx.activity.ReminderSearchResultActivity;
import com.ch3d.xreminderx.activity.RemindersActivity;
import com.ch3d.xreminderx.activity.SettingsActivity;
import com.ch3d.xreminderx.app.ReminderApplication;
import com.ch3d.xreminderx.fragment.RemindersListFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {RemindersActivity.class, ReminderDetailsActivity.class, ContactDetailsActivity.class, ReminderSearchResultActivity.class,
		SettingsActivity.class, RemindersListFragment.class}, complete = false)
public class ActivityModule {
	private final ReminderApplication app;

	public ActivityModule(ReminderApplication reminderApplication) {
		app = reminderApplication;
	}

	@Provides
	@Singleton
	Bus provideEventBus() {
		return app.getEventBus();
	}
}
