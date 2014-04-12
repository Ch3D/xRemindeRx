
package com.ch3d.xreminderx.model;

import android.graphics.Color;

import com.ch3d.xreminderx.R;

public enum ReminderColor {
    LIGHT_GREEN("#9acc5b", R.drawable.bg_xrx_green_normal),
    LIGHT_BLUE("#4dbce8", R.drawable.bg_xrx_blue_normal);

    public static int getResId(final int color) {
        ReminderColor[] v = values();
        for (int i = 0; i < v.length; i++) {
            if (v[i].color == color) {
                return v[i].resId;
            }
        }
        return R.drawable.bg_xrx_green_normal;
    }

    private final int color;

    private final int resId;

    private ReminderColor(final String c, final int res)
    {
        resId = res;
        color = Color.parseColor(c);
    }

    public int getColor() {
        return color;
    }

    public int getResId() {
        return resId;
    }
}
