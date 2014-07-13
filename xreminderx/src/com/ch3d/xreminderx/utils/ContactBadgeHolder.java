package com.ch3d.xreminderx.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch3d.xreminderx.R;

import static butterknife.ButterKnife.findById;
import static com.ch3d.xreminderx.utils.ViewUtils.setVisible;
import static com.ch3d.xreminderx.view.RoundedDrawableFactory.setRoundedColorDrawable;

public class ContactBadgeHolder {
	private final Activity activity;
	private final ViewStub mContactStub;
	private final Bitmap mImgDefaultAvatar;

	public ContactBadgeHolder(final Activity activity, final ViewStub view) {
		this.activity = activity;
		mImgDefaultAvatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_contact_picture);
		mContactStub = view;
	}

	public void setData(final Uri uri, final OnClickListener removeClickListener) {
		if (uri != null) {
			final TextView txtContactName = findById(activity, R.x_contact_badge.txtName);
			final Button btnContactRemove = findById(activity, R.x_contact_badge.btnRemove);

			Cursor c = null;
			try {
				c = activity.getContentResolver().query(uri, new String[]{ContactsContract.Contacts.PHOTO_ID,
						                                        ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME,
						                                        ContactsContract.Contacts.LOOKUP_KEY}, null, null, null
				                                       );

				if ((c != null) && c.moveToFirst()) {
					final int photoId = c.getInt(0);
					final String contactName = c.getString(1);

					setQuickContactData(getBitmap(photoId));
					btnContactRemove.setOnClickListener(removeClickListener);
					txtContactName.setText(contactName);
					setVisible(btnContactRemove, removeClickListener != null);
				} else {
					setQuickContactData(mImgDefaultAvatar);
					txtContactName.setText(R.string.no_contact);
					setVisible(btnContactRemove, false);
				}
			} finally {
				DBUtils.close(c);
			}
		}
	}

	public void setOnClickListener(final View.OnClickListener listener) {
		findById(activity, R.x_contact_badge.root).setOnClickListener(listener);
	}

	private void setQuickContactData(Bitmap bitmap) {
		setVisible(mContactStub, true);
		setRoundedColorDrawable((ImageView) findById(activity, R.x_contact_badge.imgAvatar), bitmap, Color.WHITE);
	}

	private Bitmap getBitmap(int photoId) {
		return ReminderUtils.fetchThumbnail(activity, photoId, mImgDefaultAvatar);
	}

	public void setVisibility(boolean visible) {
		setVisible(mContactStub, visible);
	}
}
