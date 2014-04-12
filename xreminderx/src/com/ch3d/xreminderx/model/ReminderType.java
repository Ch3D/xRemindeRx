package com.ch3d.xreminderx.model;

import java.util.HashMap;

public enum ReminderType
{
	SIMPLE(0),
	CONTACT(1);

	private static HashMap<Integer, ReminderType>	ids	= new HashMap<Integer, ReminderType>();

	public static ReminderType parse(final int value)
	{
		return ids.get(value);
	}

	private final int	id;

	static
	{
		for(final ReminderType v : values())
		{
			ids.put(v.id, v);
		}
	}

	private ReminderType(final int type)
	{
		id = type;
	}

	public int getId()
	{
		return id;
	}
}
