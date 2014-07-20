package com.ch3d.xreminderx.view.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;

/**
 * Created by ch3d on 20-Jul-14.
 */
public class RoundedDrawableFactory {

	public static RoundedDrawable create(Bitmap bitmap) {
		return create(bitmap, RoundedDrawableHelper.CORNER_RADIUS, RoundedDrawableHelper.MARGIN_COLORED);
	}

	public static RoundedDrawable create(Bitmap bitmap, int cornerRadius, int margin) {
		return new RoundedDrawable(getBitmapShader(bitmap), cornerRadius, margin).initPaint();
	}

	public static RoundedDrawable create(Bitmap bitmap, int borderColor) {
		final BitmapShader shader = bitmap == null ? null : getBitmapShader(bitmap);
		return new RoundedColorDrawable(shader, RoundedDrawableHelper.CORNER_RADIUS, RoundedDrawableHelper.MARGIN_COLORED)
				.setColor(borderColor).initPaint();
	}

	private static BitmapShader getBitmapShader(Bitmap bitmap) {
		return new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
	}
}
