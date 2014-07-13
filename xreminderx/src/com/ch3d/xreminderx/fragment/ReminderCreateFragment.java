package com.ch3d.xreminderx.fragment;

import android.view.MenuItem;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.ReminderColor;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;

public class ReminderCreateFragment extends ReminderEditFragment {
	@Override
	protected ReminderEntry getReminder() {
		final ReminderEntry reminder = ReminderFactory.createNew();
		return reminder;
	}

	@Override
	protected int getTitleResource() {
		return R.string.create_new_event;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.menu.action_save:
				if (!checkValid()) {
					return true;
				}
				mReminder.setText(mText.getText().toString());
				mReminder.setOngoing(mOngoing.isChecked());
				mReminder.setSilent(mSilent.isChecked());
				final ReminderColor reminderColor = (ReminderColor) mColor.getSelectedItem();
				mReminder.setColor(reminderColor.getColor());

				RemindersProvider.addReminder(getActivity(), mReminder, true);
				getActivity().onBackPressed();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
