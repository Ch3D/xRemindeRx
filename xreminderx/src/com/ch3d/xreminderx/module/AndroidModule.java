package com.ch3d.xreminderx.module;

import android.app.SearchManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import com.ch3d.xreminderx.app.ReminderApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.POWER_SERVICE;
import static android.content.Context.SEARCH_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

@Module(library = true)
public class AndroidModule {
	private final ReminderApplication application;

	public AndroidModule(ReminderApplication app) {
		application = app;
	}

	@Provides
	@Singleton
	Context provideApplicationContext() {
		return application;
	}

	@Provides
	@Singleton
	LocationManager provideLocationManager() {
		return (LocationManager) application.getSystemService(LOCATION_SERVICE);
	}

	@Provides
	@Singleton
	PowerManager providePowerManager() {
		return (PowerManager) application.getSystemService(POWER_SERVICE);
	}

	@Provides
	@Singleton
	SearchManager provideSearchManager() {
		return (SearchManager) application.getSystemService(SEARCH_SERVICE);
	}

	@Provides
	@Singleton
	TelephonyManager provideTelephonyManager() {
		return (TelephonyManager) application.getSystemService(TELEPHONY_SERVICE);
	}
}
