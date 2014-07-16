package com.ch3d.xreminderx.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.text.format.DateUtils;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.NullReminderEntry;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.model.ReminderType;
import com.ch3d.xreminderx.notifications.AlarmReceiver;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.provider.RemindersProvider;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Protocol #1<br/>
 * <li>int protocolVersion</li> <li>int id</li> <li>long ts</li> <li>long
 * alarm_ts</li> <li>String text</li> <br/>
 * <br/>
 * Protocol #2<br/>
 * <li>int protocolVersion</li> <li>int id</li> <li>long ts</li> <li>long
 * alarm_ts</li> <li>String text</li> <li><b>String contactUri</b></li> <li>
 * <b>int type</b></li><br/>
 * <br/>
 * Protocol #3<br/>
 * <li>int protocolVersion</li> <li>int id</li> <li>long ts</li> <li>long
 * alarm_ts</li> <li>String text</li> <li><b>String contactUri</b></li> <li>
 * <b>int type</b></li> <li><b>int ongoing</b></li> <li><b>int silent</b></li><br/>
 */
public class ReminderUtils {
	public static final int FORMAT_TIME = DateUtils.FORMAT_SHOW_TIME;
	private static final String NDEF_TYPE_REMINDER = "com.ch3d.xreminderx/reminder";
	protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(final byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[(j * 2) + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void cancelAlarm(final int id, final Context context) {
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(ReminderUtils.getPendingIntent(context, id));
	}

	public static void cancelNotification(final Context context, final int intId) {
		final NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancel(intId);
	}

	public static NdefRecord createNdefRecord(final Context context, final ReminderEntry entry) {
		final byte[] type = NDEF_TYPE_REMINDER.getBytes(Charset.forName("US-ASCII"));
		final byte[] id = new byte[0];
		final Parcel p = Parcel.obtain();
		entry.writeToParcel(p, context.getResources().getInteger(R.integer.protocol_version));
		final byte[] payload = p.marshall();
		return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, type, id, payload);
	}

	public static void deleteReminder(final Context context, final int id) {
		context.getContentResolver().delete(ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, id), null, null);
		context.getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);

		cancelAlarm(id, context);
		cancelNotification(context, id);
	}

	public static Bitmap fetchThumbnail(final Context context, final int thumbnailId, final Bitmap imgDefaultAvatar) {
		final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
		final Cursor cursor =
				context.getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

		try {
			Bitmap thumbnail = imgDefaultAvatar;
			if (cursor.moveToFirst()) {
				final byte[] thumbnailBytes = cursor.getBlob(0);
				if (thumbnailBytes != null) {
					thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
				}
			}
			return thumbnail;
		} finally {
			cursor.close();
		}
	}

	public static Bitmap fetchThumbnail(final Context context, final ReminderEntry reminder) {
		final Bitmap defImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_contact_picture);
		return fetchThumbnail(context, reminder.getContactUri(), defImg);
	}

	public static Bitmap fetchThumbnail(final Context context, final ReminderEntry reminder, final Bitmap defaultImg) {
		return fetchThumbnail(context, reminder.getContactUri(), defaultImg);
	}

	public static Bitmap fetchThumbnail(final Context context, final Uri contactUri, final Bitmap defaultImg) {
		if (contactUri != null) {
			Cursor c = null;
			try {
				c = context.getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.PHOTO_ID}, null, null, null);

				if ((c != null) && c.moveToFirst()) {
					final int photoId = c.getInt(0);
					return fetchThumbnail(context, photoId, defaultImg);
				}
			} finally {
				DBUtils.close(c);
			}
		}
		return defaultImg;
	}

	public static String formatAlarmDate(final Context context, final ReminderEntry mReminder) {
		return formatDate(context, mReminder.getAlarmTimestamp());
	}

	public static String formatAlarmTime(final Context context, final ReminderEntry mReminder) {
		return formatTime(context, mReminder.getAlarmTimestamp());
	}

	public static String formatDate(final Context context, final long ts) {
		return com.ch3d.xreminderx.utils.DateUtils.getDate(context, ts);
	}

	public static String formatDateTime(final Context context, final long ts) {
		return com.ch3d.xreminderx.utils.DateUtils.getDate2(context, ts);
	}

	public static String formatDateTimeShort(final Context context, final long ts) {
		return ReminderUtils.formatDate(context, ts) + " " + ReminderUtils.formatTime(context, ts);
	}

	public static String formatTime(final Context context, final long ts) {
		return DateUtils.formatDateTime(context, ts, FORMAT_TIME);
	}

	public static String formatTimestmapDate(final Context context, final ReminderEntry mReminder) {
		return formatDate(context, mReminder.getTimestamp());
	}

	public static String formatTimestmapTime(final Context context, final ReminderEntry mReminder) {
		return formatTime(context, mReminder.getTimestamp());
	}

	public static ContentValues getContentValues(final ReminderEntry entry) {
		final ContentValues values = new ContentValues();
		values.put(RemindersContract.Columns.TEXT, entry.getText());
		values.put(RemindersContract.Columns.PROTOCOL, entry.getProtocolVersion());
		values.put(RemindersContract.Columns.CONTACT_URI, entry.getContactUri().toString());
		values.put(RemindersContract.Columns.TIMESTAMP, entry.getTimestamp());
		values.put(RemindersContract.Columns.TYPE, entry.getType().getId());
		values.put(RemindersContract.Columns.ALARM_TIMESTAMP, entry.getAlarmTimestamp());
		values.put(RemindersContract.Columns.IS_ONGOING, entry.isOngoing() ? 1 : 0);
		values.put(RemindersContract.Columns.IS_SILENT, entry.isSilent() ? 1 : 0);
		values.put(RemindersContract.Columns.COLOR, entry.getColor());
		values.put(RemindersContract.Columns.VERSION, entry.getVersion());
		values.put(RemindersContract.Columns.PID, entry.getPid());
		values.put(RemindersContract.Columns.ACCOUNT, entry.getAccount());
		return values;
	}

	public static PendingIntent getPendingIntentOngoing(final Context context, final long id) {
		final Intent nIntent = new Intent(context, AlarmReceiver.class);
		nIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_ONGOING);
		nIntent.setData(ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, id));
		return PendingIntent.getBroadcast(context, 0, nIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public static PendingIntent getPendingIntent(final Context context, final long id) {
		final Intent nIntent = new Intent(context, AlarmReceiver.class);
		nIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_SHOW);
		nIntent.setData(ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, id));
		return PendingIntent.getBroadcast(context, 0, nIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public static boolean hasAddressbookContact(final Context context, final ReminderEntry entry) {
		return hasAddressbookContact(context, entry.getContactUri());
	}

	public static boolean hasAddressbookContact(final Context context, final Uri contactUri) {
		Cursor c = null;
		try {
			c = context.getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.PHOTO_ID}, null, null, null);
			return c != null && c.getCount() > 0;
		} finally {
			DBUtils.close(c);
		}
	}

	public static boolean intToBoolean(final int value) {
		return value == 1;
	}

	public static ReminderEntry parse(final Cursor cursor) {
		cursor.moveToFirst();
		if (cursor.getCount() <= 0) {
			return NullReminderEntry.VALUE;
		}
		final ReminderEntry entry = ReminderFactory
				.create(cursor.getInt(RemindersContract.Indexes.PROTOCOL), cursor.getInt(RemindersContract.Indexes._ID),
				        cursor.getInt(RemindersContract.Indexes.TYPE), cursor.getLong(RemindersContract.Indexes.TIMESTAMP),
				        cursor.getLong(RemindersContract.Indexes.ALARM_TIMESTAMP), cursor.getString(RemindersContract.Indexes.TEXT),
				        cursor.getString(RemindersContract.Indexes.CONTACT_URI),
				        intToBoolean(cursor.getInt(RemindersContract.Indexes.IS_ONGOING)),
				        intToBoolean(cursor.getInt(RemindersContract.Indexes.IS_SILENT)), cursor.getInt(RemindersContract.Indexes.COLOR),
				        cursor.getInt(RemindersContract.Indexes.VERSION));
		entry.setPid(cursor.getString(RemindersContract.Indexes.PID));
		entry.setAccount(cursor.getString(RemindersContract.Indexes.ACCOUNT));
		cursor.close();
		return entry;
	}

	public static ReminderEntry parse(final Parcel in) {
		final int protocol = in.readInt();
		final int id = in.readInt();
		final ReminderEntry entry = ReminderFactory.create(protocol, id);
		if (protocol >= 1) {
			entry.setTimestamp(in.readLong());
			entry.setAlarmTimestamp(in.readLong());
			entry.setText(in.readString());
		}
		if (protocol >= 2) {
			entry.setContactUri(Uri.parse(in.readString()));
			entry.setType(ReminderType.parse(in.readInt()));
		}
		if (protocol >= 3) {
			entry.setOngoing(intToBoolean(in.readInt()));
			entry.setSilent(intToBoolean(in.readInt()));
		}
		if (protocol >= 4) {
			entry.setColor(in.readInt());
		}
		return entry;
	}

	public static ReminderEntry parseReminder(final NdefRecord rec) {
		final byte[] payload = rec.getPayload();
		final Parcel p = Parcel.obtain();
		p.unmarshall(payload, 0, payload.length);
		p.setDataPosition(0);
		return ReminderEntry.CREATOR.createFromParcel(p);
	}

	public static void setAlarm(final Context context, final long id, final ReminderEntry entry) {
		final AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (entry.isOngoing()) {
			if (!entry.isQuick()) {
				mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, getPendingIntentOngoing(context, id));
			}
		} else {
			mNotificationManager.cancel((int) id);
		}
		if (!entry.isQuick()) {
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, entry.getAlarmTimestamp(), getPendingIntent(context, id));
		}
	}

	public static String sha1Hash(final String toHash) {
		String hash = null;
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = toHash.getBytes("UTF-8");
			digest.update(bytes, 0, bytes.length);
			bytes = digest.digest();

			// This is ~55x faster than looping and String.formating()
			hash = bytesToHex(bytes);
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hash;
	}

	public static void writeToParcel(final int protocol, final ReminderEntry entry, final Parcel dest) {
		dest.writeInt(protocol);
		dest.writeInt(entry.getId());
		dest.writeLong(entry.getTimestamp());
		dest.writeLong(entry.getAlarmTimestamp());
		dest.writeString(entry.getText());
		// added in protocol = 2
		dest.writeString(entry.getContactUri().toString());
		dest.writeInt(entry.getType().getId());
		// added in protocol = 3
		dest.writeInt(entry.getOutgoing());
		dest.writeInt(entry.getSilent());
		// added in protocol = 4
		dest.writeInt(entry.getColor());
	}

	public static String getFormattedDateTime(Context context, long timestamp) {
		return getFormattedDate(context, timestamp) + " " + getFormattedTime(context, timestamp);
	}

	public static String getFormattedDate(Context context, long timestamp) {
		return formatDate(context, timestamp);
	}

	public static String getFormattedTime(Context context, long timestamp) {
		return formatTime(context, timestamp);
	}
}
