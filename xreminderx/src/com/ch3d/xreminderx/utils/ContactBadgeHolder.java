package com.ch3d.xreminderx.utils;

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
import com.ch3d.xreminderx.view.RoundedDrawable;

public class ContactBadgeHolder {
	private final Activity activity;

	private final ViewStub mContactBadge;

	private final Bitmap mImgDefaultAvatar;

	public ContactBadgeHolder(final Activity activity, final ViewStub view) {
		this.activity = activity;
		mImgDefaultAvatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_contact_picture);
		mContactBadge = view;
	}

	public void setData(final Uri uri, final OnClickListener removeClickListener) {
		if (uri != null) {
			final TextView txtContactName = (TextView) activity.findViewById(R.x_contact_badge.txtName);
			final Button btnContactRemove = (Button) activity.findViewById(R.x_contact_badge.btnRemove);

			Cursor c = null;
			try {
				c = activity.getContentResolver().query(uri, new String[]{ContactsContract.Contacts.PHOTO_ID,
						                                        ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME,
						                                        ContactsContract.Contacts.LOOKUP_KEY}, null, null, null
				                                       );

				if ((c != null) && c.moveToFirst()) {
					final int photoId = c.getInt(0);
					final String contactName = c.getString(1);

					setQuickContactData(photoId);
					btnContactRemove.setOnClickListener(removeClickListener);
					txtContactName.setText(contactName);
					btnContactRemove.setVisibility(removeClickListener != null ? View.VISIBLE : View.GONE);
				} else {
					final ImageView imgContactAvatar = (ImageView) activity.findViewById(R.x_contact_badge.imgAvatar);
					imgContactAvatar.setImageBitmap(mImgDefaultAvatar);
					txtContactName.setText(R.string.no_contact);
					btnContactRemove.setVisibility(View.GONE);
				}
			} finally {
				DBUtils.close(c);
			}
		}
	}

	public void setOnClickListener(final View.OnClickListener listener) {
		activity.findViewById(R.x_contact_badge.root).setOnClickListener(listener);
	}

	private void setQuickContactData(int photoId) {
		mContactBadge.setVisibility(View.VISIBLE);
		ImageView imgContactAvatar = (ImageView) activity.findViewById(R.x_contact_badge.imgAvatar);
		Bitmap bitmap = ReminderUtils.fetchThumbnail(activity, photoId, mImgDefaultAvatar);
		RoundedDrawable.setRoundedDrawable(imgContactAvatar, bitmap);
	}

	public void setVisibility(final int visibility) {
		mContactBadge.setVisibility(visibility);
	}
}
