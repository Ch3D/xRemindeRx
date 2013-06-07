package com.ch3d.xreminderx.reminders;

public class Consts
{
	public static class PREFS
	{
		public static final String	SHOW_REMINDER_REMOVE_PROMT	= Consts.PREFERENCES + ".remove_reminder_promt";

		public static final String	SHOW_REMINDER_POSTPONE_TIME	= Consts.PREFERENCES + ".postpone_time";
	}

	public static final String	PREFERENCES				= "com.ch3d.xreminderx.preferences";

	public static final int		PROTOCOL_VERSION		= 1;

	public static final int		POSTPONE_TIME_DEFAULT	= 10;

	public static final int		POSTPONE_TIME_MIN		= 1;

	public static final int		POSTPONE_TIME_MAX		= 120;
}
