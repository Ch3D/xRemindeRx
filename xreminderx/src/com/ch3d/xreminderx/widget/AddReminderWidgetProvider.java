package com.ch3d.xreminderx.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ch3d.xreminderx.R;

public class AddReminderWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		final RemoteViews views = new RemoteViews(context.getPackageName(), com.ch3d.xreminderx.R.layout.widget_add);
		final Intent intent = new Intent(context, AddReminderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.widget_add.btnNew, pendingIntent);

		appWidgetManager.updateAppWidget(appWidgetIds[0], views);
	}
}
