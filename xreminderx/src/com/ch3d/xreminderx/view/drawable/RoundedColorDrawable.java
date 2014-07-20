package com.ch3d.xreminderx.view.drawable;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by xCh3Dx on 14.07.2014.
 */
public class RoundedColorDrawable extends RoundedDrawable {
	private Paint colorPaint;
	private int color;
	private RectF mRect = new RectF();

	RoundedColorDrawable(BitmapShader shader, float cornerRadius, int margin) {
		super(shader, cornerRadius, margin);
	}

	@Override
	RoundedDrawable initPaint() {
		super.initPaint();
		colorPaint = new Paint();
		colorPaint.setColor(color);
		return this;
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
		canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, colorPaint);
		super.draw(canvas);
	}
}
