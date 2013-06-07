package com.ch3d.xreminderx.reminders.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.ch3d.xreminderx.R;

public class SettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.f_settings);
	}
}
