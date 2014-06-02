
package com.ch3d.xreminderx.view;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ListViewObserveHelper implements OnScrollListener {
    public interface Callback {
        public static final int STATE_INVISIBLE = 0;
        public static final int STATE_VISIBLE = 1;

        public void onStateChanged(int state);
    }

    public static ListViewObserveHelper attach(final ListView view, final Callback callback) {
        return new ListViewObserveHelper(view, callback).setListeners(view);
    }

    private final ListView mView;
    private final Callback mCallback;

    private ListViewObserveHelper(final ListView view, final Callback callback) {
        mView = view;
        mCallback = callback;
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
            final int visibleItemCount,
            final int totalItemCount) {
//        mCallback.onStateChanged(firstVisibleItem == 0 ? Callback.STATE_VISIBLE
//                : Callback.STATE_INVISIBLE);
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        // Do nothing
    }

    private ListViewObserveHelper setListeners(final ListView view) {
        mView.setOnScrollListener(this);
        return this;
    }
}
