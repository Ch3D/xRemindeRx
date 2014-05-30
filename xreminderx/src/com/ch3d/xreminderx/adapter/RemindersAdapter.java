
package com.ch3d.xreminderx.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.utils.ReminderUtils;

public class RemindersAdapter extends CursorAdapter {

    public static class ViewHolder {
        public long id;
        public TextView text;
        public TextView date;
    }

    private final SparseBooleanArray mChecked = new SparseBooleanArray(10);

    private final LayoutInflater mInflater;

    public RemindersAdapter(final Context context, final Cursor c,
            final boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(final View view, final Context context,
            final Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.id = cursor.getLong(RemindersContract.Indexes._ID);
        holder.text.setText(cursor
                .getString(RemindersContract.Indexes.TEXT));
        final String date = ReminderUtils.formatDate(context,
                cursor.getLong(RemindersContract.Indexes.TIMESTAMP));
        final String time = ReminderUtils.formatTime(context,
                cursor.getLong(RemindersContract.Indexes.TIMESTAMP));
        holder.date.setText(date + " " + time);
    }

    public int[] getCheckedItems() {
        final int[] result = new int[mChecked.size()];
        final int size = mChecked.size();
        int j = 0;
        for (int i = 0; i < size; i++)
        {
            final int key = mChecked.keyAt(i);
            if (mChecked.get(key))
            {
                result[j++] = key;
            }
        }
        return result;
    }

    @Override
    public View newView(final Context context, final Cursor cursor,
            final ViewGroup parent) {
        final View convertView = mInflater.inflate(
                R.layout.x_reminder_item, parent, false);
        final ViewHolder holder = new ViewHolder();
        holder.text = (TextView) convertView
                .findViewById(R.x_reminder_item.text);
        holder.date = (TextView) convertView
                .findViewById(R.x_reminder_item.date);
        convertView.setTag(holder);
        return convertView;
    }

    public void setChecked(final int position, final boolean checked) {
        mChecked.put(position, checked);
        notifyDataSetChanged();
    }

    public void uncheckItems() {
        mChecked.clear();
        notifyDataSetChanged();
    }
}
