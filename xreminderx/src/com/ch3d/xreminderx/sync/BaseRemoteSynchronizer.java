package com.ch3d.xreminderx.sync;

/**
 * Created by ch3d on 02-Jun-14.
 */
public abstract class BaseRemoteSynchronizer implements RemoteSynchronizer<Void> {
	protected RemoteSyncProtocol protocol;

	public BaseRemoteSynchronizer(final RemoteSyncProtocol protocol) {
		if (protocol == null) throw new RuntimeException("Protocol cannot be null!");
		this.protocol = protocol;
	}

	@Override
	public final void sync(Callback<Void> callback) {
		new SyncTask<Void>(this, callback) {
			@Override
			protected Void doInBackground(Void... params) {
				performSync();
				return null;
			}
		}.execute();
	}

	protected void performSync() {
		protocol.sync();
	}
}
