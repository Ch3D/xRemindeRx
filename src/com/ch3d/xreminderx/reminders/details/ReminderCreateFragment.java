package com.ch3d.xreminderx.reminders.details;

import java.util.Calendar;

import android.content.ContentUris;
import android.net.Uri;
import android.view.MenuItem;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.reminders.model.ReminderEntry;
import com.ch3d.xreminderx.reminders.model.ReminderFactory;
import com.ch3d.xreminderx.reminders.provider.RemindersProvider;
import com.ch3d.xreminderx.reminders.utils.ReminderUtils;

public class ReminderCreateFragment extends ReminderEditFragment
{
	public static final String	TAG	= "ReminderCreate";

	@Override
	protected ReminderEntry getReminder()
	{
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		final ReminderEntry reminder = ReminderFactory.createNull(c.getTimeInMillis());
		return reminder;
	}

	@Override
	protected int getTitleResource()
	{
		return R.string.create_new_event;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.menu.action_save:
				if(!checkValid())
				{
					return true;
				}
				mReminder.setText(mText.getText().toString());
				mReminder.setOngoing(mOngoing.isChecked() ? 1 : 0);
				mReminder.setSilent(mSilent.isChecked() ? 1 : 0);
				mReminder.setColor((Integer)mColor.getSelectedItem());

				final Uri insert = RemindersProvider.addReminder(getActivity(), mReminder);
				final long id = ContentUris.parseId(insert);
				ReminderUtils.setAlarm(getActivity(), id, mReminder);
				getActivity().onBackPressed();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
