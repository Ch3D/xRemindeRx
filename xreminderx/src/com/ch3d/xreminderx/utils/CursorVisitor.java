package com.ch3d.xreminderx.utils;

import android.database.Cursor;

public interface CursorVisitor {
	public void visit(Cursor cursor);
}
