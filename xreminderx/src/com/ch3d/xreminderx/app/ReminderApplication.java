package com.ch3d.xreminderx.app;

import android.app.Application;
import android.util.Log;

import com.ch3d.xreminderx.activity.events.ReminderAddedEvent;
import com.ch3d.xreminderx.module.ActivityModule;
import com.ch3d.xreminderx.module.AndroidModule;
import com.ch3d.xreminderx.module.ParseCloudModule;
import com.ch3d.xreminderx.module.ParseSyncProtocolModule;
import com.ch3d.xreminderx.module.ServiceModule;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class ReminderApplication extends Application {
	private final List<Object> injectList = new ArrayList<Object>();
	private ObjectGraph mGraph;
	private GoogleApiClient mPlusClient;
	private Bus mEventBus;

	public GoogleApiClient getGoogleApi() {
		return mPlusClient;
	}

	protected List<Object> getModules() {
		return Arrays.asList(new AndroidModule(this), new ActivityModule(this), new ServiceModule(), new ParseCloudModule(),
		                     new ParseSyncProtocolModule());
	}

	public void inject(final Object object) {
		if (mGraph == null) {
			injectList.add(object);
		} else {
			mGraph.inject(object);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Parse.initialize(this, "64LrLB3jNDJQq8sSApLLHUWbjv2wmiACyemSLfN3",
		// "u1rLwcDJzwZuNYa06M0ODtsMqmNbYU9MPKwnjG3E");

		mPlusClient =
				new GoogleApiClient.Builder(this).addApi(Plus.API, null).addScope(Plus.SCOPE_PLUS_LOGIN).addScope(Plus.SCOPE_PLUS_PROFILE)
						.build();
		mPlusClient.connect();

		mEventBus = new Bus();
		mEventBus.register(this);

		mGraph = ObjectGraph.create(getModules().toArray());
		for (final Object obj : injectList) {
			mGraph.inject(obj);
		}
		injectList.clear();
	}

	@Subscribe
	public void onReminderAdded(ReminderAddedEvent event) {
		Log.e("!!!!!!", "value=" + event.getValue());
	}

	public Bus getEventBus() {
		return mEventBus;
	}
}