
package com.ch3d.xreminderx.fragment;

import android.view.MenuItem;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;

public class ReminderCreateFragment extends ReminderEditFragment
{
    public static final String TAG = "ReminderCreate";

    @Override
    protected ReminderEntry getReminder()
    {
        final ReminderEntry reminder = ReminderFactory.createNull();
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
        switch (item.getItemId())
        {
            case R.menu.action_save:
                if (!checkValid())
                {
                    return true;
                }
                mReminder.setText(mText.getText().toString());
                mReminder.setOngoing(mOngoing.isChecked());
                mReminder.setSilent(mSilent.isChecked());
                mReminder.setColor((Integer) mColor.getSelectedItem());

                RemindersProvider.addReminder(getActivity(), mReminder, true);
                getActivity().onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
