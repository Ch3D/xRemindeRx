package com.ch3d.xreminderx.reminders.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.ch3d.xreminderx.R;

public class ReminderDBHelper extends SQLiteOpenHelper
{
	private static final String	TABLE_NAME		= "reminders";

	private static final String	DATABASE_NAME	= TABLE_NAME;

	private static final String	SQL_CREATE_MAIN	= "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
														+ "_id INTEGER PRIMARY KEY, " + "protocol INTEGER, "
														+ "type INTEGER, "
														+ "ts TIMESTAMP NOT NULL DEFAULT current_timestamp, "
														+ "alarm_ts BIGINT, " + "text VARCHAR(255), "
														+ "contactUri VARCHAR(255), " + "ongoing INTEGER, "
														+ "silent INTEGER, " + "color INTEGER " + ");";

	public ReminderDBHelper(final Context context)
	{
		super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.protocol_version));
	}

	public int deleteReminder(final String selection, final String[] selectionArgs)
	{
		return getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
	}

	@Override
	public void onCreate(final SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_MAIN);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
	{
		// TODO:
		// final List<String> columns = new ArrayList<String>();
		// columns.addAll(DBUtils.getColumns(db, TABLE_NAME));
		// db.execSQL("ALTER TABLE " + TABLE_NAME + " RENAME TO temp_" + TABLE_NAME);
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// db.execSQL(SQL_CREATE_MAIN);
		// columns.retainAll(DBUtils.getColumns(db, TABLE_NAME));
		// final String cols = DBUtils.join(columns, ",");
		// db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s from temp_%s", TABLE_NAME, cols, cols, TABLE_NAME));
		// db.execSQL("DROP table temp_" + TABLE_NAME);
	}

	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder)
	{
		return getReadableDatabase().query(TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);
	}

	public Cursor queryAll()
	{
		return getReadableDatabase().query(TABLE_NAME, null, null, new String[] {}, null, null, "ts ASC");
	}

	public Cursor queryReminder(final long parseId)
	{
		return getReadableDatabase().query(TABLE_NAME, null, "_id = ?", new String[] {Long.toString(parseId)}, null,
				null, null);
	}

	public Cursor queryToday()
	{
		return getReadableDatabase().query(
				TABLE_NAME,
				null,
				"ts > strftime(\"%s\",\"now\",\"-1 days\") * 1000" + " AND "
						+ "ts < strftime(\"%s\",\"now\",\"+1 days\") * 1000", null, null, null, "ts ASC");
	}

	public int updateReminder(final Uri uri, final ContentValues values, final String selection,
			final String[] selectionArgs)
	{
		final long id = ContentUris.parseId(uri);
		return getWritableDatabase().update(TABLE_NAME, values, "_id = ?", new String[] {Long.toString(id)});
	}

}
