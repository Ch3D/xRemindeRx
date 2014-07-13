package com.ch3d.xreminderx.view;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by xCh3Dx on 14.07.2014.
 */
public class RoundedDrawableFactory {
	public static void setRoundedColorDrawable(ImageView imgView, Bitmap bitmap, int color) {
		RoundedColorDrawable drawable = new RoundedColorDrawable(bitmap, 100, 4);
		drawable.setColor(color);
		imgView.setImageDrawable(drawable);
	}

	public static void setRoundedDrawable(ImageView imgView, Bitmap bitmap, int margin) {
		RoundedDrawable drawable = new RoundedDrawable(bitmap, 100, margin);
		imgView.setImageDrawable(drawable);
	}

	public static void setRoundedDrawable(ImageView imgView, Bitmap bitmap) {
		setRoundedDrawable(imgView, bitmap, 0);
	}
}
