package com.ch3d.xreminderx.utils;

import android.content.Intent;

public class ReminderIntent extends Intent
{
	public static final String	ACTION_NEW							= "com.ch3d.xreminderx.reminder.new";

	public static final String	ACTION_NOTIFICATION_DISMISS			= "com.ch3d.xreminderx.reminder.notification.dismiss";

	public static final String	ACTION_NOTIFICATION_ONGOING			= "com.ch3d.xreminderx.reminder.notification.ongoing";

	public static final String	ACTION_NOTIFICATION_ONGOING_DISMISS	= "com.ch3d.xreminderx.reminder.notification.ongoing.dismiss";

	public static final String	ACTION_NOTIFICATION_POSTPONE		= "com.ch3d.xreminderx.reminder.notification.postpone";

	public static final String	ACTION_NOTIFICATION_SELECT			= "com.ch3d.xreminderx.reminder.notification.select";

	public static final String	ACTION_NOTIFICATION_SHOW			= "com.ch3d.xreminderx.reminder.notification.show";
}
