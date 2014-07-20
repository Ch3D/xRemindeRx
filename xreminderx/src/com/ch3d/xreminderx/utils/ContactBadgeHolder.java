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
import com.ch3d.xreminderx.model.ReminderEntry;

import static butterknife.ButterKnife.findById;
import static com.ch3d.xreminderx.utils.ViewUtils.setVisible;
import static com.ch3d.xreminderx.view.drawable.RoundedDrawableHelper.setRoundedColorDrawable;

public class ContactBadgeHolder {
	private final Activity activity;
	private final ViewStub mContactStub;
	private final Bitmap mImgDefaultAvatar;
	private OnClickListener mRemoveClickListener;
	private ReminderEntry mReminder;

	public ContactBadgeHolder(final Activity activity, final ViewStub view, ReminderEntry reminder) {
		this.activity = activity;
		mReminder = reminder;
		mImgDefaultAvatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_contact_picture);
		mContactStub = view;
	}

	public void setOnRemoveListener(final OnClickListener removeClickListener) {
		mRemoveClickListener = removeClickListener;
	}

	public void clearData() {
		setQuickContactData(mImgDefaultAvatar, mReminder.getColor());
		TextView txtContactName = findById(activity, R.x_contact_badge.txtName);
		Button btnContactRemove = findById(activity, R.x_contact_badge.btnRemove);
		txtContactName.setText(R.string.no_contact);
		setVisible(btnContactRemove, false);
	}

	public void refresh() {
		updateData(mReminder);
	}

	public void updateData(ReminderEntry reminder) {
		mReminder = reminder;
		if (!reminder.isContactRelated()) {
			setQuickContactData(mImgDefaultAvatar, mReminder.getColor());
			TextView txtContactName = findById(activity, R.x_contact_badge.txtName);
			Button btnContactRemove = findById(activity, R.x_contact_badge.btnRemove);
			txtContactName.setText(R.string.no_contact);
			setVisible(btnContactRemove, false);
			return;
		}
		setData(reminder.getContactUri(), reminder.getColor());
	}

	public void setData(Uri uri, int color) {
		Cursor c = null;
		try {
			TextView txtContactName = findById(activity, R.x_contact_badge.txtName);
			Button btnContactRemove = findById(activity, R.x_contact_badge.btnRemove);

			c = activity.getContentResolver().query(uri, new String[]{ContactsContract.Contacts.PHOTO_ID,
					                                        ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME,
					                                        ContactsContract.Contacts.LOOKUP_KEY}, null, null, null
			                                       );

			if ((c != null) && c.moveToFirst()) {
				final int photoId = c.getInt(0);
				final String contactName = c.getString(1);

				setQuickContactData(getBitmap(photoId), color);
				btnContactRemove.setOnClickListener(mRemoveClickListener);
				txtContactName.setText(contactName);
				setVisible(btnContactRemove, mRemoveClickListener != null);
			} else {
				setQuickContactData(mImgDefaultAvatar, color);
				txtContactName.setText(R.string.no_contact);
				setVisible(btnContactRemove, false);
			}
		} finally {
			DBUtils.close(c);
		}
	}

	public void setOnClickListener(final View.OnClickListener listener) {
		findById(activity, R.x_contact_badge.root).setOnClickListener(listener);
	}

	private void setQuickContactData(Bitmap bitmap, int color) {
		setVisible(mContactStub, true);
		setRoundedColorDrawable((ImageView) findById(activity, R.x_contact_badge.imgAvatar), bitmap, color);
	}

	private Bitmap getBitmap(int photoId) {
		return ReminderUtils.fetchThumbnail(activity, photoId, mImgDefaultAvatar);
	}

	public void setVisibility(boolean visible) {
		setVisible(mContactStub, visible);
	}
}
