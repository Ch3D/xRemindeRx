package com.ch3d.xreminderx.module;

import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.provider.RemoteProvider;
import com.ch3d.xreminderx.provider.parse.ParseCloudProvider;
import com.ch3d.xreminderx.sync.RemoteSyncProtocol;
import com.ch3d.xreminderx.sync.RemoteSynchronizer;
import com.ch3d.xreminderx.sync.parse.ParseRemoteSynchronizer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {RemindersProvider.class}, complete = false, library = true)
public class ParseCloudModule {
	@Provides
	@Singleton
	RemoteSynchronizer provideSynchronizer(final RemoteSyncProtocol protocol) {
		return new ParseRemoteSynchronizer(protocol);
	}

	@Provides
	@Singleton
	RemoteProvider provideRemote() {
		return new ParseCloudProvider();
	}
}
