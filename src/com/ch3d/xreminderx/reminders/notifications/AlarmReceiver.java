package com.ch3d.xreminderx.reminders.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.reminders.Consts;
import com.ch3d.xreminderx.reminders.ReminderIntent;
import com.ch3d.xreminderx.reminders.details.ReminderDetailsActivity;
import com.ch3d.xreminderx.reminders.model.ReminderEntry;
import com.ch3d.xreminderx.reminders.provider.RemindersProvider;
import com.ch3d.xreminderx.reminders.utils.ReminderUtils;

public class AlarmReceiver extends BroadcastReceiver
{
	private void onDismiss(final Context context, final ReminderEntry reminder)
	{
		final AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		final NotificationManager nManager = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final PendingIntent operation = ReminderUtils.getPendingAlarmOperation(context, reminder.getId());

		// cancel alarm
		aManager.cancel(operation);

		// cancel notification
		nManager.cancel(reminder.getId());

		// stop ringing
		stopRinging(context);
	}

	private void onOngoingDismiss(final Context context, final ReminderEntry reminder)
	{
		final NotificationManager nManager = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// cancel notification
		nManager.cancel(reminder.getId());
	}

	private void onPostpone(final Context context, final ReminderEntry reminder)
	{
		final AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		final NotificationManager nManager = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// cancel notification
		nManager.cancel(reminder.getId());

		// update reminder
		final int postponeTime = PreferenceManager.getDefaultSharedPreferences(context).getInt(
				Consts.PREFS.SHOW_REMINDER_POSTPONE_TIME, Consts.POSTPONE_TIME_DEFAULT);
		reminder.postpone(postponeTime);
		RemindersProvider.updateReminder(context, reminder);

		// setup alarm for updated reminder
		final Uri uri = ContentUris.withAppendedId(RemindersProvider.REMINDERS_URI, reminder.getId());
		final Cursor query2 = context.getContentResolver().query(uri, null, null, null, null);
		final ReminderEntry updatedReminder = ReminderUtils.parse(query2);
		final PendingIntent operation = ReminderUtils.getPendingAlarmOperation(context, reminder.getId());
		aManager.set(AlarmManager.RTC_WAKEUP, updatedReminder.getAlarmTimestamp(), operation);

		stopRinging(context);
	}

	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		final NotificationManager nManager = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// retrieve reminder
		final Uri reminderUri = intent.getData();
		final ContentResolver contentResolver = context.getContentResolver();
		final Cursor query = contentResolver.query(reminderUri, null, null, null, null);
		final ReminderEntry reminder = ReminderUtils.parse(query);

		if(ReminderIntent.ACTION_NOTIFICATION_DISMISS.equals(intent.getAction()))
		{
			onDismiss(context, reminder);
		}
		else if(ReminderIntent.ACTION_NOTIFICATION_ONGOING_DISMISS.equals(intent.getAction()))
		{
			onOngoingDismiss(context, reminder);
		}
		else if(ReminderIntent.ACTION_NOTIFICATION_SELECT.equals(intent.getAction()))
		{
			onSelect(context, reminderUri);
		}
		else if(ReminderIntent.ACTION_NOTIFICATION_POSTPONE.equals(intent.getAction()))
		{
			onPostpone(context, reminder);
		}
		else if(ReminderIntent.ACTION_NOTIFICATION_ONGOING.equals(intent.getAction()))
		{
			final String notificationText = reminder.getText();
			final Notification.Builder builder = new Builder(context);
			builder.setShowWhen(true);
			builder.setSmallIcon(R.drawable.ic_n_event);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_n_event));
			builder.setOnlyAlertOnce(false);
			builder.setAutoCancel(false);
			builder.setOngoing(true);
			builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
			builder.setTicker(notificationText);
			builder.setContentTitle(notificationText);
			builder.setContentText(ReminderUtils.formatDateTime(context, reminder.getTimestamp()));

			// click - view details
			final Intent cIntent = new Intent(context, AlarmReceiver.class);
			cIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_SELECT);
			cIntent.setData(reminderUri);
			builder.setContentIntent(PendingIntent.getBroadcast(context, 0, cIntent, PendingIntent.FLAG_CANCEL_CURRENT));

			final boolean hasAddressbookContact = ReminderUtils.hasAddressbookContact(context, reminder);
			if(reminder.isContactRelated() && hasAddressbookContact)
			{
				// action show contact details
				final Intent detIntent = new Intent(context, ContactDetailsActivity.class);
				detIntent.setData(reminderUri);
				detIntent.setAction(intent.getAction());
				detIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				final PendingIntent detailsIntent = PendingIntent.getActivity(context, 0, detIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				// start empty activity to stop ringer, cancel notification and start native contact details
				// workaround for status bar collapse issue:
				// 1) directly start native details activity - them you will not be able to stop ringer
				// 2) set broadcast intent to stop ringer and then start native details - activity will be launched
				// below status bar.
				builder.addAction(R.drawable.ic_n_contact_details, context.getString(R.string.contact), detailsIntent);
			}

			if(reminder.isContactRelated() && hasAddressbookContact)
			{
				final Notification.BigPictureStyle notification = new Notification.BigPictureStyle(builder);
				final Bitmap defImg = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.ic_contact_picture);
				final Bitmap fetchThumbnail = ReminderUtils.fetchThumbnail(context, reminder, defImg);
				notification.bigPicture(fetchThumbnail);
				nManager.notify(reminder.getId(), notification.build());
			}
			else
			{
				final Notification notification = builder.build();
				nManager.notify(reminder.getId(), notification);
			}
		}
		else if(ReminderIntent.ACTION_NOTIFICATION_SHOW.equals(intent.getAction()))
		{
			final String notificationText = reminder.getText();
			final Notification.Builder builder = new Builder(context);
			builder.setShowWhen(true);
			builder.setSmallIcon(R.drawable.ic_n_event);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_n_event));
			builder.setWhen(reminder.getAlarmTimestamp());
			builder.setOnlyAlertOnce(false);
			builder.setAutoCancel(true);
			builder.setPriority(NotificationCompat.PRIORITY_MAX);
			builder.setTicker(notificationText);
			builder.setContentTitle(notificationText);
			builder.setContentText(ReminderUtils.formatDateTime(context, reminder.getTimestamp()));
			builder.setDefaults(Notification.DEFAULT_ALL);

			// click - view details
			final Intent cIntent = new Intent(context, AlarmReceiver.class);
			cIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_SELECT);
			cIntent.setData(reminderUri);
			builder.setContentIntent(PendingIntent.getBroadcast(context, 0, cIntent, PendingIntent.FLAG_CANCEL_CURRENT));

			// delete intent
			final Intent dIntent = new Intent(context, AlarmReceiver.class);
			dIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_DISMISS);
			dIntent.setData(reminderUri);
			final PendingIntent deleteIntent = PendingIntent.getBroadcast(context, 0, dIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			builder.setDeleteIntent(deleteIntent);

			final boolean hasAddressbookContact = ReminderUtils.hasAddressbookContact(context, reminder);
			if(reminder.isContactRelated() && hasAddressbookContact)
			{
				// action show contact details
				final Intent detIntent = new Intent(context, ContactDetailsActivity.class);
				detIntent.setData(reminderUri);
				detIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				final PendingIntent detailsIntent = PendingIntent.getActivity(context, 0, detIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				// start empty activity to stop ringer, cancel notification and start native contact details
				// workaround for status bar collapse issue:
				// 1) directly start native details activity - them you will not be able to stop ringer
				// 2) set broadcast intent to stop ringer and then start native details - activity will be launched
				// below status bar.
				builder.addAction(R.drawable.ic_n_contact_details, context.getString(R.string.contact), detailsIntent);
			}
			else
			{
				// action dismiss
				builder.addAction(R.drawable.ic_n_dismiss, context.getString(R.string.dismiss), deleteIntent);
			}

			// action postpone
			final Intent pIntent = new Intent(context, AlarmReceiver.class);
			pIntent.setAction(ReminderIntent.ACTION_NOTIFICATION_POSTPONE);
			pIntent.setData(reminderUri);
			final PendingIntent postIntent = PendingIntent.getBroadcast(context, 0, pIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			builder.addAction(R.drawable.ic_n_postpone, context.getString(R.string.postpone), postIntent);

			if(reminder.isContactRelated() && hasAddressbookContact)
			{
				final Notification.BigPictureStyle notification = new Notification.BigPictureStyle(builder);
				final Bitmap defImg = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.ic_contact_picture);
				final Bitmap fetchThumbnail = ReminderUtils.fetchThumbnail(context, reminder, defImg);
				notification.bigPicture(fetchThumbnail);
				nManager.notify(reminder.getId(), notification.build());
			}
			else
			{
				final Notification notification = builder.build();
				nManager.notify(reminder.getId(), notification);
			}

			if(!reminder.isSilent())
			{
				startRinging(context, reminderUri);
			}
		}
	}

	private void onSelect(final Context context, final Uri data)
	{
		final Intent cIntent = new Intent(context, ReminderDetailsActivity.class);
		cIntent.setAction(Intent.ACTION_VIEW);
		cIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		cIntent.setData(data);
		context.startActivity(cIntent);
		context.stopService(new Intent(context, RingerService.class));
	}

	private void startRinging(final Context context, final Uri data)
	{
		final Intent ringerIntent = new Intent(context, RingerService.class);
		ringerIntent.setData(data);
		context.startService(ringerIntent);
	}

	private void stopRinging(final Context context)
	{
		context.stopService(new Intent(context, RingerService.class));
	}
}
