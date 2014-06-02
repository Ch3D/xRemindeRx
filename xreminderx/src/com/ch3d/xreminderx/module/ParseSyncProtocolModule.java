package com.ch3d.xreminderx.module;

import com.ch3d.xreminderx.fragment.RemindersListFragment;
import com.ch3d.xreminderx.sync.RemoteSyncProtocol;
import com.ch3d.xreminderx.sync.parse.ParseSyncProtocol;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ch3d on 02-Jun-14.
 */
@Module(injects = {RemindersListFragment.class}, complete = false, library = true)
public class ParseSyncProtocolModule {
	@Provides
	@Singleton
	RemoteSyncProtocol provideProtocol() {
		return new ParseSyncProtocol();
	}
}
