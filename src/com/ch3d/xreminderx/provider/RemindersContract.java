package com.ch3d.xreminderx.provider;

import android.provider.BaseColumns;

public class RemindersContract
{
	public interface Columns extends BaseColumns
	{
		public static final String	PROTOCOL		= "protocol";

		public static final String	TIMESTAMP		= "ts";

		public static final String	TYPE			= "type";

		public static final String	ALARM_TIMESTAMP	= "alarm_ts";

		public static final String	TEXT			= "text";

		public static final String	CONTACT_URI		= "contactUri";

		public static final String	IS_ONGOING		= "ongoing";

		public static final String	IS_SILENT		= "silent";

		public static final String	COLOR			= "color";

	}

	public interface Indexes
	{
		public static final int	_ID				= 0;

		public static final int	PROTOCOL		= 1;

		public static final int	TYPE			= 2;

		public static final int	TIMESTAMP		= 3;

		public static final int	ALARM_TIMESTAMP	= 4;

		public static final int	TEXT			= 5;

		public static final int	CONTACT_URI		= 6;

		public static final int	IS_ONGOING		= 7;

		public static final int	IS_SILENT		= 8;

		public static final int	COLOR			= 9;
	}
}
