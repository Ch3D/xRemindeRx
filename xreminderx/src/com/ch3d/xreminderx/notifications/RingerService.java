package com.ch3d.xreminderx.notifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ch3d.xreminderx.app.ReminderApplication;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.utils.ReminderUtils;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class RingerService extends Service implements Callback {
	public static final String TAG = RingerService.class.getSimpleName();
	private static final int PAUSE_INTERVAL = 5 * 1000;
	private static final float IN_CALL_VOLUME = 0.125f;
	private static final int MSG_PAUSE = 0;
	private static final int MSG_RESUME = 1;
	private final Set<Uri> dataSet = new HashSet<Uri>();
	private final IBinder mBinder = new RingerBinder();
	@Inject
	protected TelephonyManager mTelephonyManager;
	@Inject
	protected PowerManager mPowerManager;
	private int mInitialCallState;
	private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(
				final int state,
				final String ignored) {
			/*The user might already be	in a call when the alarm fires.	When we register
			onCallStateChanged,	we get the initial in-call state which kills the alarm.
			Check against the initial call state so	we don't kill the alarm during a call.*/
			if ((state != TelephonyManager.CALL_STATE_IDLE)
					&& (state != mInitialCallState)) {
				// TODO:
				// sendKillBroadcast(mCurrentAlarm);
				stopSelf();
			}
		}
	};
	private MediaPlayer mMediaPlayer;
	private boolean mPlaying;
	private WakeLock cpuWakeLock;

	private Handler mHandler;

	private ReminderEntry reminder;

	@Override
	public boolean handleMessage(final Message msg) {
		switch (msg.what) {
			case MSG_PAUSE:
				stop();
				mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_RESUME), PAUSE_INTERVAL);
				return true;

			case MSG_RESUME:
				try {
					play(reminder);
				} catch (final Exception e) {
					Log.e(TAG, "Unable to resume playing", e);
					stopSelf();
				}

			default:
				break;
		}
		return false;
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		((ReminderApplication) getApplication()).inject(this);

		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		cpuWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		cpuWakeLock.setReferenceCounted(false);
		mHandler = new Handler(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stop();
		mHandler.removeMessages(MSG_RESUME);
		mHandler.removeMessages(MSG_PAUSE);
		mHandler = null;
		mTelephonyManager.listen(mPhoneStateListener, 0);
		if ((cpuWakeLock != null) && cpuWakeLock.isHeld()) {
			cpuWakeLock.release();
		}
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if ((intent == null) || (intent.getData() == null)) {
			stopSelf();
		}
		if ((cpuWakeLock != null) && !cpuWakeLock.isHeld()) {
			cpuWakeLock.acquire();
		}

		final Uri data = intent.getData();
		dataSet.add(data);

		final Cursor cursor = getContentResolver().query(data, null, null, null, null);
		getContentResolver().registerContentObserver(data, false, new ContentObserver(mHandler) {
			@Override
			public void onChange(final boolean selfChange) {
				removeReminderRinger(data);
			}
		});

		reminder = ReminderUtils.parse(cursor);
		if (!mPlaying) {
			play(reminder);
		}

		return START_STICKY;
	}

	private void play(final ReminderEntry reminder) {
		stop();

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(final MediaPlayer mp, final int what, final int extra) {
				Log.e("RingerService", "Error occurred while playing audio.");
				mp.stop();
				mp.release();
				mMediaPlayer = null;
				return true;
			}
		});

		try {
			// Check if we are in a call. If we are, use the in-call alarm
			// resource at a low volume to not disrupt the call.
			if (mTelephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
				Log.v(TAG, "Using the in-call alarm");
				mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
			} else {
				mMediaPlayer.setDataSource(this,
						RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
			}
			startAlarm(mMediaPlayer);
		} catch (final Exception ex) {
			Log.v(TAG, "Using the fallback ringtone");
			// The alert may be on the sd card which could be busy right
			// now. Use the fallback ringtone.
			try {
				// Must reset the media player to clear the error state.
				mMediaPlayer.reset();
				// TODO:
				// setDataSourceFromResource(getResources(), mMediaPlayer,
				// R.raw.fallbackring);
				startAlarm(mMediaPlayer);
			} catch (final Exception ex2) {
				// At this point we just don't play anything.
				Log.e(TAG, "Failed to play fallback ringtone", ex2);
			}
		}
	}

	protected void removeReminderRinger(final Uri data) {
		dataSet.remove(data);
		if (dataSet.size() == 0) {
			stopSelf();
		}
	}

	private void startAlarm(final MediaPlayer player) throws java.io.IOException,
			IllegalArgumentException,
			IllegalStateException {
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// do not play alarms if stream volume is 0
		// (typically because ringer mode is silent).
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			player.setLooping(true);
			player.prepare();
			player.start();
			mPlaying = true;
			mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_PAUSE), PAUSE_INTERVAL);
		}
	}

	private void stop() {
		Log.v(TAG, "stop()");
		if (mPlaying) {
			mPlaying = false;

			// Stop audio playing
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}

			// Stop vibrator
			// mVibrator.cancel();
		}
		// disableKiller();
	}

	public void stopRing(final Uri data) {
		removeReminderRinger(data);
	}

	public class RingerBinder extends Binder {
		RingerService getService() {
			return RingerService.this;
		}
	}
}
