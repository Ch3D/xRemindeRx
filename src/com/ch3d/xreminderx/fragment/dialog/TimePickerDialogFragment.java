package com.ch3d.xreminderx.fragment.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.ch3d.xreminderx.provider.RemindersContract;

public class TimePickerDialogFragment extends DialogFragment implements OnTimeSetListener
{
	private final OnReminderTimeSetListener	listener;

	public TimePickerDialogFragment(final OnReminderTimeSetListener listener)
	{
		this.listener = listener;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState)
	{
		final Calendar c = Calendar.getInstance();
		final Bundle arguments = getArguments();
		c.setTimeInMillis(arguments.getLong(RemindersContract.Columns.TIMESTAMP));
		final int hour = c.get(Calendar.HOUR_OF_DAY);
		final int minute = c.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), this, hour, minute, true);
	}

	@Override
	public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute)
	{
		listener.onReminderTimeSet(getTag(), hourOfDay, minute);
	}
}
