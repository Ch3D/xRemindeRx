package com.ch3d.xreminderx.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import com.ch3d.xreminderx.activity.ReminderDetailsActivity;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.provider.RemindersProvider;

public class ActivityUtils
{
	public static Intent createReminderViewIntent(final Context context, final long id)
	{
		final Intent intent = new Intent(context, ReminderDetailsActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, id));
		return intent;
	}

	public static Intent getContactDetailsIntent(final Context context, final ReminderEntry reminder)
	{
		return getContactDetailsIntent(context, reminder.getContactUri());
	}

	public static Intent getContactDetailsIntent(final Context context, final Uri contactUri)
	{
		Cursor cursor = null;
		try
		{
			cursor = context.getContentResolver().query(contactUri,
					new String[] {ContactsContract.Contacts.LOOKUP_KEY}, null, null, null);
			if((cursor != null) && cursor.moveToFirst())
			{
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(
						ContactsContract.Contacts.CONTENT_LOOKUP_URI, cursor.getString(0)));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				return intent;
			}
			return null;
		}
		finally
		{
			DBUtils.close(cursor);
		}
	}

	public static boolean isJeallyBean()
	{
		return Build.VERSION.SDK_INT >= 16;
	}

	public static boolean isJeallyBeanMR1()
	{
		return Build.VERSION.SDK_INT >= 17;
	}
}
