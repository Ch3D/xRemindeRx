
package com.ch3d.xreminderx.module;

import static android.content.Context.LOCATION_SERVICE;

import javax.inject.Singleton;

import android.app.SearchManager;
import android.content.Context;
import android.location.LocationManager;

import com.ch3d.xreminderx.app.ReminderApplication;

import dagger.Module;
import dagger.Provides;

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
    SearchManager provideSearchManager() {
        return (SearchManager) application.getSystemService(Context.SEARCH_SERVICE);
    }
}
