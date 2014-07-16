package com.ch3d.xreminderx.provider.parse;

import android.content.ContentValues;

import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.utils.StringUtils;
import com.parse.ParseObject;

/**
 * Created by ch3d on 16-Jul-14.
 */
public class ParseUtils {
	public static ParseObject createParseObject(final long rId, final ContentValues values) {
		final ParseObject obj = new ParseObject(ParseCloudProvider.PARSE_CLASS_REMINDERS);
		obj.put(RemindersContract.Columns.ID, rId);
		final String parseObjectId = values.getAsString(RemindersContract.Columns.PID);
		if (!StringUtils.isBlank(parseObjectId)) {
			obj.setObjectId(parseObjectId);
			obj.put(RemindersContract.Columns.PID, parseObjectId);
		}
		obj.put(RemindersContract.Columns.VERSION, values.getAsInteger(RemindersContract.Columns.VERSION));
		obj.put(RemindersContract.Columns.ACCOUNT, values.getAsString(RemindersContract.Columns.ACCOUNT));
		obj.put(RemindersContract.Columns.PROTOCOL, values.getAsInteger(RemindersContract.Columns.PROTOCOL));
		obj.put(RemindersContract.Columns.ALARM_TIMESTAMP, values.getAsLong(RemindersContract.Columns.ALARM_TIMESTAMP));
		obj.put(RemindersContract.Columns.COLOR, values.getAsInteger(RemindersContract.Columns.COLOR));
		obj.put(RemindersContract.Columns.CONTACT_URI, values.getAsString(RemindersContract.Columns.CONTACT_URI));
		obj.put(RemindersContract.Columns.IS_ONGOING, values.getAsBoolean(RemindersContract.Columns.IS_ONGOING));
		obj.put(RemindersContract.Columns.IS_SILENT, values.getAsBoolean(RemindersContract.Columns.IS_SILENT));
		obj.put(RemindersContract.Columns.TEXT, values.getAsString(RemindersContract.Columns.TEXT));
		obj.put(RemindersContract.Columns.TIMESTAMP, values.getAsLong(RemindersContract.Columns.TIMESTAMP));
		obj.put(RemindersContract.Columns.TYPE, values.getAsInteger(RemindersContract.Columns.TYPE));
		return obj;
	}
}
