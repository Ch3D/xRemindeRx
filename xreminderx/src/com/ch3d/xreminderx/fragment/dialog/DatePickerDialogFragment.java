
package com.ch3d.xreminderx.fragment.dialog;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.ch3d.xreminderx.provider.RemindersContract;

public class DatePickerDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener
{
    private final OnReminderDateSetListener listener;

    public DatePickerDialogFragment()
    {
        listener = null;
    }

    public DatePickerDialogFragment(final OnReminderDateSetListener listener)
    {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        final Bundle arguments = getArguments();
        c.setTimeInMillis(arguments.getLong(RemindersContract.Columns.TIMESTAMP));
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int monthOfYear,
            final int dayOfMonth)
    {
        listener.onReminderDateSet(getTag(), year, monthOfYear, dayOfMonth);
    }
}
