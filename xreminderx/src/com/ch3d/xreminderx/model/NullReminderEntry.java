package com.ch3d.xreminderx.model;

public class NullReminderEntry extends ReminderEntry {
	public static final NullReminderEntry VALUE = new NullReminderEntry();

	private NullReminderEntry() {
		super(-1);
	}
}
