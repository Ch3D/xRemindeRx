package com.ch3d.xreminderx.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBUtils {
	private static final String LOG_TAG = DBUtils.class.getSimpleName();

	public static void close(final Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	public static List<String> getColumns(final SQLiteDatabase db, final String tableName) {
		List<String> ar = null;
		Cursor c = null;
		try {
			c = db.rawQuery("select * from " + tableName + " limit 1", null);
			if (c != null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
			}
		} catch (final Exception e) {
			Log.v(tableName, e.getMessage(), e);
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return ar;
	}

	public static String join(final List<String> list, final String delim) {
		final StringBuilder buf = new StringBuilder();
		final int num = list.size();
		for (int i = 0; i < num; i++) {
			if (i != 0) {
				buf.append(delim);
			}
			buf.append(list.get(i));
		}
		return buf.toString();
	}


	public static void closeCursor(final Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	public static <E extends Object> E getEntry(final Cursor cursor, final CursorEntryVisitor<E> visitor) {
		E result = null;
		try {
			cursor.moveToFirst();
			while ((result == null) && !cursor.isAfterLast()) {
				result = visitor.visit(cursor);
				cursor.moveToNext();
			}
		} catch (final Throwable e) {
			Log.e(LOG_TAG, "getEntry", e);
		} finally {
			DBUtils.closeCursor(cursor);
		}
		return result;
	}

	public static <E extends Object> E getEntry2(final Cursor cursor, final CursorEntryVisitor<E> visitor) {
		E result = null;
		try {
			cursor.moveToFirst();
			while ((result == null) && !cursor.isAfterLast()) {
				result = visitor.visit(cursor);
				cursor.moveToNext();
			}
		} catch (final Throwable e) {
			Log.e(LOG_TAG, "getEntry", e);
		} finally {
			cursor.moveToFirst();
		}
		return result;
	}

	/**
	 * Process all items in cursor and then close it.
	 *
	 * @param cursor  - cursor to visit
	 * @param visitor - cursor visitor
	 */
	public static void processCursor(final Cursor cursor, final CursorVisitor visitor) {
		try {
			if (cursor == null) {
				Log.e(LOG_TAG, "Trying to process NULL cursor");
				return;
			}
			if (!cursor.isFirst()) {
				cursor.moveToFirst();
			}
			while (!cursor.isAfterLast()) {
				visitor.visit(cursor);
				cursor.moveToNext();
			}
		} catch (final Throwable e) {
			Log.e(LOG_TAG, "processCursor", e);
		} finally {
			DBUtils.closeCursor(cursor);
		}
	}

	/**
	 * Unlike {@link #processCursor(Cursor, CursorVisitor)} this one do not close cursor when done
	 *
	 * @param cursor  - cursor to visit
	 * @param visitor - cursor visitor.
	 */
	public static void processCursor2(final Cursor cursor, final CursorVisitor visitor) {
		try {
			if (!cursor.isFirst()) {
				cursor.moveToFirst();
			}
			while (!cursor.isAfterLast()) {
				visitor.visit(cursor);
				cursor.moveToNext();
			}
		} catch (final Throwable e) {
			Log.e(LOG_TAG, "processCursor", e);
		}
	}

	public static void deactivate(final Cursor cursor) {
		if (cursor != null) {
			cursor.deactivate();
		}
	}
}
