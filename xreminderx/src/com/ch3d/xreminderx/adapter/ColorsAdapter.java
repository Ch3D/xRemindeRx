package com.ch3d.xreminderx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.ReminderColor;

public class ColorsAdapter extends BaseAdapter {
    private final Context mContext;

    private final ReminderColor[] mColors;

    private final LayoutInflater mInflater;

    public ColorsAdapter(final Context context) {
        mContext = context;
        mColors = ReminderColor.values();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mColors.length;
    }

    @Override
    public Object getItem(final int position) {
        return mColors[position];
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public int getPosition(final int color) {
        for (int i = 0; i < mColors.length; i++) {
            if (mColors[i].getColor() == color) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View inflate = mInflater.inflate(R.layout.simple_spinner_item,
                parent, false);
        inflate.setBackgroundColor(mColors[position].getColor());
        return inflate;
    }
}
