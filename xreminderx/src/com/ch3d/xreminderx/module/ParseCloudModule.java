
package com.ch3d.xreminderx.module;

import javax.inject.Singleton;

import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.provider.RemoteProvider;
import com.ch3d.xreminderx.provider.parse.ParseCloudProvider;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        RemindersProvider.class
}, complete = false)
public class ParseCloudModule {
    @Provides
    @Singleton
    RemoteProvider provideRemote() {
        return new ParseCloudProvider();
    }
}
