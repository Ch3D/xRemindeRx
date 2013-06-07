package com.ch3d.xreminderx.reminders.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.ch3d.xreminderx.reminders.model.ReminderEntry;
import com.ch3d.xreminderx.reminders.utils.ReminderUtils;

public class RemindersProvider extends ContentProvider
{
	private static final String		PATH_REMINDERS	= "reminders";

	private static final String		AUTHORITY		= "com.ch3d.xreminderx.provider";

	public static final Uri			URI				= Uri.parse("content://" + AUTHORITY);

	public static final Uri			REMINDERS_URI	= Uri.withAppendedPath(URI, PATH_REMINDERS);

	private static final UriMatcher	sUriMatcher		= new UriMatcher(UriMatcher.NO_MATCH);

	private static final int		TODAY			= 1;

	private static final int		ALL				= 2;

	private static final int		REMINDERS		= 3;

	private ReminderDBHelper		mDbHelper;

	static
	{
		sUriMatcher.addURI(AUTHORITY, PATH_REMINDERS + "/all", ALL);
		sUriMatcher.addURI(AUTHORITY, PATH_REMINDERS + "/today", TODAY);
		sUriMatcher.addURI(AUTHORITY, PATH_REMINDERS + "/#", REMINDERS);
	}

	public static Uri addReminder(final Context context, final ReminderEntry reminder)
	{
		final Uri result = context.getContentResolver().insert(RemindersProvider.REMINDERS_URI,
				ReminderUtils.getContentValues(reminder));
		context.getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);
		return result;
	}

	public static int updateReminder(final Context context, final ReminderEntry reminder)
	{
		return updateReminder(context, reminder, false);
	}

	public static int updateReminder(final Context context, final ReminderEntry reminder, final boolean notify)
	{
		final ContentValues values = ReminderUtils.getContentValues(reminder);
		final Uri uri = ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, reminder.getId());
		final int result = context.getContentResolver().update(uri, values, null, null);
		if(notify)
		{
			context.getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);
		}
		return result;
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs)
	{
		return mDbHelper.deleteReminder(selection, selectionArgs);
	}

	@Override
	public String getType(final Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values)
	{
		final long id = mDbHelper.getWritableDatabase().insert(PATH_REMINDERS, null, values);
		return ContentUris.withAppendedId(REMINDERS_URI, id);
	}

	@Override
	public boolean onCreate()
	{
		mDbHelper = new ReminderDBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder)
	{
		switch(sUriMatcher.match(uri))
		{
			case ALL:
				return mDbHelper.queryAll();

			case TODAY:
				return mDbHelper.queryToday();

			case REMINDERS:
				return mDbHelper.queryReminder(ContentUris.parseId(uri));
		}
		return mDbHelper.query(uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs)
	{
		return mDbHelper.updateReminder(uri, values, selection, selectionArgs);
	}

}
