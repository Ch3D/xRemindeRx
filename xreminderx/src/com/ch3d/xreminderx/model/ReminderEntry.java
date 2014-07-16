package com.ch3d.xreminderx.model;

import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.StringUtils;

import java.util.Calendar;

public class ReminderEntry implements Parcelable {
	public static final int DEFAULT_COLOR = Color.WHITE;
	private int color = DEFAULT_COLOR;
	public static final Parcelable.Creator<ReminderEntry> CREATOR = new Parcelable.Creator<ReminderEntry>() {
		@Override
		public ReminderEntry createFromParcel(final Parcel in) {
			return ReminderUtils.parse(in);
		}

		@Override
		public ReminderEntry[] newArray(final int size) {
			return new ReminderEntry[size];
		}
	};

	private final int id;
	private final int protocolVersion;
	private long timestamp;
	private long alarmTimestamp;
	private String text;
	private String account;
	private Uri contactUri = Uri.EMPTY;
	private ReminderType type = ReminderType.SIMPLE;
	private int ongoing;
	private int silent;
	private int version;
	/**
	 * id on remote/cloud storage
	 */
	private String pid = StringUtils.EMPTY_STRING;

	ReminderEntry(final int id) {
		this(0, id);
	}

	ReminderEntry(final int protocolVersion, final int id) {
		this.protocolVersion = protocolVersion;
		this.id = id;
	}

	ReminderEntry(final int id, final int type, final long ts, final long alarmTs, final String text) {
		this(id);
		timestamp = ts;
		alarmTimestamp = alarmTs;
		this.text = text;
	}

	public boolean isQuick() {
		return alarmTimestamp == ReminderFactory.NULL_TIMESTAMP;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(final String account) {
		this.account = account;
	}

	public long getAlarmTimestamp() {
		return alarmTimestamp;
	}

	public void setAlarmTimestamp(final long alarmTimestamp) {
		this.alarmTimestamp = alarmTimestamp;
	}

	public int getColor() {
		return color;
	}

	public void setColor(final int color) {
		this.color = color;
	}

	public Uri getContactUri() {
		return contactUri;
	}

	public void setContactUri(final Uri contactUri) {
		this.contactUri = contactUri;
	}

	public int getId() {
		return id;
	}

	public int getOutgoing() {
		return ongoing;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(final String pid) {
		this.pid = pid;
	}

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public int getSilent() {
		return silent;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public ReminderType getType() {
		return type;
	}

	public void setType(final ReminderType type) {
		this.type = type;
	}

	public int getVersion() {
		return version;
	}

	void setVersion(final int version) {
		this.version = version;
	}

	public boolean hasAccountOrRemoteId() {
		return !StringUtils.isBlank(account) || !StringUtils.isBlank(pid);
	}

	public boolean isContactRelated() {
		return (getContactUri() != null) && !getContactUri().equals(Uri.EMPTY);
	}

	public boolean isNull() {
		return this == NullReminderEntry.VALUE;
	}

	public boolean isOngoing() {
		return ReminderUtils.intToBoolean(ongoing);
	}

	public void setOngoing(final int ongoing) {
		this.ongoing = ongoing;
	}

	public void setOngoing(final boolean ongoing) {
		this.ongoing = ongoing ? 1 : 0;
	}

	public boolean isSilent() {
		return ReminderUtils.intToBoolean(silent);
	}

	public void setSilent(final int silent) {
		this.silent = silent;
	}

	public void setSilent(final boolean silent) {
		this.silent = silent ? 1 : 0;
	}

	public void postpone(final int time) {
		setAlarmTimestamp(Calendar.getInstance().getTimeInMillis() + time);
	}

	@Override
	public String toString() {
		return "Reminder [" + "id:" + getId() + " text:" + text + " timestamp:" + timestamp + "]";
	}

	@Override
	public void writeToParcel(final Parcel dest, final int protocol) {
		ReminderUtils.writeToParcel(protocol, this, dest);
	}
}
