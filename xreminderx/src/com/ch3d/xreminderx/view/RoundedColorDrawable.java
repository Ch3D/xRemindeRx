package com.ch3d.xreminderx.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by xCh3Dx on 14.07.2014.
 */
public class RoundedColorDrawable extends RoundedDrawable {
	private int color;
	private RectF mRect = new RectF();

	protected RoundedColorDrawable(Bitmap bitmap, float cornerRadius, int margin) {
		super(bitmap, cornerRadius, margin);
	}

	public RoundedColorDrawable setColor(int color) {
		this.color = color;
		return this;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mRect.set(0, 0, bounds.width(), bounds.height());
	}

	@Override
	public void draw(Canvas canvas) {
		Paint colorPaint = new Paint();
		colorPaint.setColor(color);
		canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, colorPaint);
		super.draw(canvas);
	}
}
