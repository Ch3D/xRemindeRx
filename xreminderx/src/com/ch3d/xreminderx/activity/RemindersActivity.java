
package com.ch3d.xreminderx.activity;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.SearchView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.app.BaseFragmentActivity;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.StringUtils;
import com.ch3d.xreminderx.utils.ViewUtils;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import dagger.Lazy;

public class RemindersActivity extends BaseFragmentActivity implements
        android.view.View.OnClickListener
{
    @InjectView(android.R.id.edit)
    protected FloatLabeledEditText mEditQuickText;

    @InjectView(R.x_reminders.panel_bottom)
    protected View                 mBottomPanel;

    @Inject
    Lazy<SearchManager>            searchManager;

    private NdefMessage[] getNdefMessages(final Intent intent)
    {
        NdefMessage[] msgs = null;
        final Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null)
        {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++)
            {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
        }
        return msgs;
    }

    @Override
    @OnClick(android.R.id.button1)
    public void onClick(final View v)
    {
        final String title = mEditQuickText.getText().toString().trim();
        if (StringUtils.isBlank(title))
        {
            return;
        }
        final ReminderEntry reminder = ReminderFactory.createNull();
        reminder.setText(title);
        reminder.setOngoing(false);
        reminder.setSilent(true);
        reminder.setColor(Color.WHITE);

        mEditQuickText.getText().clear();
        ViewUtils.hideKeyboard(mEditQuickText.getEditText());
        RemindersProvider.addReminder(this, reminder, true);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.x_reminders);
        ButterKnife.inject(this);
        setTitle(R.string.reminders);

        // parse intent in case if
        // application is not already running
        parseNfcIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        getMenuInflater().inflate(R.menu.reminders_list, menu);
        final MenuItem menuItem = menu.findItem(R.menu.action_search);
        menuItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                mBottomPanel.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                mBottomPanel.setVisibility(View.GONE);
                return true;
            }
        });
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.get().getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(final Intent intent)
    {
        super.onNewIntent(intent);
        // parse intent in case if application is running
        parseNfcIntent(intent);
    }

    private void parseNdefMessages(final NdefMessage[] msgs)
    {
        if (msgs == null)
        {
            return;
        }
        for (final NdefMessage msg : msgs)
        {
            for (final NdefRecord rec : msg.getRecords())
            {
                final ReminderEntry reminder = ReminderUtils.parseReminder(rec);
                final Cursor cursor = getContentResolver().query(
                        RemindersProvider.REMINDERS_URI,
                        null,
                        "text = ? AND ts = ? AND alarm_ts = ?",
                        new String[] {
                                reminder.getText(),
                                Long.toString(reminder.getTimestamp()),
                                Long.toString(reminder.getAlarmTimestamp())
                        },
                        null);
                if (cursor.getCount() > 0)
                {
                    final AlertDialog.Builder builder = new Builder(this);
                    builder.setTitle(R.string.reminder_is_already_exist);
                    builder.setMessage(reminder.getText());
                    builder.setPositiveButton(R.string.add,
                            new OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int which)
                                {
                                    RemindersProvider.addReminder(
                                            RemindersActivity.this, reminder);
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton(R.string.cancel,
                            new OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int which)
                                {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
                else
                {
                    RemindersProvider.addReminder(this, reminder, true);
                }
            }
        }
    }

    private void parseNfcIntent(final Intent intent)
    {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            final NdefMessage[] msgs = getNdefMessages(intent);
            parseNdefMessages(msgs);
        }
    }
}
