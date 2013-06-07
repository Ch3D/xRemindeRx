package com.ch3d.xreminderx.adapter;

import java.util.HashMap;
import java.util.Iterator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.ReminderType;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.Consts;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.Consts.PREFS;

public class RemindersAdapter extends CursorAdapter implements OnClickListener
{
	static class ViewHolder
	{
		long				id;

		TextView			text;

		Button				btnRemove;

		View				color;

		public TextView		date;

		public TextView		time;

		public ImageView	iconType;
	}

	static class ViewHolderContact extends ViewHolder
	{
		ImageView	imgContact;

		Button		btnDetails;
	}

	private static final int		VIEW_TYPE_COUNT		= 2;

	private static final int		VIEW_TYPE_SIMPLE	= 0;

	private static final int		VIEW_TYPE_CONTACT	= 1;

	HashMap<Integer, Boolean>		mChecked			= new HashMap<Integer, Boolean>();

	private final LayoutInflater	mInflater;

	private final Bitmap			mDefaultImg;

	public RemindersAdapter(final Activity activity, final Cursor c, final boolean autoRequery)
	{
		super(activity, c, autoRequery);
		mInflater = LayoutInflater.from(activity);
		mDefaultImg = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_contact_picture);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor)
	{
		final long id = cursor.getLong(RemindersContract.Indexes._ID);

		final int position = cursor.getPosition();
		if((mChecked.get(position) != null) && mChecked.get(position))
		{
			view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_bright));
		}
		else
		{
			view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
			// view.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
		}

		final int type = cursor.getInt(RemindersContract.Indexes.TYPE);
		if(ReminderType.parse(type) == ReminderType.CONTACT)
		{
			final Uri contactUri = Uri.parse(cursor.getString(RemindersContract.Indexes.CONTACT_URI));

			final ViewHolderContact holder = (ViewHolderContact)view.getTag();
			holder.id = id;
			holder.text.setText(cursor.getString(RemindersContract.Indexes.TEXT));
			holder.color.setBackgroundColor(cursor.getInt(RemindersContract.Indexes.COLOR));

			final String dateTime = ReminderUtils.formatDate(context,
					cursor.getLong(RemindersContract.Indexes.TIMESTAMP))
					+ " " + ReminderUtils.formatTime(context, cursor.getLong(RemindersContract.Indexes.TIMESTAMP));
			holder.date.setText(dateTime);

			holder.btnDetails.setTag(contactUri);
			holder.btnDetails.setOnClickListener(this);
			holder.btnDetails.setEnabled(ReminderUtils.hasAddressbookContact(mContext, contactUri));
			holder.btnRemove.setTag(id);
			holder.btnRemove.setOnClickListener(this);
			holder.btnRemove.setEnabled(mChecked.size() == 0);
			holder.imgContact.setImageBitmap(ReminderUtils.fetchThumbnail(context, contactUri, mDefaultImg));
			holder.imgContact.setTag(contactUri);
			holder.imgContact.setOnClickListener(this);
		}
		else
		{
			final ViewHolder holder = (ViewHolder)view.getTag();
			holder.id = id;
			holder.text.setText(cursor.getString(RemindersContract.Indexes.TEXT));
			holder.color.setBackgroundColor(cursor.getInt(RemindersContract.Indexes.COLOR));
			holder.iconType.setImageLevel(cursor.getInt(RemindersContract.Indexes.TYPE));
			holder.date.setText(ReminderUtils.formatDate(context, cursor.getLong(RemindersContract.Indexes.TIMESTAMP)));
			holder.time.setText(ReminderUtils.formatTime(context, cursor.getLong(RemindersContract.Indexes.TIMESTAMP)));
			holder.btnRemove.setTag(id);
			holder.btnRemove.setOnClickListener(this);
			holder.btnRemove.setEnabled(mChecked.size() == 0);
		}
	}

	public Iterator<Integer> getCheckedItemPositions()
	{
		return mChecked.keySet().iterator();
	}

	@Override
	public int getItemViewType(final int position)
	{
		final Cursor cursor = getCursor();
		final boolean moveToPosition = cursor.moveToPosition(position);
		if(moveToPosition)
		{
			final int type = cursor.getInt(RemindersContract.Indexes.TYPE);
			return ReminderType.parse(type) == ReminderType.CONTACT ? VIEW_TYPE_CONTACT : VIEW_TYPE_SIMPLE;
		}
		return VIEW_TYPE_SIMPLE;
	}

	@Override
	public int getViewTypeCount()
	{
		return VIEW_TYPE_COUNT;
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent)
	{
		final int type = cursor.getInt(RemindersContract.Indexes.TYPE);
		if(ReminderType.parse(type) == ReminderType.CONTACT)
		{
			final View convertView = mInflater.inflate(R.layout.x_reminder_item_contact2, parent, false);
			final ViewHolderContact holder = new ViewHolderContact();
			holder.text = (TextView)convertView.findViewById(R.x_reminder_item_contact.txt_msg);
			holder.date = (TextView)convertView.findViewById(R.x_reminder_item_contact.txt_datetime);
			holder.time = (TextView)convertView.findViewById(R.x_reminder_item_contact.txt_datetime);
			holder.btnRemove = (Button)convertView.findViewById(R.x_reminder_item_contact.btn_remove);
			holder.color = convertView.findViewById(R.x_reminder_item_contact.color);
			holder.btnRemove.setTag(R.id.parent, convertView);
			holder.btnDetails = (Button)convertView.findViewById(R.x_reminder_item_contact.btn_details);
			holder.imgContact = (ImageView)convertView.findViewById(R.x_reminder_item_contact.img);
			convertView.setTag(holder);
			return convertView;
		}
		else
		{
			final View convertView = mInflater.inflate(R.layout.x_reminder_item, parent, false);
			final ViewHolder holder = new ViewHolder();
			holder.text = (TextView)convertView.findViewById(R.x_reminder_item.text);
			holder.iconType = (ImageView)convertView.findViewById(R.x_reminder_item.icon_type);
			holder.color = convertView.findViewById(R.x_reminder_item.color);
			holder.date = (TextView)convertView.findViewById(R.x_reminder_item.date);
			holder.time = (TextView)convertView.findViewById(R.x_reminder_item.time);
			holder.btnRemove = (Button)convertView.findViewById(R.x_reminder_item.btn_remove);
			holder.btnRemove.setTag(R.id.parent, convertView);
			convertView.setTag(holder);
			return convertView;
		}
	}

	@Override
	public void onClick(final View v)
	{
		switch(v.getId())
		{
			case R.x_reminder_item_contact.btn_remove:
			case R.x_reminder_item.btn_remove:
				onRemove(v);
				break;

			case R.x_reminder_item_contact.btn_details:
			case R.x_reminder_item_contact.img:
				final Uri contactUri = (Uri)v.getTag();
				final Intent intent = ActivityUtils.getContactDetailsIntent(mContext, contactUri);
				if(ActivityUtils.isJeallyBean())
				{
					final ActivityOptions options = ActivityOptions.makeCustomAnimation(mContext,
							android.R.anim.fade_in, android.R.anim.fade_out);
					mContext.startActivity(intent, options.toBundle());
				}
				else
				{
					mContext.startActivity(intent);
				}
				break;

			default:
				break;
		}
	}

	private void onRemove(final View v)
	{
		final Long id = (Long)v.getTag();
		final ViewGroup parent = (ViewGroup)v.getTag(R.id.parent);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		final boolean showPromt = preferences.getBoolean(Consts.PREFS.SHOW_REMINDER_REMOVE_PROMT, mContext
				.getResources().getBoolean(R.bool.show_prompt_default));
		if(showPromt)
		{
			final View dialogView = mInflater.inflate(R.layout.f_dialog_remove_promt, null);
			final CheckBox cb = (CheckBox)dialogView.findViewById(R.f_dialog_remove_promt.checkboxPromt);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
				{
					final Editor editor = preferences.edit();
					editor.putBoolean(Consts.PREFS.SHOW_REMINDER_REMOVE_PROMT, !isChecked);
					editor.commit();
				}
			});
			final AlertDialog.Builder builder = new Builder(mContext);
			builder.setView(dialogView);
			builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(final DialogInterface dialog, final int which)
				{
					removeReminder(parent, id);
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(final DialogInterface dialog, final int which)
				{
					dialog.dismiss();
				}
			});
			builder.show();
		}
		else
		{
			removeReminder(parent, id);
		}
	}

	public void removeReminder(final View convertView, final Long id)
	{
		final PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[2];
		arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat(View.X, 800);
		arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

		final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(convertView, arrayOfPropertyValuesHolder);
		anim.setDuration(300);
		ViewCompat.setHasTransientState(convertView, true);
		anim.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(final Animator animation)
			{
				final Context context = convertView.getContext();
				context.getContentResolver().delete(RemindersProvider.REMINDERS_URI, "_id = ?",
						new String[] {Long.toString(id)});
				context.getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);

				final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.cancel(ReminderUtils.getPendingAlarmOperation(context, id));

				convertView.post(new Runnable()
				{
					@Override
					public void run()
					{
						convertView.setX(0);
						convertView.setAlpha(1);
						ViewCompat.setHasTransientState(convertView, false);
					}
				});
			}
		});
		anim.start();
	}

	public void setChecked(final int position, final boolean checked)
	{
		mChecked.put(position, checked);
		notifyDataSetChanged();
	}

	public void uncheckItems()
	{
		mChecked.clear();
		notifyDataSetChanged();
	}
}
