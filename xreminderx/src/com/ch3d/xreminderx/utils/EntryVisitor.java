package com.ch3d.xreminderx.utils;

import com.ch3d.xreminderx.model.ReminderEntry;

public interface EntryVisitor<T extends ReminderEntry> {
	public boolean visit(T entry);
}
