package com.ch3d.xreminderx.model;

public class NullReminderEntry extends ReminderEntry {
	public static final NullReminderEntry VALUE = new NullReminderEntry();

	NullReminderEntry()
	{
		super(-1);
	}
}
