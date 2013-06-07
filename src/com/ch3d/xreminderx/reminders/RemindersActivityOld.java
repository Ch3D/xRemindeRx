package com.ch3d.xreminderx.reminders;

import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.reminders.RemindersAdapter.ViewHolder;
import com.ch3d.xreminderx.reminders.details.ReminderDetailsActivity;
import com.ch3d.xreminderx.reminders.loader.RemindersLoader;
import com.ch3d.xreminderx.reminders.provider.RemindersProvider;
import com.ch3d.xreminderx.reminders.utils.ActivityUtils;

public class RemindersActivityOld extends FragmentActivity
{
	public static class RemindersListFragment extends ListFragment implements LoaderCallbacks<Cursor>,
			OnItemLongClickListener
	{
		public static final String	TAG			= "tag_reminders";

		public static final int		TAG_TODAY	= 0;

		public static final int		TAG_ALL		= 1;

		private RemindersAdapter	mAdapter;

		public RemindersListFragment()
		{
		}

		@Override
		public Loader<Cursor> onCreateLoader(final int arg0, final Bundle bundle)
		{
			return new RemindersLoader(getActivity(), bundle);
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState)
		{
			final View view = super.onCreateView(inflater, container, savedInstanceState);

			final LoaderManager loaderManager = getLoaderManager();
			loaderManager.initLoader(0, getArguments(), this);
			// mAdapter = new RemindersAdapter((RemindersActivityOld)getActivity(), null, true);

			return view;
		}

		@Override
		public boolean onItemLongClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3)
		{
			return false;
		}

		@Override
		public void onListItemClick(final ListView l, final View v, final int position, final long id)
		{
			final RemindersAdapter.ViewHolder holder = (ViewHolder)v.getTag();
			final Intent intent = ActivityUtils.createReminderViewIntent(getActivity(), holder.id);
			startActivity(intent);
		}

		@Override
		public void onLoaderReset(final Loader<Cursor> arg0)
		{
			mAdapter.swapCursor(null);
		}

		@Override
		public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
		{
			cursor.setNotificationUri(getActivity().getContentResolver(), RemindersProvider.REMINDERS_URI);
			mAdapter.swapCursor(cursor);
			setListAdapter(mAdapter);
		}

		@Override
		public void onViewCreated(final View view, final Bundle savedInstanceState)
		{
			super.onViewCreated(view, savedInstanceState);
			setEmptyText(getActivity().getString(R.string.you_have_no_reminders));
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		private static final int	TABS_COUNT	= 2;

		public SectionsPagerAdapter(final FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount()
		{
			return TABS_COUNT;
		}

		@Override
		public Fragment getItem(final int position)
		{
			final Fragment fragment = new RemindersListFragment();
			final Bundle args = new Bundle();
			args.putInt(RemindersListFragment.TAG, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(final int position)
		{
			final Locale l = Locale.getDefault();
			switch(position)
			{
				case 0:
					return getString(R.string.title_today).toUpperCase(l);
				case 1:
					return getString(R.string.title_all).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
	 * If this becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter	mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager				mViewPager;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.x_main);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		getMenuInflater().inflate(R.menu.reminders_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.menu.action_new_reminder:
				final Intent intent = new Intent(this, ReminderDetailsActivity.class);
				intent.setAction(ReminderIntent.ACTION_NEW);
				startActivity(intent);
				return true;

			case R.menu.action_settings:
				// final long ts = System.currentTimeMillis();
				// final ContentValues values = new ContentValues();
				// values.put(RemindersContract.Columns.TYPE, 0);
				// values.put(RemindersContract.Columns.TEXT, "TEST TEST " + ts);
				// values.put(RemindersContract.Columns.ALARM_TIMESTAMP, ts);
				// values.put(RemindersContract.Columns.TIMESTAMP, ts);
				// getContentResolver().insert(RemindersProvider.REMINDERS_URI, values);
				// getContentResolver().notifyChange(RemindersProvider.REMINDERS_URI, null);
				return true;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
