package com.ch3d.xreminderx.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ch3d.xreminderx.view.AnimatedAlphaSpanGroup;
import com.ch3d.xreminderx.view.MutableForegroundColorSpan;

public class ViewUtils {
	private static final Property<AnimatedAlphaSpanGroup, Float> FIREWORKS_GROUP_PROGRESS_PROPERTY =
			new Property<AnimatedAlphaSpanGroup, Float>(Float.class, "FIREWORKS_GROUP_PROGRESS_PROPERTY") {

				@Override
				public Float get(final AnimatedAlphaSpanGroup spanGroup) {
					return spanGroup.getProgress();
				}

				@Override
				public void set(final AnimatedAlphaSpanGroup spanGroup, final Float value) {
					spanGroup.setProgress(value);
				}
			};

	private static final AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();

	public static void animateText(final TextView mText) {
		final SpannableString spannable = new SpannableString(mText.getText());
		final AnimatedAlphaSpanGroup spanGroup = buildFireworksSpanGroup(spannable, 0, mText.length() - 1);
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(spanGroup, FIREWORKS_GROUP_PROGRESS_PROPERTY, 0.0f, 1.0f);
		objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				mText.setText(spannable);
			}
		});
		objectAnimator.setInterpolator(sSmoothInterpolator);
		objectAnimator.setDuration(1000);
		objectAnimator.start();
	}

	public static void setVisiible(View view, boolean visible) {
		view.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	private static AnimatedAlphaSpanGroup buildFireworksSpanGroup(final SpannableString spannable, final int start, final int end) {
		final AnimatedAlphaSpanGroup group = new AnimatedAlphaSpanGroup();
		for (int index = start; index <= end; index++) {
			final MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, Color.BLACK);
			group.addSpan(span);
			spannable.setSpan(span, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		group.init();
		return group;
	}

	public static int dipToPixels(final Context context, final float dips) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final int paddingInPixels = (int) ((dips * scale) + 0.5f);
		return paddingInPixels;
	}

	public static void hideKeyboard(final TextView view) {
		if ((view == null) || (view.getContext() == null) || (view.getWindowToken() == null)) {
			return;
		}
		final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void moveCursorRight(final EditText editText) {
		editText.setSelection(editText.length());
	}

	public static void setAnimatedText(final TextView textView, final String text) {
		textView.setText(text);
		ViewUtils.animateText(textView);
	}

	public static void setVisible(final View view, final boolean visible) {
		view.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public static void showKeyboard(final TextView view) {
		if ((view == null) || (view.getContext() == null) || (view.getWindowToken() == null)) {
			return;
		}
		final InputMethodManager mgr = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

}
