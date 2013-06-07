package com.ch3d.xreminderx.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class ViewUtils
{
	public static int dipToPixels(final Context context, final float dips)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		final int paddingInPixels = (int)((dips * scale) + 0.5f);
		return paddingInPixels;
	}

	public static void hideKeyboard(final TextView view)
	{
		if((view == null) || (view.getContext() == null) || (view.getWindowToken() == null))
		{
			return;
		}
		final InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		if(imm.isActive())
		{
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void showKeyboard(final TextView view)
	{
		if((view == null) || (view.getContext() == null) || (view.getWindowToken() == null))
		{
			return;
		}
		final InputMethodManager mgr = (InputMethodManager)view.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

}
