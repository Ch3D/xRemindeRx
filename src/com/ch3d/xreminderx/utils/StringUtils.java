package com.ch3d.xreminderx.utils;

public class StringUtils
{

	public static final String	EMPTY_STRING	= "";

	public static final String	SPACE_STRING	= " ";

	public static boolean isBlank(final String string)
	{
		if((string == null) || (string == EMPTY_STRING) || (string.length() == 0))
		{
			return true;
		}
		return false;
	}

}
