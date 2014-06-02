package com.ch3d.xreminderx.sync.parse;

import android.os.SystemClock;

import com.ch3d.xreminderx.sync.RemoteSyncProtocol;

/**
 * Created by ch3d on 02-Jun-14.
 */
public class ParseSyncProtocol implements RemoteSyncProtocol {
	@Override
	public void sync() {
		SystemClock.sleep(2000);
	}
}