package com.ch3d.xreminderx.loader;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.utils.CursorVisitor;
import com.ch3d.xreminderx.utils.DBUtils;

/**
 * Created by ch3d on 04-Jun-14.
 */
public class SearchCursorWrapper extends CursorWrapper {
	private String mQuery;

	/**
	 * Creates a cursor wrapper.
	 *
	 * @param cursor The underlying cursor to wrap.
	 */
	public SearchCursorWrapper(Cursor cursor, final String query) {
		super(cursor);
		this.mQuery = query;
		DBUtils.processCursor(cursor, new CursorVisitor() {
			@Override
			public void visit(final Cursor cursor) {
				final String text = cursor.getString(RemindersContract.Indexes.TEXT);
			}
		});
	}

	@Override
	public boolean moveToNext() {
		return super.moveToNext();
	}

	@Override
	@Deprecated
	public boolean requery() {
		return false;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}
}
