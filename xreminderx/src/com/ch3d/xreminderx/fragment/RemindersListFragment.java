package com.ch3d.xreminderx.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.activity.ReminderDetailsActivity;
import com.ch3d.xreminderx.activity.SettingsActivity;
import com.ch3d.xreminderx.adapter.RemindersAdapter;
import com.ch3d.xreminderx.adapter.RemindersAdapter.ViewHolder;
import com.ch3d.xreminderx.app.ReminderApplication;
import com.ch3d.xreminderx.loader.RemindersLoader;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.sync.RemoteSynchronizer;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.PreferenceHelper;
import com.ch3d.xreminderx.utils.ReminderIntent;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.view.SwipeDismissListViewTouchListener;
import com.ch3d.xreminderx.view.SwipeDismissListViewTouchListener.DismissCallbacks;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import javax.inject.Inject;

public class RemindersListFragment extends ListFragment implements LoaderCallbacks<Cursor>, RemoteSynchronizer.Callback<Object> {

	public static final int TAG_TODAY = 0;
	public static final int TAG_ALL = 1;

	@Inject
	RemoteSynchronizer<Object> mRemoteSynchronizer;

//	@Inject
//	RemoteSyncProtocol mSyncProtocol;

	private ActionMode mActionMode;

	private final MultiChoiceModeListener mActionModeCallback = new MultiChoiceModeListener() {

		@Override
		public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
			return onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
			mActionMode = mode;
			final MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.reminders_list_contextual, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(final ActionMode mode) {
			mAdapter.uncheckItems();
			mActionMode = null;
		}

		@Override
		public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {
			mAdapter.setChecked(position, checked);
		}

		@Override
		public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
			return false;
		}
	};
	private RemindersAdapter mAdapter;
	private SwipeDismissListViewTouchListener mSwipeListener;
	private Handler mHandler;
	private SwipeRefreshLayout mSwipeLayout;

	public RemindersListFragment() {
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ReminderApplication) getActivity().getApplication()).inject(this);
		setHasOptionsMenu(true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int arg0, final Bundle bundle) {
		return new RemindersLoader(getActivity(), bundle);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.f_swipe_refresh_list, container, false);

		mHandler = new Handler();
		final LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(0, getArguments(), this);
		mAdapter = new RemindersAdapter(getActivity(), null, true);
		return view;
	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		if (mActionMode == null) {
			ActivityUtils.startDetailsActivity(getActivity(), v, position);
		} else {
			getListView().setItemChecked(position, true);
			getListView().setSelection(position);
			v.setSelected(true);
		}
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
		cursor.setNotificationUri(getActivity().getContentResolver(), RemindersProvider.REMINDERS_URI);
		final ContentObserver contentObserver = new ContentObserver(mHandler) {
			@Override
			public boolean deliverSelfNotifications() {
				return true;
			}

			@Override
			public void onChange(final boolean selfChange) {
				super.onChange(selfChange);
				mSwipeListener.releaseTransientViews();
			}
		};
		getActivity().getContentResolver().registerContentObserver(RemindersProvider.REMINDERS_URI, true, contentObserver);
		mAdapter.swapCursor(cursor);
		final SwingBottomInAnimationAdapter animatedAdapter = new SwingBottomInAnimationAdapter(mAdapter);
		animatedAdapter.setAbsListView(getListView());
		setListAdapter(animatedAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.menu.action_new_reminder:
				startActivity(ReminderDetailsActivity.newIntent(getActivity(), ReminderIntent.ACTION_NEW));
				return true;

			case R.menu.action_remove:
				final int[] mChecked = mAdapter.getCheckedItems();
				for (int cItem : mChecked) {
					final View view = getListView().getChildAt(cItem);
					final ViewHolder tag = (ViewHolder) view.getTag();
					removeReminder(view, (int) tag.id);
				}
				mAdapter.uncheckItems();
				if (mActionMode != null) {
					mActionMode.finish();
				}
				return true;

			case R.menu.action_settings:
				startActivity(new Intent(getActivity(), SettingsActivity.class));
				return true;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		mSwipeListener.releaseTransientViews();
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// setEmptyText(getActivity().getString(R.string.you_have_no_reminders));
		final ListView listView = getListView();
		mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_orange_light,
		                            android.R.color.holo_red_light);
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				performCloudSync();
			}
		});

		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(mActionModeCallback);
		listView.setFooterDividersEnabled(false);
		listView.addFooterView(View.inflate(getActivity(), R.layout.footer_reminders, null), null, false);

		mSwipeListener = new SwipeDismissListViewTouchListener(listView, mSwipeLayout, new DismissCallbacks() {
			@Override
			public boolean canDismiss(final int position) {
				return mActionMode == null;
			}

			@Override
			public void onDismiss(final ListView listView, final int[] reverseSortedPositions) {
				removeReminders(reverseSortedPositions);
			}
		});
		listView.setOnTouchListener(mSwipeListener);
		listView.setOnScrollListener(mSwipeListener.makeScrollListener());
	}

	protected void performCloudSync() {
		mRemoteSynchronizer.sync(this);
	}

	public void removeReminder(final View convertView, final int id) {
		final PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[2];
		arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat(View.X, 800);
		arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

		final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(convertView, arrayOfPropertyValuesHolder);
		anim.setDuration(300);
		ViewCompat.setHasTransientState(convertView, true);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				ReminderUtils.deleteReminder(convertView.getContext(), id);
				convertView.post(new Runnable() {
					@Override
					public void run() {
						convertView.setX(0);
						convertView.setAlpha(1);
						ViewCompat.setHasTransientState(convertView, false);
					}
				});
			}
		});
		anim.start();
	}

	private void removeReminders(final int[] reverseSortedPositions) {
		final FragmentActivity context = getActivity();
		if (PreferenceHelper.isShowDisplayPrompt(context)) {
			final View dialogView = View.inflate(context, R.layout.f_dialog_remove_promt, null);
			final CheckBox cb = (CheckBox) dialogView.findViewById(R.f_dialog_remove_promt.checkboxPromt);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
					PreferenceHelper.setShowDisplayPrompt(context, !isChecked);
				}
			});

			final AlertDialog.Builder builder = new Builder(context);
			builder.setView(dialogView);
			builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					for (final int position : reverseSortedPositions) {
						final ViewGroup view = (ViewGroup) getListView().getChildAt(position);
						final ViewHolder tag = (ViewHolder) view.getTag();
						ReminderUtils.deleteReminder(context, (int) tag.id);
					}
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					dialog.dismiss();
				}
			});
			builder.show();
		} else {
			for (final int position : reverseSortedPositions) {
				final View view = getListView().getChildAt(position);
				final ViewHolder tag = (ViewHolder) view.getTag();
				if (tag != null) {
					ReminderUtils.deleteReminder(context, (int) tag.id);
				}
			}
		}
	}

	@Override
	public void onPreExecute() {
		mSwipeLayout.setRefreshing(true);
	}

	@Override
	public void onPostExecute(Object result) {
		mSwipeLayout.setRefreshing(false);
	}
}