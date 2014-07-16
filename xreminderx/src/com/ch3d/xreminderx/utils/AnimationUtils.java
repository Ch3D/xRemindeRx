package com.ch3d.xreminderx.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;

import com.ch3d.xreminderx.activity.RemindersActivity;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

/**
 * Created by ch3d on 16-Jul-14.
 */
public class AnimationUtils {
	public static void animateButton(final ImageButton btn, final boolean visible) {
		final int visibility = visible ? View.VISIBLE : View.GONE;
		if (visible) {
			btn.setVisibility(visibility);
		}
		btn.animate().setDuration(RemindersActivity.ANIMATION_DURATION).
				setInterpolator(new AccelerateInterpolator()).
				alpha(visible ? 1 : 0).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				btn.setVisibility(visibility);
			}
		}).start();
	}

	public static void animateEditText(final FloatLabeledEditText floatView, final View anchor, final boolean visible) {
		ValueAnimator widthAnimator = null;
		floatView.setVisibility(View.VISIBLE);
		if (!visible) {
			int[] location = new int[2];
			anchor.getLocationOnScreen(location);
			widthAnimator = ValueAnimator.ofInt(location[0], 0);
		} else {
			int[] location = new int[2];
			anchor.getLocationOnScreen(location);
			widthAnimator = ValueAnimator.ofInt(0, location[0]);
		}

		widthAnimator.setInterpolator(new AccelerateInterpolator());
		widthAnimator.setDuration(RemindersActivity.ANIMATION_DURATION);
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				floatView.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				floatView.requestLayout();
			}
		});
		widthAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (visible) {
					// ActivityUtils.showKeyboard(mEditQuickText.getEditText());
				} else {
					ActivityUtils.hideKeyboard(floatView.getEditText());
				}
			}
		});
		widthAnimator.start();
	}
}
