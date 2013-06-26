package com.ch3d.xreminderx.fragment;

import android.animation.LayoutTransition;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
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
import com.ch3d.xreminderx.loader.RemindersLoader;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.ch3d.xreminderx.utils.ActivityUtils;
import com.ch3d.xreminderx.utils.PreferenceHelper;
import com.ch3d.xreminderx.utils.ReminderIntent;
import com.ch3d.xreminderx.utils.ReminderUtils;
import com.ch3d.xreminderx.view.SwipeDismissListViewTouchListener;
import com.ch3d.xreminderx.view.SwipeDismissListViewTouchListener.DismissCallbacks;

import java.util.Iterator;

public class RemindersListFragment extends ListFragment implements
LoaderCallbacks<Cursor> {
    private ActionMode						mActionMode;

    private final MultiChoiceModeListener	mActionModeCallback	= new MultiChoiceModeListener() {

        // Called
        // when the
        // user
        // selects a
        // contextual
        // menu
        // item
        @Override
        public boolean onActionItemClicked(
                final ActionMode mode,
                final MenuItem item) {
            return onOptionsItemSelected(item);
        }

        // Called
        // when the
        // action
        // mode is
        // created;
        // startActionMode()
        // was
        // called
        @Override
        public boolean onCreateActionMode(
                final ActionMode mode,
                final Menu menu) {
            mActionMode = mode;
            final MenuInflater inflater = mode
                    .getMenuInflater();
            inflater.inflate(
                    R.menu.reminders_list_contextual,
                    menu);
            return true;
        }

        // Called
        // when the
        // user
        // exits the
        // action
        // mode
        @Override
        public void onDestroyActionMode(
                final ActionMode mode) {
            mAdapter.uncheckItems();
            mActionMode = null;
        }

        @Override
        public void onItemCheckedStateChanged(
                final ActionMode mode,
                final int position,
                final long id,
                final boolean checked) {
            mAdapter.setChecked(
                    position,
                    checked);
        }

        // Called
        // each time
        // the
        // action
        // mode is
        // shown.
        // Always
        // called
        // after
        // onCreateActionMode,
        // but
        // may be
        // called
        // multiple
        // times if
        // the mode
        // is
        // invalidated.
        @Override
        public boolean onPrepareActionMode(
                final ActionMode mode,
                final Menu menu) {
            return false; // Return
            // false
            // if
            // nothing
            // is
            // done
        }
    };

    public static final String				TAG					= "tag_reminders";

    public static final int					TAG_TODAY			= 0;

    public static final int					TAG_ALL				= 1;

    private RemindersAdapter				mAdapter;

    public RemindersListFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int arg0, final Bundle bundle) {
        return new RemindersLoader(getActivity(), bundle);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.reminders_list, menu);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container,
                savedInstanceState);

        final LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, getArguments(), this);
        mAdapter = new RemindersAdapter(getActivity(), null, true);

        return view;
    }

    @Override
    public void onListItemClick(final ListView l, final View v,
            final int position, final long id) {
        if (mActionMode == null) {
            final RemindersAdapter.ViewHolder holder = (ViewHolder) v.getTag();
            final Intent intent = new Intent(getActivity(),
                    ReminderDetailsActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(ContentUris.withAppendedId(
                    RemindersProvider.REMINDERS_URI, holder.id));
            if (ActivityUtils.isJeallyBean()) {
                final ActivityOptions options = ActivityOptions
                        .makeCustomAnimation(getActivity(),
                                android.R.anim.fade_in, android.R.anim.fade_out);
                getActivity().startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
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
        cursor.setNotificationUri(getActivity().getContentResolver(),
                RemindersProvider.REMINDERS_URI);
        mAdapter.swapCursor(cursor);
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.menu.action_new_reminder:
                final Intent intent = new Intent(getActivity(),
                        ReminderDetailsActivity.class);
                intent.setAction(ReminderIntent.ACTION_NEW);
                startActivity(intent);
                return true;

            case R.menu.action_remove:
                final Iterator<Integer> iterator = mAdapter
                .getCheckedItemPositions();
                while (iterator.hasNext()) {
                    final Integer next = iterator.next();
                    final View view = getListView().getChildAt(next);
                    final ViewHolder tag = (ViewHolder) view.getTag();
                    final long idLong = tag.id;
                    mAdapter.removeReminder(view, (int) idLong);
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
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getActivity().getString(R.string.you_have_no_reminders));
        final ListView listView = getListView();

        listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listView.setLayoutTransition(new LayoutTransition());
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(mActionModeCallback);
        listView.addFooterView(View.inflate(getActivity(),
                R.layout.footer_reminders, null));

        listView.setOnTouchListener(new SwipeDismissListViewTouchListener(
                listView, new DismissCallbacks() {
                    @Override
                    public boolean canDismiss(final int position) {
                        return mActionMode == null;
                    }

                    @Override
                    public void onDismiss(final ListView listView,
                            final int[] reverseSortedPositions) {
                        removeReminders(reverseSortedPositions);
                    }


                }));
    }

    private void removeReminders(
            final int[] reverseSortedPositions) {
        final FragmentActivity context = getActivity();
        if (PreferenceHelper.isShowDisplayPrompt(context)) {
            final View dialogView = View.inflate(context,
                    R.layout.f_dialog_remove_promt, null);
            final CheckBox cb = (CheckBox) dialogView
                    .findViewById(R.f_dialog_remove_promt.checkboxPromt);
            cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(
                        final CompoundButton buttonView,
                        final boolean isChecked) {
                    PreferenceHelper.setShowDisplayPrompt(
                            context, !isChecked);
                }
            });
            final AlertDialog.Builder builder = new Builder(
                    context);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.remove,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(
                        final DialogInterface dialog,
                        final int which) {
                    for (final int position : reverseSortedPositions) {
                        final ViewGroup view = (ViewGroup) getListView()
                                .getChildAt(position);
                        final ViewHolder tag = (ViewHolder) view
                                .getTag();
                        ReminderUtils.deleteReminder(
                                context, (int) tag.id);
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(
                        final DialogInterface dialog,
                        final int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            for (final int position : reverseSortedPositions) {
                final ViewGroup view = (ViewGroup) getListView()
                        .getChildAt(position);
                final ViewHolder tag = (ViewHolder) view
                        .getTag();
                ReminderUtils.deleteReminder(context,
                        (int) tag.id);
            }
        }
    }
}
