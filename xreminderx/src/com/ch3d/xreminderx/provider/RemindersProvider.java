
package com.ch3d.xreminderx.provider;

import javax.inject.Inject;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.ch3d.xreminderx.BuildConfig;
import com.ch3d.xreminderx.app.ReminderApplication;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.StringUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import dagger.Lazy;

public class RemindersProvider extends ContentProvider
{
    private static final String PATH_REMINDERS = "reminders";

    private static final String PATH_SEARCH = "search";

    private static final String AUTHORITY = BuildConfig.PROVIDER_AUTHORITY;

    public static final Uri URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri REMINDERS_URI = Uri.withAppendedPath(URI, PATH_REMINDERS);

    public static final Uri REMINDERS_SEARCH = Uri.withAppendedPath(URI, PATH_SEARCH);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int TODAY = 1;

    private static final int ALL = 2;

    private static final int REMINDERS = 3;

    private static final int SEARCH = 4;

    @Inject
    Lazy<RemoteProvider> mRemoteProvider;

    private ReminderDBHelper mDbHelper;

    static
    {
        sUriMatcher.addURI(AUTHORITY, PATH_REMINDERS + "/all", ALL);
        sUriMatcher.addURI(AUTHORITY, PATH_REMINDERS + "/today", TODAY);
        sUriMatcher.addURI(AUTHORITY, PATH_REMINDERS + "/#", REMINDERS);
        sUriMatcher.addURI(AUTHORITY, PATH_SEARCH + "/*", SEARCH);
    }

    public static Uri addReminder(final Context context, final ReminderEntry reminder)
    {
        return addReminder(context, reminder, false);
    }

    public static Uri addReminder(final Context context, final ReminderEntry reminder,
            final boolean setUpAlarm)
    {
        final Uri result = context.getContentResolver().insert(RemindersProvider.REMINDERS_URI,
                ReminderUtils.getContentValues(reminder));
        context.getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);
        if (setUpAlarm)
        {
            ReminderUtils.setAlarm(context, ContentUris.parseId(result), reminder);
        }
        return result;
    }

    public static int updateReminder(final Context context, final ReminderEntry reminder)
    {
        return updateReminder(context, reminder, false);
    }

    public static int updateReminder(final Context context, final ReminderEntry reminder,
            final boolean notify)
    {
        final ContentValues values = ReminderUtils.getContentValues(reminder);
        final Uri uri = ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI,
                reminder.getId());
        final int result = context.getContentResolver().update(uri, values, null, null);
        if (notify)
        {
            context.getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);
        }
        return result;
    }

    private void addAccount(final ContentValues values) {
        final String accountName = Plus.AccountApi.getAccountName(getGoogleApi());
        values.put(RemindersContract.Columns.ACCOUNT, ReminderUtils.sha1Hash(accountName));
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs)
    {
        int rowsAffected = 0;

        final Cursor query = query(uri, null, null, null, null);
        final ReminderEntry reminder = ReminderUtils.parse(query);

        if (reminder != null)
        {
            if (StringUtils.isBlank(selection) || (selectionArgs.length == 0)) {
                rowsAffected = mDbHelper.deleteReminderForId(ContentUris.parseId(uri));
            } else {
                rowsAffected = mDbHelper.deleteReminder(selection, selectionArgs);
            }
            if ((rowsAffected > 0) && reminder.hasAccountOrRemoteId() && isGoogleApiConnected())
            {
                mRemoteProvider.get().deleteRemote(reminder);
            }
        }
        return rowsAffected;
    }

    private GoogleApiClient getGoogleApi() {
        return ActivityUtils.getGoogleApi(getContext());
    }

    @Override
    public String getType(final Uri uri)
    {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values)
    {
        final long id = mDbHelper.getWritableDatabase().insert(PATH_REMINDERS,
                StringUtils.EMPTY_STRING, values);
        if ((id > 0) && isGoogleApiConnected())
        {
            addAccount(values);
            mRemoteProvider.get().insertRemote(getContext(), id, uri, values);
        }
        return ContentUris.withAppendedId(REMINDERS_URI, id);
    }

    private boolean isGoogleApiConnected() {
        return getGoogleApi().isConnected();
    }

    @Override
    public boolean onCreate()
    {
        mDbHelper = new ReminderDBHelper(getContext());
        ((ReminderApplication) getContext().getApplicationContext()).inject(this);
        return true;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
            final String[] selectionArgs,
            final String sortOrder)
    {
        switch (sUriMatcher.match(uri))
        {
            case ALL:
                return mDbHelper.queryAll();

            case TODAY:
                return mDbHelper.queryToday();

            case SEARCH:
                return mDbHelper.query(uri, projection, "text LIKE '%" + uri.getLastPathSegment()
                        + "%'",
                        selectionArgs, sortOrder);

            case REMINDERS:
                return mDbHelper.queryReminder(ContentUris.parseId(uri));
        }
        return mDbHelper.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
            final String[] selectionArgs)
    {
        final int rowsAffected = mDbHelper.updateReminder(uri, values, selection, selectionArgs);
        if ((rowsAffected > 0) && isGoogleApiConnected()
                && values.containsKey(RemindersContract.Columns.PID)
                && !StringUtils.isBlank(values.getAsString(RemindersContract.Columns.PID)))
        {
            addAccount(values);
            mRemoteProvider.get().updateRemote(uri, values);
        }
        return rowsAffected;
    }

}
