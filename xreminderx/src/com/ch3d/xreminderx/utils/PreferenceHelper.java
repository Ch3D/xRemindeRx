package com.ch3d.xreminderx.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.ch3d.xreminderx.R;

public class PreferenceHelper {

	public static boolean isShowDisplayPrompt(final Context mContext) {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(Consts.PREFS.SHOW_REMINDER_REMOVE_PROMT,
				mContext.getResources().getBoolean(R.bool.show_prompt_default));
	}

	public static void setShowDisplayPrompt(final Context mContext,
			final boolean show) {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		final Editor editor = preferences.edit();
		editor.putBoolean(Consts.PREFS.SHOW_REMINDER_REMOVE_PROMT, show);
		editor.commit();
	}

}
