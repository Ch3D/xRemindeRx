package com.ch3d.xreminderx.model;

import android.graphics.Color;

public enum ReminderColor {
	WHITE(Color.WHITE),
	BLUE(Color.BLUE),
	GREEN(Color.GREEN),
	RED(Color.RED),
	YELLOW(Color.YELLOW);

	private final int color;

	private ReminderColor(final int color)
	{
		this.color = color;
	}

	public int getColor()
	{
		return color;
	}
}
