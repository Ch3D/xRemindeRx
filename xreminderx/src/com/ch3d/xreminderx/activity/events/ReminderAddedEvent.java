package com.ch3d.xreminderx.activity.events;

/**
 * Created by ch3d on 17-Jul-14.
 */
public class ReminderAddedEvent {
	private final int i;

	public ReminderAddedEvent(int i) {
		this.i = i;
	}

	public int getValue() {
		return i;
	}
}