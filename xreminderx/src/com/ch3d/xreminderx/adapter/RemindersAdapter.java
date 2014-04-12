
package com.ch3d.xreminderx.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.StringUtils;

public class RemindersAdapter extends CursorAdapter implements OnClickListener {
    public static class ViewHolder {
        public View      root;

        public long      id;

        public TextView  text;

        public TextView  date;

        public ImageView iconType;

        public ImageView imgContact;

    }

    private static final int           VIEW_TYPE_COUNT   = 2;

    private static final int           VIEW_TYPE_SIMPLE  = 0;

    private static final int           VIEW_TYPE_CONTACT = 1;

    private final SparseArray<Boolean> mChecked          = new SparseArray<Boolean>(10);

    private final LayoutInflater       mInflater;

    private final Bitmap               mDefaultImg;

    public RemindersAdapter(final Context context, final Cursor c,
            final boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
        mDefaultImg = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_contact_picture);
    }

    @Override
    public void bindView(final View view, final Context context,
            final Cursor cursor) {
        final long id = cursor.getLong(RemindersContract.Indexes._ID);

        final int position = cursor.getPosition();
        if ((mChecked.get(position) != null) && mChecked.get(position)) {
            view.setBackgroundColor(context.getResources().getColor(
                    android.R.color.holo_blue_bright));
        } else {
            view.setBackgroundColor(context.getResources().getColor(
                    android.R.color.transparent));
            // view.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        }

        if (isContactRelated(cursor)) {
            final Uri contactUri = Uri.parse(cursor
                    .getString(RemindersContract.Indexes.CONTACT_URI));
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.id = id;
            holder.text.setText(cursor
                    .getString(RemindersContract.Indexes.TEXT));
            // holder.color.setBackgroundColor(cursor
            // .getInt(RemindersContract.Indexes.COLOR));

            final String dateTime = ReminderUtils.formatDateTimeShort(context,
                    cursor.getLong(RemindersContract.Indexes.TIMESTAMP));
            holder.date.setText(dateTime);

            holder.imgContact.setImageBitmap(ReminderUtils.fetchThumbnail(
                    context, contactUri, mDefaultImg));
            holder.imgContact.setTag(contactUri);
            holder.imgContact.setOnClickListener(this);
        } else {
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.id = id;
            holder.text.setText(cursor
                    .getString(RemindersContract.Indexes.TEXT));
            holder.root.setBackgroundResource(ReminderUtils.getReminderItemBackground(context,
                    cursor.getInt(RemindersContract.Indexes.COLOR)));
            // holder.color.setBackgroundColor(cursor
            // .getInt(RemindersContract.Indexes.COLOR));
            holder.iconType.setImageLevel(cursor
                    .getInt(RemindersContract.Indexes.TYPE));
            final String date = ReminderUtils.formatDate(context,
                    cursor.getLong(RemindersContract.Indexes.TIMESTAMP));
            final String time = ReminderUtils.formatTime(context,
                    cursor.getLong(RemindersContract.Indexes.TIMESTAMP));
            holder.date.setText(date + " " + time);
        }
    }

    public SparseArray<Boolean> getCheckedItems() {
        return mChecked;
    }

    @Override
    public int getItemViewType(final int position) {
        final Cursor cursor = getCursor();
        final boolean moveToPosition = cursor.moveToPosition(position);
        if (moveToPosition) {
            return isContactRelated(cursor) ? VIEW_TYPE_CONTACT
                    : VIEW_TYPE_SIMPLE;
        }
        return VIEW_TYPE_SIMPLE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    private boolean isContactRelated(final Cursor cursor) {
        final String contactUri = cursor.getString(RemindersContract.Indexes.CONTACT_URI);
        return !StringUtils.isBlank(contactUri);
    }

    @Override
    public View newView(final Context context, final Cursor cursor,
            final ViewGroup parent) {
        final View convertView;
        final ViewHolder holder = new ViewHolder();
        if (isContactRelated(cursor)) {
            convertView = mInflater.inflate(
                    R.layout.x_reminder_item_contact2, parent, false);
            holder.text = (TextView) convertView
                    .findViewById(R.x_reminder_item_contact.txt_msg);
            holder.date = (TextView) convertView
                    .findViewById(R.x_reminder_item_contact.txt_datetime);
            // holder.color = convertView
            // .findViewById(R.x_reminder_item_contact.color);
            holder.imgContact = (ImageView) convertView
                    .findViewById(R.x_reminder_item_contact.img);
        } else {
            convertView = mInflater.inflate(
                    R.layout.x_reminder_item, parent, false);
            holder.root = convertView
                    .findViewById(R.x_reminder_item.root);
            holder.text = (TextView) convertView
                    .findViewById(R.x_reminder_item.text);
            holder.iconType = (ImageView) convertView
                    .findViewById(R.x_reminder_item.icon_type);
            // holder.color = convertView.findViewById(R.x_reminder_item.color);
            holder.date = (TextView) convertView
                    .findViewById(R.x_reminder_item.date);
        }
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.x_reminder_item_contact.img:
                final Uri contactUri = (Uri) v.getTag();
                final Intent intent = ActivityUtils.getContactDetailsIntent(
                        mContext, contactUri);
                if (intent == null) {
                    return;
                }
                if (ActivityUtils.isJeallyBean()) {
                    final ActivityOptions options = ActivityOptions
                            .makeCustomAnimation(mContext,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                    mContext.startActivity(intent, options.toBundle());
                } else {
                    mContext.startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Used when removing reminder using actionBar/menu and so on.
     * 
     * @param convertView - viwe that represents reminder
     * @param id - reminder id
     */
    public void removeReminder(final View convertView, final int id) {
        final PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[2];
        arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat(View.X, 800);
        arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

        final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(convertView,
                arrayOfPropertyValuesHolder);
        anim.setDuration(300);
        ViewCompat.setHasTransientState(convertView, true);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                ReminderUtils.deleteReminder(convertView.getContext(), id);
                convertView.post(new Runnable() {
                    @Override
                    public void run() {
                        convertView.setX(0);
                        convertView.setAlpha(1);
                        ViewCompat.setHasTransientState(convertView, false);
                    }
                });
            }
        });
        anim.start();
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
