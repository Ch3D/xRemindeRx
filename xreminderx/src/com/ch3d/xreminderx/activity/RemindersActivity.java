package com.ch3d.xreminderx.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.app.BaseFragmentActivity;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.AnimationUtils;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.utils.StringUtils;
import com.ch3d.xreminderx.utils.ViewUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.Lazy;

public class RemindersActivity extends BaseFragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {
<<<<<<< HEAD
	public static final int ANIMATION_DURATION = 300;
	private static final int REQUEST_CODE_SIGN_IN = 0x02;
	@InjectView(android.R.id.edit)
	protected FloatLabeledEditText mEditQuickText;

	@InjectView(R.id.panel_bottom)
	protected View mBottomPanel;

	@InjectView(android.R.id.button1)
	protected ImageButton mBtnAdd;

	@InjectView(android.R.id.button2)
	protected ImageButton mBtnNew;

	@Inject
	Lazy<SearchManager> searchManager;

	private GoogleApiClient mGoogleApi;

	private ConnectionResult mConnectionResult;

	private boolean mSignInSelected;

	private boolean mIntentInProgress;

	private NdefMessage[] getNdefMessages(final Intent intent) {
		NdefMessage[] msgs = null;
		final Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];
			}
		}
		return msgs;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if ((resultCode == RESULT_OK) && (requestCode == REQUEST_CODE_SIGN_IN)) {
			mGoogleApi.connect();
		}
	}

	@OnClick(android.R.id.button2)
	public void onAddClick(final View v) {
		final String title = mEditQuickText.getText().toString().trim();
		if (StringUtils.isBlank(title)) {
			return;
		}
		final ReminderEntry reminder = ReminderFactory.createNew();
		reminder.setText(title);
		reminder.setOngoing(false);
		reminder.setSilent(true);
		reminder.setColor(Color.WHITE);

		mEditQuickText.getText().clear();
		ViewUtils.hideKeyboard(mEditQuickText.getEditText());
		RemindersProvider.addReminder(this, reminder, true);
	}

	@OnClick(android.R.id.button1)
	public void onNewClick(final View v) {
		setAddModeEnabled(true);
	}

	private void setAddModeEnabled(boolean enabled) {
		if (enabled) {
			ActivityUtils.showKeyboard(mEditQuickText.getEditText());
			AnimationUtils.animateEditText(mEditQuickText, mBtnAdd, true);
		} else {
			AnimationUtils.animateEditText(mEditQuickText, mBtnNew, false);
		}
		AnimationUtils.animateButton(mBtnNew, enabled);
		AnimationUtils.animateButton(mBtnAdd, !enabled);
	}

	@Override

	public void onConnected(final Bundle bundle) {
		invalidateOptionsMenu();

		if (mIntentInProgress) {
			// TODO: Ask to sync reminders into the cloud
		}

		mSignInSelected = false;
		mIntentInProgress = false;
		mConnectionResult = new ConnectionResult(ConnectionResult.SUCCESS, null);
		ActivityUtils.showToastShort(this, "Signed in as " + Plus.AccountApi.getAccountName(mGoogleApi));
	}

	@Override
	public void onConnectionFailed(final ConnectionResult result) {
		mConnectionResult = result;
		invalidateOptionsMenu();

		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
		}
		if (mIntentInProgress) {
			mSignInSelected = false;
		}
		if (mSignInSelected) {
			resolveSignInError();
		}
	}

	@Override
	public void onConnectionSuspended(final int arg0) {
		mSignInSelected = false;
		invalidateOptionsMenu();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final String action = intent.getAction();
		final String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				handleSendText(intent);
			}
		}

		mGoogleApi = ActivityUtils.getGoogleApi(this);
		mGoogleApi.registerConnectionCallbacks(this);
		mGoogleApi.registerConnectionFailedListener(this);
		mGoogleApi.connect();

		setContentView(R.layout.x_reminders);
		ButterKnife.inject(this);
		setTitle(R.string.reminders);

		final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				ActivityUtils.createKeyboardListener(activityRootView, new ActivityUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						if (!isVisible) {
							setAddModeEnabled(false);
						}
					}
				})
		                                                                );

		// parse intent in case if
		// application is not already running
		parseNfcIntent(getIntent());
	}

	private void handleSendText(final Intent intent) {
		final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (!StringUtils.isBlank(sharedText)) {
			ReminderEntry entry = ReminderFactory.createNew();
			entry.setText(sharedText);
			RemindersProvider.addReminder(this, entry);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.reminders_list, menu);
		final MenuItem menuItem = menu.findItem(R.menu.action_search);
		menuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(final MenuItem item) {
				// mBottomPanel.setVisibility(View.VISIBLE);
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(final MenuItem item) {
				setAddModeEnabled(false);
				// mBottomPanel.setVisibility(View.GONE);
				return true;
			}
		});
		final SearchView searchView = (SearchView) menuItem.getActionView();
		searchView.setSearchableInfo(searchManager.get().getSearchableInfo(getComponentName()));
		return true;
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		// parse intent in case if application is running
		parseNfcIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.menu.action_gplus_signin) {
			mSignInSelected = true;
			mGoogleApi.connect();
			return true;
		} else if (item.getItemId() == R.menu.action_gplus_signout) {
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApi);
			mGoogleApi.disconnect();
			mGoogleApi.connect();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if ((mConnectionResult != null) && mConnectionResult.isSuccess()) {
			mGoogleApi.connect();
			mConnectionResult = null;
		}
		final boolean connected = mGoogleApi.isConnected();
		menu.findItem(R.menu.action_gplus_signin).setVisible(!connected);
		menu.findItem(R.menu.action_gplus_signout).setVisible(connected);
		return true;
	}

	private void parseNdefMessages(final NdefMessage[] msgs) {
		if (msgs == null) {
			return;
		}
		for (final NdefMessage msg : msgs) {
			for (final NdefRecord rec : msg.getRecords()) {
				final ReminderEntry reminder = ReminderUtils.parseReminder(rec);
				final Cursor cursor = getContentResolver()
						.query(RemindersProvider.REMINDERS_URI, null, "text = ? AND ts = ? AND alarm_ts = ?",
						       new String[]{reminder.getText(), Long.toString(reminder.getTimestamp()),
								       Long.toString(reminder.getAlarmTimestamp())}, null
						      );
				if (cursor.getCount() > 0) {
					final AlertDialog.Builder builder = new Builder(this);
					builder.setTitle(R.string.reminder_is_already_exist);
					builder.setMessage(reminder.getText());
					builder.setPositiveButton(R.string.add, new OnClickListener() {
						                          @Override
						                          public void onClick(final DialogInterface dialog, final int which) {
							                          RemindersProvider.addReminder(RemindersActivity.this, reminder);
							                          dialog.dismiss();
						                          }
					                          }
					                         );
					builder.setNegativeButton(R.string.cancel, new OnClickListener() {
						                          @Override
						                          public void onClick(final DialogInterface dialog, final int which) {
							                          dialog.dismiss();
						                          }
					                          }
					                         );
					builder.show();
				} else {
					RemindersProvider.addReminder(this, reminder, true);
				}
			}
		}
	}

	private void parseNfcIntent(final Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			final NdefMessage[] msgs = getNdefMessages(intent);
			parseNdefMessages(msgs);
		}
	}

	private void resolveSignInError() {
		if ((mConnectionResult != null) && mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
			} catch (final SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApi.connect();
			}
		}
	}
=======
    public static final int ANIMATION_DURATION = 300;
    private static final int REQUEST_CODE_SIGN_IN = 0x02;
    @InjectView(android.R.id.edit)
    protected FloatLabeledEditText mEditQuickText;

    @InjectView(R.id.panel_bottom)
    protected View mBottomPanel;

    @InjectView(android.R.id.button1)
    protected ImageButton mBtnAdd;

    @InjectView(android.R.id.button2)
    protected ImageButton mBtnNew;

    @Inject
    Lazy<SearchManager> searchManager;

    private GoogleApiClient mGoogleApi;

    private ConnectionResult mConnectionResult;

    private boolean mSignInSelected;

    private boolean mIntentInProgress;

    public static void animateButton(final ImageButton btn, final boolean visible) {
        final int visibility = visible ? View.VISIBLE : View.GONE;
        if (visible) {
            btn.setVisibility(visibility);
        }
        btn.animate().setDuration(ANIMATION_DURATION).
                setInterpolator(new AccelerateInterpolator()).
                alpha(visible ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                btn.setVisibility(visibility);
            }
        }).start();
    }

    public static void animateEditText(final FloatLabeledEditText floatView, final View anchor, final boolean visible) {
        ValueAnimator widthAnimator = null;
        floatView.setVisibility(View.VISIBLE);
        if (!visible) {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            widthAnimator = ValueAnimator.ofInt(location[0], 0);
        } else {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            widthAnimator = ValueAnimator.ofInt(0, location[0]);
        }

        widthAnimator.setInterpolator(new AccelerateInterpolator());
        widthAnimator.setDuration(ANIMATION_DURATION);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                floatView.getLayoutParams().width = (Integer) animation.getAnimatedValue();
                floatView.requestLayout();
            }
        });
        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (visible) {
                    // ActivityUtils.showKeyboard(mEditQuickText.getEditText());
                } else {
                    ActivityUtils.hideKeyboard(floatView.getEditText());
                }
            }
        });
        widthAnimator.start();
    }

    private NdefMessage[] getNdefMessages(final Intent intent) {
        NdefMessage[] msgs = null;
        final Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
        }
        return msgs;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if ((resultCode == RESULT_OK) && (requestCode == REQUEST_CODE_SIGN_IN)) {
            mGoogleApi.connect();
        }
    }

    @OnClick(android.R.id.button2)
    public void onAddClick(final View v) {
        final String title = mEditQuickText.getText().toString().trim();
        if (StringUtils.isBlank(title)) {
            return;
        }
        final ReminderEntry reminder = ReminderFactory.createNew();
        reminder.setText(title);
        reminder.setOngoing(false);
        reminder.setSilent(true);
        reminder.setColor(Color.WHITE);

        mEditQuickText.getText().clear();
        ViewUtils.hideKeyboard(mEditQuickText.getEditText());
        RemindersProvider.addReminder(this, reminder, true);
    }

    @OnClick(android.R.id.button1)
    public void onNewClick(final View v) {
        setAddModeEnabled(true);
    }

    private void setAddModeEnabled(boolean enabled) {
        if (enabled) {
            ActivityUtils.showKeyboard(mEditQuickText.getEditText());
            animateEditText(mEditQuickText, mBtnAdd, true);
        } else {
            animateEditText(mEditQuickText, mBtnNew, false);
        }
        animateButton(mBtnNew, enabled);
        animateButton(mBtnAdd, !enabled);
    }

    @Override

    public void onConnected(final Bundle bundle) {
        invalidateOptionsMenu();

        if (mIntentInProgress) {
            // TODO: Ask to sync reminders into the cloud
        }

        mSignInSelected = false;
        mIntentInProgress = false;
        mConnectionResult = new ConnectionResult(ConnectionResult.SUCCESS, null);
        ActivityUtils.showToastShort(this, "Signed in as " + Plus.AccountApi.getAccountName(mGoogleApi));
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
        mConnectionResult = result;
        invalidateOptionsMenu();

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
        }
        if (mIntentInProgress) {
            mSignInSelected = false;
        }
        if (mSignInSelected) {
            resolveSignInError();
        }
    }

    @Override
    public void onConnectionSuspended(final int arg0) {
        mSignInSelected = false;
        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }

        mGoogleApi = ActivityUtils.getGoogleApi(this);
        mGoogleApi.registerConnectionCallbacks(this);
        mGoogleApi.registerConnectionFailedListener(this);
        mGoogleApi.connect();

        setContentView(R.layout.x_reminders);
        ButterKnife.inject(this);
        setTitle(R.string.reminders);

        final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(ActivityUtils.createKeyboardListener(activityRootView, new ActivityUtils.KeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    setAddModeEnabled(false);
                }
            }
        }));

        // parse intent in case if
        // application is not already running
        parseNfcIntent(getIntent());
    }

    private void handleSendText(final Intent intent) {
        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (!StringUtils.isBlank(sharedText)) {
            ReminderEntry entry = ReminderFactory.createNew();
            entry.setText(sharedText);
            RemindersProvider.addReminder(this, entry);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.reminders_list, menu);
        final MenuItem menuItem = menu.findItem(R.menu.action_search);
        menuItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                // mBottomPanel.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                setAddModeEnabled(false);
                // mBottomPanel.setVisibility(View.GONE);
                return true;
            }
        });
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.get().getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        // parse intent in case if application is running
        parseNfcIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.menu.action_gplus_signin) {
            mSignInSelected = true;
            mGoogleApi.connect();
            return true;
        } else if (item.getItemId() == R.menu.action_gplus_signout) {
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApi);
            mGoogleApi.disconnect();
            mGoogleApi.connect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if ((mConnectionResult != null) && mConnectionResult.isSuccess()) {
            mGoogleApi.connect();
            mConnectionResult = null;
        }
        final boolean connected = mGoogleApi.isConnected();
        menu.findItem(R.menu.action_gplus_signin).setVisible(!connected);
        menu.findItem(R.menu.action_gplus_signout).setVisible(connected);
        return true;
    }

    private void parseNdefMessages(final NdefMessage[] msgs) {
        if (msgs == null) {
            return;
        }
        for (final NdefMessage msg : msgs) {
            for (final NdefRecord rec : msg.getRecords()) {
                final ReminderEntry reminder = ReminderUtils.parseReminder(rec);
                final Cursor cursor = getContentResolver().query(RemindersProvider.REMINDERS_URI, null, "text = ? AND ts = ? AND alarm_ts = ?", new String[]{reminder.getText(), Long.toString(reminder.getTimestamp()), Long.toString(reminder.getAlarmTimestamp())}, null);
                if (cursor.getCount() > 0) {
                    final AlertDialog.Builder builder = new Builder(this);
                    builder.setTitle(R.string.reminder_is_already_exist);
                    builder.setMessage(reminder.getText());
                    builder.setPositiveButton(R.string.add, new OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    RemindersProvider.addReminder(RemindersActivity.this, reminder);
                                    dialog.dismiss();
                                }
                            }
                    );
                    builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    dialog.dismiss();
                                }
                            }
                    );
                    builder.show();
                } else {
                    RemindersProvider.addReminder(this, reminder, true);
                }
            }
        }
    }

    private void parseNfcIntent(final Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            final NdefMessage[] msgs = getNdefMessages(intent);
            parseNdefMessages(msgs);
        }
    }

    private void resolveSignInError() {
        if ((mConnectionResult != null) && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
            } catch (final SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApi.connect();
            }
        }
    }
>>>>>>> 4109440443e784accd8ac6d55374fef38e134cc4
}
