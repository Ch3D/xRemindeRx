
package com.ch3d.xreminderx.model;

import java.util.Calendar;

import android.net.Uri;

import com.ch3d.xreminderx.utils.StringUtils;

public class ReminderFactory
{
    public static final ReminderEntry create(final int protocolVersion,
            final int id)
    {
        return new ReminderEntry(protocolVersion, id);
    }

    public static final ReminderEntry create(final int protocolVersion,
            final int id, final int type, final long ts, final long alarmTs,
            final String text, final String contactUri, final boolean ongoing,
            final boolean silent)
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

    public static final ReminderEntry create(final int protocolVersion,
            final int id, final int type, final long ts, final long alarmTs,
            final String text, final String contactUri, final boolean ongoing,
            final boolean silent, final int color, final int version)
    {
        final ReminderEntry reminder = create(protocolVersion, id, type, ts, alarmTs, text,
                contactUri, ongoing, silent);
        reminder.setColor(color);
        reminder.setVersion(version);
        return reminder;
    }

    public static final ReminderEntry createNew() {
        final ReminderEntry reminder = createNull();
        reminder.setVersion(1);
        return reminder;
    }

    public static final ReminderEntry createNull()
    {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        final long ts = c.getTimeInMillis();

        final NullReminderEntry reminder = new NullReminderEntry();
        reminder.setAlarmTimestamp(ts);
        reminder.setTimestamp(ts);
        reminder.setContactUri(Uri.EMPTY);
        reminder.setText(StringUtils.EMPTY_STRING);
        reminder.setType(ReminderType.SIMPLE);
        return reminder;
    }
}