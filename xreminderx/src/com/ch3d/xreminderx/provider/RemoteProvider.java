
package com.ch3d.xreminderx.provider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.ch3d.xreminderx.model.ReminderEntry;

public interface RemoteProvider {

    // TODO: Get rid of this stuff
    // public static final String SKIP = "SKIP";

    void deleteRemote(ReminderEntry reminder);

    void insertRemote(Context context, long id, Uri uri, ContentValues values);

    void remoteUpdate(Uri uri, ContentValues values);
}
