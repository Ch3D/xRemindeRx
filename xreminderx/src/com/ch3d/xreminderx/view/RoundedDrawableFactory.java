package com.ch3d.xreminderx.view;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by xCh3Dx on 14.07.2014.
 */
public class RoundedDrawableFactory {
	public static final int CORNER_RADIUS = 100;
	public static final int MARGIN_COLORED = 4;
	public static final int MARGIN_DEFAULT = 0;

	public static void setRoundedColorDrawable(ImageView imgView, Bitmap bitmap, int color) {
		RoundedColorDrawable drawable = new RoundedColorDrawable(bitmap, CORNER_RADIUS, MARGIN_COLORED);
		drawable.setColor(color);
		imgView.setImageDrawable(drawable);
	}

	public static void setRoundedDrawable(ImageView imgView, Bitmap bitmap, int margin) {
		RoundedDrawable drawable = new RoundedDrawable(bitmap, CORNER_RADIUS, margin);
		imgView.setImageDrawable(drawable);
	}

	public static void setRoundedDrawable(ImageView imgView, Bitmap bitmap) {
		setRoundedDrawable(imgView, bitmap, MARGIN_DEFAULT);
	}
}
