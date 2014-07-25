package com.ch3d.xreminderx.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.activity.RemindersActivity;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.ContactBadgeHolder;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

import static com.ch3d.xreminderx.utils.ViewUtils.setVisible;

public class ReminderViewFragment extends Fragment {
	public static final String TAG = "ReminderDetails";

	@InjectView(R.id.title)
	protected TextView mText;

	@InjectView(R.id.timestamp)
	protected TextView mTimestamp;

	@InjectView(R.id.alarm_timestamp)
	protected TextView mAlarmTimstamp;

	@InjectView(R.id.panel_select_contact)
	protected View mPanelContact;

	@InjectView(R.id.text_ongoing)
	protected TextView mOngoing;

	@InjectView(R.id.text_silent)
	protected TextView mSilent;

	@InjectView(R.id.color)
	protected View mColor;

	@InjectViews({R.id.alarm_timestamp, R.id.alarm_timestamp_delimiter, R.id.alarm_timestamp_header})
	protected List<View> alarmViews;

	private ContactBadgeHolder mContactBadgeHolder;

	private void bindContactData(final ReminderEntry reminder, final boolean isContactRelated) {
		if (isContactRelated) {
			mContactBadgeHolder.setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.btn_remove).setVisibility(View.GONE);
			mContactBadgeHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					showContactDetails(reminder);
				}
			});
			setContactBagdeData(reminder.getContactUri(), null);
		} else {
			mContactBadgeHolder.setVisibility(View.GONE);
		}
	}

	private void bindView(final ReminderEntry reminder) {
		ViewUtils.setAnimatedText(mText, reminder.getText());
		ViewUtils.setAnimatedText(mTimestamp, ReminderUtils.formatDateTimeShort(getActivity(), reminder.getTimestamp()));
		ViewUtils.setAnimatedText(mAlarmTimstamp, ReminderUtils.formatDateTimeShort(getActivity(), reminder.getAlarmTimestamp()));

		mColor.setBackgroundColor(reminder.getColor());
		setVisible(mOngoing, reminder.isOngoing());
		setVisible(mSilent, reminder.isSilent());

		final boolean isContactRelated = reminder.isContactRelated();
		setVisible(mPanelContact, isContactRelated);
		bindContactData(reminder, isContactRelated);
		setVisible(alarmViews, !reminder.isQuick());
	}

	private ReminderEntry getReminder() {
		final Cursor cursor = getActivity().getContentResolver().query(getActivity().getIntent().getData(), null, null, null, null);
		return ReminderUtils.parse(cursor);
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
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.f_reminder_view, container, false);
		ButterKnife.inject(this, view);
		mContactBadgeHolder = new ContactBadgeHolder(getActivity(), (ViewStub) view.findViewById(R.id.contact_badge));
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final FragmentActivity context = getActivity();
		switch (item.getItemId()) {
			case android.R.id.home:
				final Intent intent = new Intent(context, RemindersActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				context.finish();
				return true;

			case R.id.action_share:
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, getReminder().getText());
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));
				return true;

			case R.id.action_edit:
				final FragmentTransaction trx = getFragmentManager().beginTransaction();
				trx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
				final Fragment viewFragment = getFragmentManager().findFragmentByTag(ReminderViewFragment.TAG);
				trx.detach(viewFragment);
				trx.add(R.x_reminder_details.root, ReminderEditFragment.newInstance(getActivity().getIntent().getData()),
				        ReminderEditFragment.TAG);
				trx.addToBackStack(ReminderEditFragment.TAG);
				trx.commit();
				return true;

			case R.id.action_delete:
				final long parseId = ContentUris.parseId(context.getIntent().getData());
				ReminderUtils.deleteReminder(context, (int) parseId);
				context.finish();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getActivity().setTitle(R.string.view_reminder);
		final ReminderEntry reminder = getReminder();
		bindView(reminder);
		final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if (nfcAdapter != null) {
			final NdefMessage msg = new NdefMessage(ReminderUtils.createNdefRecord(getActivity(), reminder));
			nfcAdapter.setNdefPushMessage(msg, getActivity());
		}

	}

	private void setContactBagdeData(final Uri uri, final OnClickListener removeClickListener) {
		mContactBadgeHolder.setData(uri, removeClickListener);
	}

	private void showContactDetails(final ReminderEntry reminder) {
		final Intent detailsIntent = ActivityUtils.getContactDetailsIntent(getActivity(), reminder);
		if (detailsIntent != null) {
			getActivity().startActivity(detailsIntent);
		}
	}
}
