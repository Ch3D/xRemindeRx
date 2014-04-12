
package com.ch3d.xreminderx.model;

import android.graphics.Color;

import com.ch3d.xreminderx.R;

public enum ReminderColor {
    LIGHT_GREEN("#9acc5b", R.drawable.bg_xrx_green_normal, R.style.Theme_XRX_Green),
    LIGHT_BLUE("#4dbce8", R.drawable.bg_xrx_blue_normal, R.style.Theme_XRX_Blue);

    private static ReminderColor getReminderColor(int color) {
        ReminderColor[] v = values();
        for (int i = 0; i < v.length; i++) {
            if (v[i].color == color) {
                return v[i];
            }
        }
        return null;
    }

    public static int getResId(final int color) {
        final ReminderColor rc = getReminderColor(color);
        if (rc != null) {
            return rc.resId;
        }
        return R.drawable.bg_xrx_green_normal;
    }

    public static int getThemeId(int color) {
        final ReminderColor rc = getReminderColor(color);
        if (rc != null) {
            return rc.theme;
        }
        return R.style.Theme_XRX_Green;
    }

    private final int color;

    private final int resId;

    private final int theme;

    private ReminderColor(final String c, final int res, final int theme)
    {
        resId = res;
        this.theme = theme;
        color = Color.parseColor(c);
    }

    public int getColor() {
        return color;
    }

    public int getResId() {
        return resId;
    }

    public int getTheme() {
        return theme;
    }
}
