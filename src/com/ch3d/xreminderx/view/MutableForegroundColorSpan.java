
package com.ch3d.xreminderx.view;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

public class MutableForegroundColorSpan extends ForegroundColorSpan
{
    private int mAlpha = 255;
    private int mForegroundColor;

    public MutableForegroundColorSpan(final int alpha, final int color)
    {
        super(color);
        mAlpha = alpha;
        mForegroundColor = color;
    }

    public MutableForegroundColorSpan(final Parcel src)
    {
        super(src);
        mForegroundColor = src.readInt();
        mAlpha = src.readInt();
    }

    public float getAlpha()
    {
        return mAlpha;
    }

    @Override
    public int getForegroundColor()
    {
        return Color.argb(mAlpha, Color.red(mForegroundColor), Color.green(mForegroundColor),
                Color.blue(mForegroundColor));
    }

    /**
     * @param alpha from 0 to 255
     */
    public void setAlpha(final int alpha)
    {
        mAlpha = alpha;
    }

    public void setForegroundColor(final int foregroundColor)
    {
        mForegroundColor = foregroundColor;
    }

    @Override
    public void updateDrawState(final TextPaint ds)
    {
        ds.setColor(getForegroundColor());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(mForegroundColor);
        dest.writeFloat(mAlpha);
    }
}
