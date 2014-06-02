package com.ch3d.xreminderx.sync;

import android.os.AsyncTask;

/**
 * Created by ch3d on 02-Jun-14.
 */
public abstract class SyncTask<T> extends AsyncTask<Void, Integer, T> {

	private RemoteSynchronizer mSynchronizer;
	private RemoteSynchronizer.Callback<T> mCallback;

	SyncTask(RemoteSynchronizer synchronizer, RemoteSynchronizer.Callback<T> callback) {
		mSynchronizer = synchronizer;
		mCallback = callback;
	}

	@Override
	protected void onPostExecute(final T result) {
		mCallback.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		mCallback.onPreExecute();
	}


}
