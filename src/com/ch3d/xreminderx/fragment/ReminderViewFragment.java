
package com.ch3d.xreminderx.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.activity.RemindersActivity;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.ContactBadgeHolder;
import com.ch3d.xreminderx.utils.ReminderUtils;

public class ReminderViewFragment extends Fragment {
    public static final String TAG = "ReminderDetails";

    private TextView           mText;

    private TextView           mTimestamp;

    private TextView           mAlarmTimstamp;

    private ImageView          mIconType;

    private ContactBadgeHolder mContactBadgeHolder;

    private View               mPanelContact;

    private TextView           mOngoing;

    private TextView           mSilent;

    private View               mColor;

    private void bindView(final ReminderEntry reminder) {
        mText.setText(reminder.getText());
        mIconType.setImageLevel(reminder.getType().getId());

        mOngoing.setVisibility(reminder.isOngoing() ? View.VISIBLE : View.GONE);
        mSilent.setVisibility(reminder.isSilent() ? View.VISIBLE : View.GONE);

        mColor.setBackgroundColor(reminder.getColor());

        if (reminder.isContactRelated()) {
            mContactBadgeHolder.setVisibility(View.VISIBLE);
            mPanelContact.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.x_contact_badge.btnRemove)
                    .setVisibility(View.GONE);
            mContactBadgeHolder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    showContactDetails(reminder);
                }
            });
            setContactBagdeData(reminder.getContactUri(), null);
        } else {
            mPanelContact.setVisibility(View.GONE);
            mContactBadgeHolder.setVisibility(View.GONE);
        }
        mTimestamp.setText(ReminderUtils.formatDateTimeShort(getActivity(),
                reminder.getTimestamp()));
        mAlarmTimstamp.setText(ReminderUtils.formatDateTimeShort(getActivity(),
                reminder.getAlarmTimestamp()));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.reminder_view, menu);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.f_reminder_view, container,
                false);
        mText = (TextView) view.findViewById(R.f_reminder_view.text);
        mOngoing = (TextView) view.findViewById(R.f_reminder_view.txtOngoing);
        mSilent = (TextView) view.findViewById(R.f_reminder_view.txtSilent);
        mColor = view.findViewById(R.f_reminder_view.color);
        mIconType = (ImageView) view.findViewById(R.f_reminder_view.icon_type);
        mTimestamp = (TextView) view.findViewById(R.f_reminder_view.timestamp);
        mContactBadgeHolder = new ContactBadgeHolder(getActivity(),
                (ViewStub) view.findViewById(R.f_reminder_view.contact_badge));
        mPanelContact = view.findViewById(R.f_reminder_view.panelSelectContact);
        mAlarmTimstamp = (TextView) view
                .findViewById(R.f_reminder_view.alarmTimestamp);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final Intent intent = new Intent(getActivity(), RemindersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                getActivity().finish();
                return true;

            case R.menu.action_edit:
                final FragmentManager manager = getFragmentManager();
                final FragmentTransaction trx = manager.beginTransaction();
                trx.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out, android.R.anim.fade_in,
                        android.R.anim.fade_out);
                final Fragment viewFragment = manager
                        .findFragmentByTag(ReminderViewFragment.TAG);
                trx.detach(viewFragment);
                trx.add(R.x_reminder_details.root, new ReminderEditFragment(),
                        ReminderEditFragment.TAG);
                trx.addToBackStack(ReminderEditFragment.TAG);
                trx.commit();
                return true;

            case R.menu.action_delete:
                final long parseId = ContentUris.parseId(getActivity()
                        .getIntent().getData());
                ReminderUtils.deleteReminder(getActivity(), (int) parseId);
                getActivity().onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.view_reminder);
        final Cursor cursor = getActivity().getContentResolver().query(
                getActivity().getIntent().getData(), null, null, null, null);
        final ReminderEntry reminder = ReminderUtils.parse(cursor);
        bindView(reminder);

        final NfcAdapter nfcAdapter = NfcAdapter
                .getDefaultAdapter(getActivity());
        if (nfcAdapter != null) {
            final NdefMessage msg = new NdefMessage(
                    ReminderUtils.createNdefRecord(getActivity(), reminder));
            nfcAdapter.setNdefPushMessage(msg, getActivity());
        }

    }

    private void setContactBagdeData(final Uri uri,
            final OnClickListener removeClickListener) {
        mContactBadgeHolder.setData(uri, removeClickListener);
    }

    private void showContactDetails(final ReminderEntry reminder) {
        final Intent detailsIntent = ActivityUtils.getContactDetailsIntent(
                getActivity(), reminder);
        if (detailsIntent != null) {
            getActivity().startActivity(detailsIntent);
        }
    }
}
