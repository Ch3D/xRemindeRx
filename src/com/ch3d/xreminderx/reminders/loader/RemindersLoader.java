package com.ch3d.xreminderx.reminders.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import com.ch3d.xreminderx.reminders.RemindersListFragment;
import com.ch3d.xreminderx.reminders.provider.RemindersProvider;

public class RemindersLoader extends CursorLoader
{
	private final int	mTag;

	public RemindersLoader(final Context context, final Bundle bundle)
	{
		super(context);
		// mTag = bundle.getInt(RemindersListFragment.TAG, RemindersListFragment.TAG_TODAY);
		mTag = RemindersListFragment.TAG_ALL;
	}

	@Override
	public Cursor loadInBackground()
	{
		final Uri uri = Uri.parse(RemindersProvider.REMINDERS_URI
				+ ((mTag == RemindersListFragment.TAG_TODAY) ? "/today" : "/all"));
		final ContentResolver contentResolver = getContext().getContentResolver();

		final Cursor cursor = contentResolver.query(uri, null, null, null, null);
		return cursor;
	}
}
