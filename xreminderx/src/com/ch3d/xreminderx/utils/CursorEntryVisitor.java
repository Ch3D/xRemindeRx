package com.ch3d.xreminderx.utils;

import android.database.Cursor;

public interface CursorEntryVisitor<E> {
	public E visit(Cursor cursor);
}
