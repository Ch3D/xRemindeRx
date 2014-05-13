
package com.ch3d.xreminderx.utils;

import java.nio.charset.Charset;

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
import com.ch3d.xreminderx.model.ReminderColor;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.model.ReminderType;
import com.ch3d.xreminderx.notifications.AlarmReceiver;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.parse.ParseObject;

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
public class ReminderUtils
{

    private static final String NDEF_TYPE_REMINDER = "com.ch3d.xreminderx/reminder";

    public static final int FORMAT_TIME = DateUtils.FORMAT_SHOW_TIME;

    public static final int FORMAT_DATE = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY
            | DateUtils.FORMAT_SHOW_YEAR
            | DateUtils.FORMAT_ABBREV_WEEKDAY
            | DateUtils.FORMAT_ABBREV_MONTH;

    public static final int FORMAT_DATETIME = DateUtils.FORMAT_SHOW_TIME
            | DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY
            | DateUtils.FORMAT_SHOW_YEAR
            | DateUtils.FORMAT_ABBREV_WEEKDAY
            | DateUtils.FORMAT_ABBREV_MONTH;

    public static void cancelAlarm(final int id, final Context context)
    {
        final AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager
                .cancel(ReminderUtils.getPendingAlarmOperation(context, id));
    }

    public static void cancelNotification(final Context context, final int intId)
    {
        final NotificationManager notifManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(intId);
    }

    public static NdefRecord createNdefRecord(final Context context,
            final ReminderEntry entry)
    {
        final byte[] type = NDEF_TYPE_REMINDER.getBytes(Charset
                .forName("US-ASCII"));
        final byte[] id = new byte[0];
        final Parcel p = Parcel.obtain();
        entry.writeToParcel(p,
                context.getResources().getInteger(R.integer.protocol_version));
        final byte[] payload = p.marshall();

        final NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                type, id, payload);
        return mimeRecord;
    }

    public static ParseObject createParseObject(final long rId, final ContentValues values) {
        final ParseObject obj = new ParseObject("reminders");
        obj.put(RemindersContract.Columns.ID, rId);
        final String parseObjectId = values.getAsString(RemindersContract.Columns.PID);
        if (!StringUtils.isBlank(parseObjectId)) {
            obj.setObjectId(parseObjectId);
            obj.put(RemindersContract.Columns.PID, parseObjectId);
        }
        obj.put(RemindersContract.Columns.VERSION,
                values.getAsInteger(RemindersContract.Columns.VERSION));
        obj.put(RemindersContract.Columns.PROTOCOL,
                values.getAsInteger(RemindersContract.Columns.PROTOCOL));
        obj.put(RemindersContract.Columns.ALARM_TIMESTAMP,
                values.getAsLong(RemindersContract.Columns.ALARM_TIMESTAMP));
        obj.put(RemindersContract.Columns.COLOR,
                values.getAsInteger(RemindersContract.Columns.COLOR));
        obj.put(RemindersContract.Columns.CONTACT_URI,
                values.getAsString(RemindersContract.Columns.CONTACT_URI));
        obj.put(RemindersContract.Columns.IS_ONGOING,
                values.getAsBoolean(RemindersContract.Columns.IS_ONGOING));
        obj.put(RemindersContract.Columns.IS_SILENT,
                values.getAsBoolean(RemindersContract.Columns.IS_SILENT));
        obj.put(RemindersContract.Columns.TEXT, values.getAsString(RemindersContract.Columns.TEXT));
        obj.put(RemindersContract.Columns.TIMESTAMP,
                values.getAsLong(RemindersContract.Columns.TIMESTAMP));
        obj.put(RemindersContract.Columns.TYPE, values.getAsInteger(RemindersContract.Columns.TYPE));
        return obj;
    }

    public static void deleteReminder(final Context context, final int id)
    {
        context.getContentResolver().delete(
                ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, id),
                null, null);
        context.getContentResolver().notifyChange(
                RemindersProvider.REMINDERS_URI, null);

        cancelAlarm(id, context);
        cancelNotification(context, id);
    }

    public static Bitmap fetchThumbnail(final Context context,
            final int thumbnailId, final Bitmap imgDefaultAvatar)
    {
        final Uri uri = ContentUris.withAppendedId(
                ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = context.getContentResolver().query(uri,
                new String[] {
                    ContactsContract.CommonDataKinds.Photo.PHOTO
                },
                null, null, null);

        try
        {
            Bitmap thumbnail = imgDefaultAvatar;
            if (cursor.moveToFirst())
            {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null)
                {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes,
                            0, thumbnailBytes.length);
                }
            }
            return thumbnail;
        } finally
        {
            cursor.close();
        }
    }

    public static Bitmap fetchThumbnail(final Context context,
            final ReminderEntry reminder)
    {
        final Bitmap defImg = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_contact_picture);
        return fetchThumbnail(context, reminder.getContactUri(), defImg);
    }

    public static Bitmap fetchThumbnail(final Context context,
            final ReminderEntry reminder, final Bitmap defaultImg)
    {
        return fetchThumbnail(context, reminder.getContactUri(), defaultImg);
    }

    public static Bitmap fetchThumbnail(final Context context,
            final Uri contactUri, final Bitmap defaultImg)
    {
        if (contactUri != null)
        {
            Cursor c = null;
            try
            {
                c = context.getContentResolver().query(contactUri,
                        new String[] {
                            ContactsContract.Contacts.PHOTO_ID
                        },
                        null, null, null);

                if ((c != null) && c.moveToFirst())
                {
                    final int photoId = c.getInt(0);
                    return fetchThumbnail(context, photoId, defaultImg);
                }
            } finally
            {
                DBUtils.close(c);
            }
        }
        return defaultImg;
    }

    public static String formatAlarmDate(final Context context,
            final ReminderEntry mReminder)
    {
        return formatDate(context, mReminder.getAlarmTimestamp());
    }

    public static String formatAlarmTime(final Context context,
            final ReminderEntry mReminder)
    {
        return formatTime(context, mReminder.getAlarmTimestamp());
    }

    public static String formatDate(final Context context, final long ts)
    {
        return com.ch3d.xreminderx.utils.DateUtils.getDate(context, ts);
    }

    public static String formatDateTime(final Context context, final long ts)
    {
        return com.ch3d.xreminderx.utils.DateUtils.getDate2(context, ts);
    }

    public static String formatDateTimeShort(final Context context, final long ts)
    {
        final String dateTime = ReminderUtils.formatDate(context,
                ts)
                + " "
                + ReminderUtils
                        .formatTime(
                                context,
                                ts);
        return dateTime;
    }

    public static String formatTime(final Context context, final long ts)
    {
        return DateUtils.formatDateTime(context, ts, FORMAT_TIME);
    }

    public static String formatTimestmapDate(final Context context,
            final ReminderEntry mReminder)
    {
        return formatDate(context, mReminder.getTimestamp());
    }

    public static String formatTimestmapTime(final Context context,
            final ReminderEntry mReminder)
    {
        return formatTime(context, mReminder.getTimestamp());
    }

    public static ContentValues getContentValues(final ReminderEntry entry)
    {
        final ContentValues values = new ContentValues();
        values.put(RemindersContract.Columns.TEXT, entry.getText());
        values.put(RemindersContract.Columns.PROTOCOL,
                entry.getProtocolVersion());
        values.put(RemindersContract.Columns.CONTACT_URI, entry.getContactUri()
                .toString());
        values.put(RemindersContract.Columns.TIMESTAMP, entry.getTimestamp());
        values.put(RemindersContract.Columns.TYPE, entry.getType().getId());
        values.put(RemindersContract.Columns.ALARM_TIMESTAMP,
                entry.getAlarmTimestamp());
        values.put(RemindersContract.Columns.IS_ONGOING, entry.isOngoing() ? 1
                : 0);
        values.put(RemindersContract.Columns.IS_SILENT, entry.isSilent() ? 1
                : 0);
        values.put(RemindersContract.Columns.COLOR, entry.getColor());
        values.put(RemindersContract.Columns.VERSION, entry.getVersion());
        values.put(RemindersContract.Columns.PID, entry.getPid());
        return values;
    }

    public static PendingIntent getPendingAlarmOngoingOperation(
            final Context context, final long id)
    {
        final Intent nIntent = new Intent(context, AlarmReceiver.class);
        nIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_ONGOING);
        nIntent.setData(ContentUris.withAppendedId(
                RemindersProvider.REMINDERS_URI, id));
        final PendingIntent intent = PendingIntent.getBroadcast(context, 0,
                nIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return intent;
    }

    public static PendingIntent getPendingAlarmOperation(final Context context,
            final long id)
    {
        final Intent nIntent = new Intent(context, AlarmReceiver.class);
        nIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_SHOW);
        nIntent.setData(ContentUris.withAppendedId(
                RemindersProvider.REMINDERS_URI, id));
        final PendingIntent intent = PendingIntent.getBroadcast(context, 0,
                nIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return intent;
    }

    public static int getReminderItemBackground(final Context context, final int color) {
        return ReminderColor.getResId(color);
    }

    public static int getReminderTheme(final Context context, final int color) {
        return ReminderColor.getThemeId(color);
    }

    public static boolean hasAddressbookContact(final Context context,
            final ReminderEntry entry)
    {
        return hasAddressbookContact(context, entry.getContactUri());
    }

    public static boolean hasAddressbookContact(final Context context,
            final Uri contactUri)
    {
        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(contactUri,
                    new String[] {
                        ContactsContract.Contacts.PHOTO_ID
                    }, null,
                    null, null);
            if (c != null)
            {
                return c.getCount() > 0;
            }
            return false;
        } finally
        {
            DBUtils.close(c);
        }
    }

    public static boolean intToBoolean(final int value)
    {
        return value == 1;
    }

    public static ReminderEntry parse(final Cursor cursor)
    {
        cursor.moveToFirst();
        if (cursor.getCount() <= 0)
        {
            return NullReminderEntry.VALUE;
        }
        final ReminderEntry entry = ReminderFactory.create(
                cursor.getInt(RemindersContract.Indexes.PROTOCOL),
                cursor.getInt(RemindersContract.Indexes._ID),
                cursor.getInt(RemindersContract.Indexes.TYPE),
                cursor.getLong(RemindersContract.Indexes.TIMESTAMP),
                cursor.getLong(RemindersContract.Indexes.ALARM_TIMESTAMP),
                cursor.getString(RemindersContract.Indexes.TEXT),
                cursor.getString(RemindersContract.Indexes.CONTACT_URI),
                intToBoolean(cursor.getInt(RemindersContract.Indexes.IS_ONGOING)),
                intToBoolean(cursor.getInt(RemindersContract.Indexes.IS_SILENT)),
                cursor.getInt(RemindersContract.Indexes.COLOR),
                cursor.getInt(RemindersContract.Indexes.VERSION));
        entry.setPid(cursor.getString(RemindersContract.Indexes.PID));
        cursor.close();
        return entry;
    }

    public static ReminderEntry parse(final Parcel in)
    {
        final int protocol = in.readInt();
        final int id = in.readInt();
        final ReminderEntry entry = ReminderFactory.create(protocol, id);
        if (protocol >= 1)
        {
            entry.setTimestamp(in.readLong());
            entry.setAlarmTimestamp(in.readLong());
            entry.setText(in.readString());
        }
        if (protocol >= 2)
        {
            entry.setContactUri(Uri.parse(in.readString()));
            entry.setType(ReminderType.parse(in.readInt()));
        }
        if (protocol >= 3)
        {
            entry.setOngoing(intToBoolean(in.readInt()));
            entry.setSilent(intToBoolean(in.readInt()));
        }
        if (protocol >= 4)
        {
            entry.setColor(in.readInt());
        }
        return entry;
    }

    public static ReminderEntry parseReminder(final NdefRecord rec)
    {
        final byte[] payload = rec.getPayload();
        final Parcel p = Parcel.obtain();
        p.unmarshall(payload, 0, payload.length);
        p.setDataPosition(0);
        final ReminderEntry ndefReminder = ReminderEntry.CREATOR
                .createFromParcel(p);
        return ndefReminder;
    }

    public static void setAlarm(final Context context, final long id,
            final ReminderEntry entry)
    {
        final AlarmManager mAlarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (entry.isOngoing())
        {
            final PendingIntent ongoingIntent = ReminderUtils
                    .getPendingAlarmOngoingOperation(context, id);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 500, ongoingIntent);
        }
        else
        {
            mNotificationManager.cancel((int) id);
        }
        final PendingIntent intent = ReminderUtils.getPendingAlarmOperation(
                context, id);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, entry.getAlarmTimestamp(),
                intent);
    }

    public static void writeToParcel(final int protocol,
            final ReminderEntry entry, final Parcel dest)
    {
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
}
