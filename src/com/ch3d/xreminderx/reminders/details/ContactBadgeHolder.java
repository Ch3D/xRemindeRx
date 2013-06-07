package com.ch3d.xreminderx.reminders.details;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.reminders.provider.DBUtils;
import com.ch3d.xreminderx.reminders.utils.ReminderUtils;

public class ContactBadgeHolder
{
	private final Activity	activity;

	private final ViewStub	mContactBadge;

	private final Bitmap	mImgDefaultAvatar;

	public ContactBadgeHolder(final Activity activity, final ViewStub view)
	{
		this.activity = activity;
		mImgDefaultAvatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_contact_picture);
		mContactBadge = view;
	}

	public void setData(final Uri uri, final OnClickListener removeClickListener)
	{
		if(uri != null)
		{
			Cursor c = null;
			try
			{
				c = activity.getContentResolver().query(
						uri,
						new String[] {ContactsContract.Contacts.PHOTO_ID,
								ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME,
								ContactsContract.Contacts.LOOKUP_KEY}, null, null, null);

				if((c != null) && c.moveToFirst())
				{
					final int photoId = c.getInt(0);
					final String contactName = c.getString(1);
					final String lookupKey = c.getString(2);

					setQuickContactData(lookupKey, photoId);

					final TextView txtContactName = (TextView)activity.findViewById(R.x_contact_badge.txtName);
					final Button btnContactRemove = (Button)activity.findViewById(R.x_contact_badge.btnRemove);
					btnContactRemove.setOnClickListener(removeClickListener);
					txtContactName.setText(contactName);
				}
			}
			finally
			{
				DBUtils.close(c);
			}
		}
	}

	private void setQuickContactData(final String lookupKey, final int photoId)
	{
		mContactBadge.setVisibility(View.VISIBLE);
		final ImageView imgContactAvatar = (ImageView)activity.findViewById(R.x_contact_badge.imgAvatar);
		imgContactAvatar.setImageBitmap(ReminderUtils.fetchThumbnail(activity, photoId, mImgDefaultAvatar));
		// QuickContactBadge case
		// imgContactAvatar.setMode(QuickContact.MODE_SMALL);
		// imgContactAvatar
		// .assignContactUri(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey));
	}

	public void setVisibility(final int visibility)
	{
		mContactBadge.setVisibility(visibility);
	}

}
