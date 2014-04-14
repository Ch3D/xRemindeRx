
package com.ch3d.xreminderx.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.adapter.ColorsAdapter;
import com.ch3d.xreminderx.fragment.dialog.DatePickerDialogFragment;
import com.ch3d.xreminderx.fragment.dialog.OnReminderDateSetListener;
import com.ch3d.xreminderx.fragment.dialog.OnReminderTimeSetListener;
import com.ch3d.xreminderx.fragment.dialog.TimePickerDialogFragment;
import com.ch3d.xreminderx.model.ReminderColor;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.provider.RemindersContract;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ContactBadgeHolder;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.StringUtils;
import com.ch3d.xreminderx.utils.ViewUtils;

public class ReminderEditFragment extends Fragment implements OnClickListener,
        OnReminderDateSetListener,
        OnReminderTimeSetListener
{
    private static final String REMINDER_FRAGMENT_DATA          = "ReminderEditFragment.data";

    public static final String  TAG                             = "ReminderEdit";

    private static final int    REQUEST_CODE_SPEECH_RECOGNITION = 1;

    public static final int     REQUEST_CODE_GET_CONTACT        = 2;

    /**
     * @param data array of data
     * @return index of max value in array
     */
    private static int findMax(final float[] data)
    {
        int result = -1;
        float currMax = Float.MIN_VALUE;
        for (int i = 0; i < data.length; i++)
        {
            final float curr = data[i];
            if (currMax < curr)
            {
                currMax = curr;
                result = i;
            }
        }
        return result;
    }

    public static ReminderEditFragment newInstance(final Uri data) {
        ReminderEditFragment fragment = new ReminderEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(REMINDER_FRAGMENT_DATA, data.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @InjectView(R.f_reminder_edit.text)
    protected EditText         mText;

    @InjectView(R.f_reminder_edit.btnTsDatePicker)
    protected Button           mBtnDatePicker;

    @InjectView(R.f_reminder_edit.btnTsTimePicker)
    protected Button           mBtnTimePicker;

    @InjectView(R.f_reminder_edit.btnTsAlarmDatePicker)
    protected Button           mBtnAlarmDatePicker;

    @InjectView(R.f_reminder_edit.btnTsAlarmTimePicker)
    protected Button           mBtnAlarmTimePicker;

    protected ReminderEntry    mReminder;

    private ContactBadgeHolder mContactBadgeHolder;

    @InjectView(R.f_reminder_edit.btnStartSpeech)
    protected Button           btnSpeech;

    @InjectView(R.f_reminder_edit.cbOngoing)
    protected CheckBox         mOngoing;

    @InjectView(R.f_reminder_edit.cbSilent)
    protected CheckBox         mSilent;

    @InjectView(R.f_reminder_edit.color)
    protected Spinner          mColor;

    private ColorsAdapter      mColorsAdapter;

    protected boolean checkValid()
    {
        final boolean isBlank = StringUtils.isBlank(mText.getText().toString());
        if (isBlank)
        {
            mText.setError(getActivity().getString(R.string.please_enter_event_title));
            mText.requestFocus();
            return false;
        }
        if (mReminder.getAlarmTimestamp() > mReminder.getTimestamp())
        {
            Toast.makeText(getActivity(),
                    "Alarm time can't be after Event time", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    protected ReminderEntry getReminder()
    {
        final Uri data = Uri.parse(getArguments().getString(REMINDER_FRAGMENT_DATA));
        final Cursor query = getActivity().getContentResolver().query(
                data, null, null,
                null, null);
        return ReminderUtils.parse(query);
    }

    protected int getTitleResource()
    {
        return R.string.edit_reminder;
    }

    private Calendar modifyDate(final long ts, final int year, final int monthOfYear,
            final int dayOfMonth)
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONDAY, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return c;
    }

    private Calendar modifyTime(final long ts, final int hourOfDay, final int minute)
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if (requestCode == REQUEST_CODE_SPEECH_RECOGNITION)
        {
            final ArrayList<String> words = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final float[] scores = data
                    .getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

            final int maxItemIndex = findMax(scores);
            mText.setText(words.get(maxItemIndex));
        }
        else if (requestCode == REQUEST_CODE_GET_CONTACT)
        {
            if (data != null)
            {
                final Uri uri = data.getData();
                setContactData(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @Optional
    @OnClick({
            R.f_reminder_edit.btnStartSpeech, R.x_contact_badge.btnRemove,
            R.f_reminder_edit.btnTsDatePicker, R.f_reminder_edit.btnTsTimePicker,
            R.f_reminder_edit.btnTsAlarmDatePicker, R.f_reminder_edit.btnTsAlarmTimePicker
    })
    public void onClick(final View v)
    {
        final Bundle bundle = new Bundle();
        bundle.putLong(RemindersContract.Columns.TIMESTAMP, mReminder.getTimestamp());
        switch (v.getId())
        {
            case R.f_reminder_edit.btnStartSpeech:
                final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent
                        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition...");
                startActivityForResult(speechIntent, REQUEST_CODE_SPEECH_RECOGNITION);
                break;

            case R.x_contact_badge.btnRemove:
                setContactData(Uri.EMPTY);
                break;

            // case R.f_reminder_edit.btnPickContact:
            // final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            // startActivityForResult(intent, REQUEST_CODE_GET_CONTACT);
            // break;

            case R.f_reminder_edit.btnTsDatePicker:
                final DatePickerDialogFragment dateFragment = new DatePickerDialogFragment(this);
                dateFragment.setArguments(bundle);
                dateFragment.show(getFragmentManager(), RemindersContract.Columns.TIMESTAMP);
                break;

            case R.f_reminder_edit.btnTsTimePicker:
                final TimePickerDialogFragment timeFragment = new TimePickerDialogFragment(this);
                timeFragment.setArguments(bundle);
                timeFragment.show(getFragmentManager(), RemindersContract.Columns.TIMESTAMP);
                break;

            case R.f_reminder_edit.btnTsAlarmDatePicker:
                final DatePickerDialogFragment alarmDateFragment = new DatePickerDialogFragment(
                        this);
                alarmDateFragment.setArguments(bundle);
                alarmDateFragment.show(getFragmentManager(),
                        RemindersContract.Columns.ALARM_TIMESTAMP);
                break;

            case R.f_reminder_edit.btnTsAlarmTimePicker:
                final TimePickerDialogFragment alarmTimeFragment = new TimePickerDialogFragment(
                        this);
                alarmTimeFragment.setArguments(bundle);
                alarmTimeFragment.show(getFragmentManager(),
                        RemindersContract.Columns.ALARM_TIMESTAMP);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
    {
        inflater.inflate(R.menu.reminder_edit, menu);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.f_reminder_edit, container, false);
        mContactBadgeHolder = new ContactBadgeHolder(getActivity(),
                (ViewStub) view.findViewById(R.f_reminder_edit.contact_badge));
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.menu.action_save:
                if (!checkValid())
                {
                    return false;
                }

                mReminder.setText(mText.getText().toString());
                mReminder.setOngoing(mOngoing.isChecked());
                mReminder.setSilent(mSilent.isChecked());
                ReminderColor color = (ReminderColor) mColor.getSelectedItem();
                mReminder.setColor(color.getColor());

                RemindersProvider.updateReminder(getActivity(), mReminder, true);
                setAlarm(mReminder);

                getActivity().onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        ViewUtils.hideKeyboard(mText);
    }

    @Override
    public void onReminderDateSet(final String tag, final int year, final int monthOfYear,
            final int dayOfMonth)
    {
        if (RemindersContract.Columns.TIMESTAMP.equals(tag))
        {
            final Calendar calendar = modifyDate(mReminder.getTimestamp(), year, monthOfYear,
                    dayOfMonth);
            mReminder.setTimestamp(calendar.getTimeInMillis());
        }
        else if (RemindersContract.Columns.ALARM_TIMESTAMP.equals(tag))
        {
            final Calendar c = modifyDate(mReminder.getAlarmTimestamp(), year, monthOfYear,
                    dayOfMonth);
            mReminder.setAlarmTimestamp(c.getTimeInMillis());
        }
        updateRemiderViewData();
    }

    @Override
    public void onReminderTimeSet(final String tag, final int hourOfDay, final int minute)
    {
        if (RemindersContract.Columns.TIMESTAMP.equals(tag))
        {
            final Calendar c = modifyTime(mReminder.getTimestamp(), hourOfDay, minute);
            mReminder.setTimestamp(c.getTimeInMillis());
        }
        else if (RemindersContract.Columns.ALARM_TIMESTAMP.equals(tag))
        {
            final Calendar c = modifyTime(mReminder.getAlarmTimestamp(), hourOfDay, minute);
            mReminder.setAlarmTimestamp(c.getTimeInMillis());
        }
        updateRemiderViewData();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getTitleResource());
        mColorsAdapter = new ColorsAdapter(getActivity());
        mColor.setAdapter(mColorsAdapter);
        mColor.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int pos,
                    final long arg3)
            {
                ReminderColor color = (ReminderColor) mColor.getItemAtPosition(pos);
                mReminder.setColor(color.getColor());
            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0)
            {

            }
        });
        mText.setSelectAllOnFocus(true);

        mText.requestFocus();
        mText.selectAll();
        mText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(final Editable s)
            {
                // Do nothing
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                    final int after)
            {
                // Do nothing
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                    final int count)
            {
                mReminder.setText(s.toString());
            }
        });

        mReminder = getReminder();
        mContactBadgeHolder.setVisibility(View.VISIBLE);
        mContactBadgeHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, REQUEST_CODE_GET_CONTACT);
            }
        });
        setContactData(mReminder.getContactUri());

        updateRemiderViewData();
    }

    protected void setAlarm(final ReminderEntry entry)
    {
        final int id = mReminder.getId();
        ReminderUtils.setAlarm(getActivity(), id, entry);
    }

    private void setContactBadgeData(final Uri uri, final OnClickListener removeClickListener)
    {
        mContactBadgeHolder.setData(uri, removeClickListener);
    }

    protected void setContactData(final Uri uri)
    {
        mReminder.setContactUri(uri);
        setContactBadgeData(uri, this);
    }

    protected void updateRemiderViewData()
    {
        mText.setText(mReminder.getText());
        ViewUtils.moveCursorRight(mText);

        mOngoing.setChecked(mReminder.isOngoing());
        mSilent.setChecked(mReminder.isSilent());
        final int colorPosition =
                mColorsAdapter.getPosition(mReminder.getColor());
        mColor.setSelection(colorPosition);

        setContactBadgeData(mReminder.getContactUri(), this);
        mBtnDatePicker.setText(ReminderUtils.formatTimestmapDate(getActivity(), mReminder));
        mBtnTimePicker.setText(ReminderUtils.formatTimestmapTime(getActivity(), mReminder));

        mBtnAlarmDatePicker.setText(ReminderUtils.formatAlarmDate(getActivity(), mReminder));
        mBtnAlarmTimePicker.setText(ReminderUtils.formatAlarmTime(getActivity(), mReminder));
    }
}
