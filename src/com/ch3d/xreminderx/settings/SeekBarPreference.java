package com.ch3d.xreminderx.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ch3d.xreminderx.utils.StringUtils;
import com.ch3d.xreminderx.utils.ViewUtils;

public abstract class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener
{
	private static final int		TEXT_SIZE	= 32;

	protected static final String	androidns	= "http://schemas.android.com/apk/res/android";

	protected static final String	reminderns	= "http://schemas.android.com/apk/res/com.ch3d.xreminderx";

	protected SeekBar				mSeekBar;

	protected TextView				mSplashText, mValueText;

	protected final Context			mContext;

	protected final String			mDialogMessage, mSuffix;

	protected int					mDefault;

	protected int					mMax		= 0;

	protected int					mMin		= 0;

	protected int					mValue		= 0;

	protected final int				mSuffixRes;

	protected final int				mDialogMessageRes;

	/**
	 * Stores value of preference that was setted when dialog is opening.
	 */
	protected int					mInitialOpenedValue;

	public SeekBarPreference(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;

		mDialogMessageRes = attrs.getAttributeResourceValue(androidns, "dialogMessage", 0);
		mDialogMessage = context.getString(mDialogMessageRes);
		mSuffixRes = attrs.getAttributeResourceValue(androidns, "text", 0);
		mSuffix = mSuffixRes == 0 ? StringUtils.EMPTY_STRING : context.getString(mSuffixRes);

		mInitialOpenedValue = mDefault;
	}

	protected abstract int getAddValue();

	protected String getValueText(final int fixedValue)
	{
		final String t = String.valueOf(fixedValue);
		final String valueText = mSuffix == null ? t : t.concat(StringUtils.SPACE_STRING).concat(mSuffix);
		return valueText;
	}

	protected int getValueTextSize()
	{
		return TEXT_SIZE;
	}

	@Override
	protected void onBindDialogView(final View v)
	{
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}

	@Override
	protected View onCreateDialogView()
	{
		LinearLayout.LayoutParams params;
		final LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		mInitialOpenedValue = getPersistedInt(mDefault);

		mSplashText = new TextView(mContext);
		if(mDialogMessage != null)
		{
			mSplashText.setText(mDialogMessage);
		}
		layout.addView(mSplashText);

		mValueText = new TextView(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(getValueTextSize());
		// #28518 - HUDm Android: Setting auto-away timeout to a minimum removes timeout value from view
		// For some reason PreferenceDialog do not invoke onProgressChanged on Galaxy Tab 1000 7"
		mValueText.setText(getValueText(mInitialOpenedValue + getAddValue()));
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(mValueText, params);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		final int progressPadding = ViewUtils.dipToPixels(mContext, 16);
		mSeekBar.setPadding(progressPadding, 0, progressPadding, 0);
		final int splashPadding = ViewUtils.dipToPixels(mContext, 4);
		mSplashText.setPadding(splashPadding, splashPadding, 0, 0);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		if(shouldPersist())
		{
			mValue = mInitialOpenedValue;
		}

		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue - getAddValue());
		return layout;
	}

	@Override
	protected void onDialogClosed(final boolean positiveResult)
	{
		if(!positiveResult)
		{
			persistInt(mInitialOpenedValue);
		}
	}

	@Override
	public void onProgressChanged(final SeekBar seek, final int value, final boolean fromTouch)
	{
		final int v = Math.max(mMin, value);
		final String valueText = getValueText(v + getAddValue());
		mValueText.setText(valueText);
		if(shouldPersist() && fromTouch)
		{
			persistInt(v);
		}
		if(fromTouch)
		{
			callChangeListener(Integer.valueOf(v));
		}
	}

	@Override
	protected void onSetInitialValue(final boolean restore, final Object defaultValue)
	{
		super.onSetInitialValue(restore, defaultValue);
		if(restore)
		{
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		}
		else
		{
			mValue = (Integer)defaultValue;
		}
	}

	@Override
	public void onStartTrackingTouch(final SeekBar seek)
	{
	}

	@Override
	public void onStopTrackingTouch(final SeekBar seek)
	{
	}
}
