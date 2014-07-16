package com.ch3d.xreminderx.provider.parse;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.provider.RemoteProvider;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ParseCloudProvider implements RemoteProvider {
	public static final String PARSE_CLASS_REMINDERS = "reminders";
	private static final String LOG_TAG = ParseCloudProvider.class.getSimpleName();

	@Override
	public void deleteRemote(final ReminderEntry reminder) {
		new ParseQuery<ParseObject>(PARSE_CLASS_REMINDERS).getInBackground(reminder.getPid(), new GetCallback<ParseObject>() {
			                                                                   @Override
			                                                                   public void done(final ParseObject obj,
			                                                                                    final ParseException exc) {
				                                                                   if (exc == null) {
					                                                                   obj.deleteEventually();
				                                                                   } else {
					                                                                   Log.e(LOG_TAG, "deleteRemote", exc);
				                                                                   }
			                                                                   }
		                                                                   }
		                                                                  );
	}

	@Override
	public void insertRemote(final Context context, final long id, final Uri uri, final ContentValues values) {
		final ParseObject parseObject = ParseUtils.createParseObject(id, values);
		parseObject.saveEventually(new SaveCallback() {
			@Override
			public void done(final ParseException exc) {
				values.put(RemindersContract.Columns.PID, parseObject.getObjectId());
				context.getContentResolver().update(ContentUris.withAppendedId(uri, id), values, null, null);
			}
		});
	}

	@Override
	public void updateRemote(final Uri uri, final ContentValues values) {
		ParseUtils.createParseObject(ContentUris.parseId(uri), values).saveEventually();
	}
}
