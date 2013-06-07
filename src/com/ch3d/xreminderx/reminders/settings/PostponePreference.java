package com.ch3d.xreminderx.reminders.settings;

import android.content.Context;
import android.util.AttributeSet;

import com.ch3d.xreminderx.reminders.Consts;

public class PostponePreference extends SeekBarPreference
{
	public PostponePreference(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
		mDefault = Consts.POSTPONE_TIME_DEFAULT;
		mMax = Consts.POSTPONE_TIME_MAX;
		mMin = Consts.POSTPONE_TIME_MIN;
	}

	@Override
	protected int getAddValue()
	{
		return 0;
	}
}
