
package com.ch3d.xreminderx.model;

import java.util.Calendar;

import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.ch3d.xreminderx.utils.ReminderUtils;

public class ReminderEntry implements Parcelable
{
    public static final int                               DEFAULT_COLOR = Color.WHITE;

    public static final Parcelable.Creator<ReminderEntry> CREATOR       = new Parcelable.Creator<ReminderEntry>()
                                                                        {
                                                                            @Override
                                                                            public ReminderEntry createFromParcel(
                                                                                    final Parcel in)
                                                                            {
                                                                                return ReminderUtils
                                                                                        .parse(in);
                                                                            }

                                                                            @Override
                                                                            public ReminderEntry[] newArray(
                                                                                    final int size)
                                                                            {
                                                                                return new ReminderEntry[size];
                                                                            }
                                                                        };

    private final int                                     id;

    private long                                          timestamp;

    private long                                          alarmTimestamp;

    private String                                        text;

    private Uri                                           contactUri    = Uri.EMPTY;

    private ReminderType                                  type          = ReminderType.SIMPLE;

    private final int                                     protocolVersion;

    private int                                           ongoing;

    private int                                           silent;

    private int                                           color         = DEFAULT_COLOR;

    ReminderEntry(final int id)
    {
        this(0, id);
    }

    ReminderEntry(final int protocolVersion, final int id)
    {
        this.protocolVersion = protocolVersion;
        this.id = id;
    }

    ReminderEntry(final int id, final int type, final long ts, final long alarmTs, final String text)
    {
        this(id);
        timestamp = ts;
        alarmTimestamp = alarmTs;
        this.text = text;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public long getAlarmTimestamp()
    {
        return alarmTimestamp;
    }

    public int getColor()
    {
        return color;
    }

    public Uri getContactUri()
    {
        return contactUri;
    }

    public int getId()
    {
        return id;
    }

    public int getOutgoing()
    {
        return ongoing;
    }

    public int getProtocolVersion()
    {
        return protocolVersion;
    }

    public int getSilent()
    {
        return silent;
    }

    public String getText()
    {
        return text;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public ReminderType getType()
    {
        return type;
    }

    public boolean isContactRelated()
    {
        return (getContactUri() != null) && !getContactUri().equals(Uri.EMPTY);
    }

    public boolean isNull()
    {
        return this == NullReminderEntry.VALUE;
    }

    public boolean isOngoing()
    {
        return ReminderUtils.intToBoolean(ongoing);
    }

    public boolean isSilent()
    {
        return ReminderUtils.intToBoolean(silent);
    }

    public void postpone(final int time)
    {
        setAlarmTimestamp(Calendar.getInstance().getTimeInMillis() + time);
    }

    public void setAlarmTimestamp(final long alarmTimestamp)
    {
        this.alarmTimestamp = alarmTimestamp;
    }

    public void setColor(final int color)
    {
        this.color = color;
    }

    public void setContactUri(final Uri contactUri)
    {
        this.contactUri = contactUri;
    }

    public void setOngoing(final boolean ongoing)
    {
        this.ongoing = ongoing ? 1 : 0;
    }

    public void setSilent(final boolean silent)
    {
        this.silent = silent == true ? 1 : 0;
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    public void setTimestamp(final long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setType(final ReminderType type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Reminder [" + "id:" + getId() + " text:" + text + " timestamp:" + timestamp + "]";
    }

    @Override
    public void writeToParcel(final Parcel dest, final int protocol)
    {
        ReminderUtils.writeToParcel(protocol, this, dest);
    }
}
