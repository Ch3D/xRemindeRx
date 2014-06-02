package com.ch3d.xreminderx.sync;

/**
 * Created by ch3d on 02-Jun-14.
 */
public interface RemoteSynchronizer<T> {
	public void sync(Callback<T> callback);

	public interface Callback<T> {
		public void onPreExecute();

		public void onPostExecute(T result);
	}
}
