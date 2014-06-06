package com.ch3d.xreminderx.sync;

/**
 * Created by ch3d on 02-Jun-14.
 */
public abstract class BaseRemoteSynchronizer<T> implements RemoteSynchronizer<T> {
	protected RemoteSyncProtocol protocol;

	public BaseRemoteSynchronizer(final RemoteSyncProtocol protocol) {
		if (protocol == null) throw new RuntimeException("Protocol cannot be null!");
		this.protocol = protocol;
	}

	@Override
	public final void sync(final Callback<T> callback) {
		new SyncTask<T>(this, callback) {
			@Override
			protected T doInBackground(final Void... params) {
				performSync();
				return null;
			}
		}.execute();
	}

	protected void performSync() {
		protocol.sync();
	}
}
