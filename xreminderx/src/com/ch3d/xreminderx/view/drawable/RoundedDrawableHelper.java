package com.ch3d.xreminderx.view.drawable;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by xCh3Dx on 14.07.2014.
 */
public class RoundedDrawableHelper {
	public static final int CORNER_RADIUS = 100;
	public static final int MARGIN_COLORED = 4;
	public static final int MARGIN_DEFAULT = 0;

	public static void setRoundedColorDrawable(ImageView imgView, Bitmap bitmap, int color) {
		imgView.setImageDrawable(RoundedDrawableFactory.create(bitmap, color));
	}

	public static void setRoundedDrawable(ImageView imgView, Bitmap bitmap, int margin) {
		imgView.setImageDrawable(RoundedDrawableFactory.create(bitmap, CORNER_RADIUS, margin));
	}

	public static void setRoundedDrawable(ImageView imgView, Bitmap bitmap) {
		setRoundedDrawable(imgView, bitmap, MARGIN_DEFAULT);
	}
}
