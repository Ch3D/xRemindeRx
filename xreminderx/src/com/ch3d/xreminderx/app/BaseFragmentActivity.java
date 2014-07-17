package com.ch3d.xreminderx.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.squareup.otto.Bus;

import javax.inject.Inject;

public abstract class BaseFragmentActivity extends FragmentActivity {
	@Inject
	protected Bus eventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ReminderApplication) getApplication()).inject(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		eventBus.register(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		eventBus.unregister(this);
	}
}