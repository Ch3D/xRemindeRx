package com.ch3d.xreminderx.reminders.model;

import android.net.Uri;

import com.ch3d.xreminderx.reminders.utils.StringUtils;

public class ReminderFactory
{
	public static final ReminderEntry create(final int protocolVersion, final int id)
	{
		return new ReminderEntry(protocolVersion, id);
	}

	public static final ReminderEntry create(final int protocolVersion, final int id, final int type, final long ts,
			final long alarmTs, final String text, final String contactUri, final int ongoing, final int silent)
	{
		final ReminderEntry reminder = create(protocolVersion, id);
		reminder.setType(ReminderType.parse(type));
		reminder.setTimestamp(ts);
		reminder.setAlarmTimestamp(alarmTs);
		reminder.setText(text);
		reminder.setContactUri(Uri.parse(contactUri));
		reminder.setOngoing(ongoing);
		reminder.setSilent(silent);
		return reminder;
	}

	public static final ReminderEntry createNull(final long ts)
	{
		final NullReminderEntry reminder = new NullReminderEntry();
		reminder.setAlarmTimestamp(ts);
		reminder.setTimestamp(ts);
		reminder.setContactUri(Uri.EMPTY);
		reminder.setText(StringUtils.EMPTY_STRING);
		reminder.setType(ReminderType.SIMPLE);
		return reminder;
	}
}
