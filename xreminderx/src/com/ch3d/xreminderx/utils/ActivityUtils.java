package com.ch3d.xreminderx.utils;

import android.app.ActivityOptions;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ch3d.xreminderx.activity.ReminderDetailsActivity;
import com.ch3d.xreminderx.adapter.RemindersAdapter;
import com.ch3d.xreminderx.adapter.RemindersAdapter.ViewHolder;
import com.ch3d.xreminderx.app.ReminderApplication;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.google.android.gms.common.api.GoogleApiClient;

public class ActivityUtils {
	public static void showKeyboard(EditText view)
	{
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		view.requestFocus();
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	public static ViewTreeObserver.OnGlobalLayoutListener createKeyboardListener(final View view, final KeyboardVisibilityListener listener)
	{
		return new ViewTreeObserver.OnGlobalLayoutListener() {

			private final Rect r = new Rect();
			private boolean wasOpened;

			@Override
			public void onGlobalLayout()
			{
				view.getWindowVisibleDisplayFrame(r);

				int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
				boolean isOpen = heightDiff > 100;
				if (isOpen == wasOpened) {
					return;
				}
				wasOpened = isOpen;
				listener.onVisibilityChanged(isOpen);
			}
		};
	}

	public static void hideKeyboard(EditText view)
	{
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
	}

	public static float convertPixelsToDp(final float px, final Context context)
	{
		final Resources resources = context.getResources();
		final DisplayMetrics metrics = resources.getDisplayMetrics();
		final float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	private static ActivityOptions getActivityOptions(final View v, final int position)
	{
		final int height = v.getHeight();
		final int width = v.getWidth();
		final boolean isFirst = position == 0;
		final int startY = isFirst ? height >> 2 : (int) (v.getY() - (height >> 1));
		final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, width >> 2, startY, width >> 1, height);
		return options;
	}

	public static Intent getContactDetailsIntent(final Context context, final ReminderEntry reminder)
	{
		return getContactDetailsIntent(context, reminder.getContactUri());
	}

	public static Intent getContactDetailsIntent(final Context context, final Uri contactUri)
	{
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.LOOKUP_KEY}, null, null, null);
			if ((cursor != null) && cursor.moveToFirst()) {
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, cursor.getString(0)));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				return intent;
			}
			return null;
		} finally {
			DBUtils.close(cursor);
		}
	}

	public static GoogleApiClient getGoogleApi(final Context context)
	{
		return ((ReminderApplication) context.getApplicationContext()).getGoogleApi();
	}

	public static boolean isJeallyBean()
	{
		return Build.VERSION.SDK_INT >= 16;
	}

	public static boolean isJeallyBeanMR1()
	{
		return Build.VERSION.SDK_INT >= 17;
	}

	public static void showToastLong(final Context context, final int resId)
	{
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	public static void showToastLong(final Context context, final String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void showToastShort(final Context context, final int resId)
	{
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	public static void showToastShort(final Context context, final String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void startDetailsActivity(final Context context, final View v, final int position)
	{
		final RemindersAdapter.ViewHolder holder = (ViewHolder) v.getTag();
		final Intent intent = ReminderDetailsActivity.newIntent(context, Intent.ACTION_VIEW, ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, holder.id));
		if (ActivityUtils.isJeallyBean()) {
			context.startActivity(intent, getActivityOptions(v, position).toBundle());
		} else {
			context.startActivity(intent);
		}
	}

	public interface KeyboardVisibilityListener {
		public void onVisibilityChanged(boolean isVisible);
	}
}
