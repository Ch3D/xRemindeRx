package com.ch3d.xreminderx.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ch3d.xreminderx.model.NullReminderEntry;
import com.ch3d.xreminderx.model.ReminderEntry;

import java.util.ArrayList;
import java.util.Collection;

public abstract class EntryCursor implements Cursor {
	private static final String LOG_TAG = "EntryCursor";
	protected final Context mContext;
	private final Collection<ReminderEntry> mItems;
	protected int position = 0;
	protected ArrayList<ReminderEntry> entries;
	private EntryVisitor<ReminderEntry> mfilter;
	private boolean mActive;

	public EntryCursor(final Collection<ReminderEntry> items, final EntryVisitor<ReminderEntry>
			filter) {
		mfilter = filter;
		mItems = items;
		mContext = null;
		fillData(filter);
	}

	public EntryCursor(final Collection<ReminderEntry> items) {
		this(items, null);
	}

	@Override
	public void close() {
		// entries.clear();
	}

	@Override
	public void copyStringToBuffer(final int arg0, final CharArrayBuffer arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		mActive = false;
		entries.clear();
	}

	protected void fillData(final EntryVisitor<ReminderEntry> filter) {
		try {
			if (filter == null) {
				entries = new ArrayList<ReminderEntry>(mItems);
				return;
			}
			entries = filterItems(filter);
		} finally {
			mActive = true;
		}
	}

	private ArrayList<ReminderEntry> filterItems(EntryVisitor<ReminderEntry> filter) {
		final ArrayList<ReminderEntry> result = new ArrayList<ReminderEntry>();
		for (final ReminderEntry item : mItems) {
			if (filter.visit(item)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public byte[] getBlob(final int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnIndex(final String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnIndexOrThrow(final String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnName(final int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		if (!mActive) {
			return 0;
		}
		return entries.size();
	}

	@Override
	public double getDouble(final int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ReminderEntry getEntry() {
		if (!mActive) {
			return NullReminderEntry.VALUE;
		}
		if (getCount() == position) {
			position--;
		}
		if (position < 0) {
			Log.e(LOG_TAG, "Cannot get entry for position = " + position);
			return NullReminderEntry.VALUE;
		}
		return entries.get(position);
	}

	public ReminderEntry getEntry(final int pos) {
		return entries.get(pos);
	}

	@Override
	public Bundle getExtras() {
		return new Bundle();
	}

	@Override
	public float getFloat(final int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(final int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(final int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPosition() {
		if (!mActive) {
			return 0;
		}
		return position;
	}

	@Override
	public short getShort(final int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getString(final int arg0) {
		return StringUtils.EMPTY_STRING;
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAfterLast() {
		if (position == entries.size()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isBeforeFirst() {
		if (position < 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFirst() {
		return position == 0;
	}

	@Override
	public boolean isLast() {
		return position == (entries.size() - 1);
	}

	@Override
	public boolean isNull(final int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean move(final int arg0) {
		if (!mActive) {
			return false;
		}
		position = arg0;
		return true;
	}

	@Override
	public boolean moveToFirst() {
		if (!mActive) {
			return false;
		}
		position = 0;
		return true;
	}

	@Override
	public boolean moveToLast() {
		if (!mActive) {
			return false;
		}
		position = entries.size();
		return true;
	}

	@Override
	public boolean moveToNext() {
		if (!mActive) {
			return false;
		}
		position++;
		return true;
	}

	@Override
	public boolean moveToPosition(final int pos) {
		if (!mActive) {
			return false;
		}
		position = pos;
		return true;
	}

	@Override
	public boolean moveToPrevious() {
		if (!mActive) {
			return false;
		}
		position--;
		return true;
	}

	@Override
	public void registerContentObserver(final ContentObserver arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerDataSetObserver(final DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requery() {
		entries.clear();
		fillData(mfilter);
		return true;
	}

	@Override
	public Bundle respond(final Bundle arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// FIXME
//	public void search(final SearchEntryVisitor<ReminderEntry> searchVisitor) {
//		if (!mActive) {
//			return;
//		}
//		if (StringUtils.isBlank(searchVisitor.getQuery())) {
//			fillData(filter);
//		} else {
//			fillData(searchVisitor);
//		}
//	}

	@Override
	public void setNotificationUri(final ContentResolver arg0, final Uri arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterContentObserver(final ContentObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(final DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType(final int columnIndex) {
		return 0;
	}

}
