package com.ch3d.xreminderx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ch3d.xreminderx.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeDismissListViewTouchListener implements View.OnTouchListener, Callback {
	// Cached ViewConfiguration and system-wide constant values
	private final int mSlop;
	private final int mMinFlingVelocity;
	private final int mMaxFlingVelocity;
	private final long mAnimationTime;
	// Fixed properties
	private final ListView mListView;
	private final DismissCallbacks mCallbacks;
	// Transient properties
	private final List<PendingDismissData> mPendingDismisses = new ArrayList<PendingDismissData>();
	private final Handler mHandler;
	private final SwipeRefreshLayout swipeRefreshLayout;
	// 1 and not 0 to prevent dividing by zero
	private int mViewWidth = 1;
	private int mDismissAnimationRefCount = 0;
	private float mDownX;
	private boolean mSwiping;
	private VelocityTracker mVelocityTracker;
	private int mDownPosition;
	private View mDownView;
	private boolean mPaused;
	private float mDownY;
	private boolean mSkip;

	/**
	 * Constructs a new swipe-to-dismiss touch listener for the given list view.
	 *
	 * @param listView  The list view whose items should be dismissable.
	 * @param callbacks The callback to trigger when the user has indicated that she
	 *                  would like to dismiss one or more list items.
	 */
	public SwipeDismissListViewTouchListener(
			@NotNull final ListView listView, @Nullable SwipeRefreshLayout swipeRefreshLayout, final DismissCallbacks callbacks) {
		this.swipeRefreshLayout = swipeRefreshLayout;
		final ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 8;
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		mAnimationTime = listView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
		mListView = listView;
		mCallbacks = callbacks;
		mHandler = new Handler(this);
	}

	@Override
	public boolean handleMessage(final Message msg) {
		switch (msg.what) {

			default:
				break;
		}
		return false;
	}

	/**
	 * Returns an {@link android.widget.AbsListView.OnScrollListener} to be
	 * added to the {@link ListView} using
	 * {@link ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)}
	 * . If a scroll listener is already assigned, the caller should still pass
	 * scroll changes through to this listener. This will ensure that this
	 * {@link SwipeDismissListViewTouchListener} is paused during list view
	 * scrolling.</p>
	 *
	 * @see SwipeDismissListViewTouchListener
	 */
	public AbsListView.OnScrollListener makeScrollListener() {
		return new AbsListView.OnScrollListener() {
			@Override
			public void onScroll(final AbsListView absListView, final int i, final int i1, final int i2) {
			}

			@Override
			public void onScrollStateChanged(final AbsListView absListView, final int scrollState) {
				setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			}
		};
	}

	@Override
	public boolean onTouch(final View view, final MotionEvent motionEvent) {
		if (mViewWidth < 2) {
			mViewWidth = mListView.getWidth();
		}

		switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				if (mPaused) {
					return false;
				}
				mSkip = false;

				// TODO: ensure this is a finger, and set a flag

				// Find the child view that was touched (perform a hit test)
				final Rect rect = new Rect();
				final int childCount = mListView.getChildCount();
				final int[] listViewCoords = new int[2];
				mListView.getLocationOnScreen(listViewCoords);
				final int x = (int) motionEvent.getRawX() - listViewCoords[0];
				final int y = (int) motionEvent.getRawY() - listViewCoords[1];
				View child;
				for (int i = 0; i < childCount; i++) {
					child = mListView.getChildAt(i);
					child.getHitRect(rect);
					if (rect.contains(x, y)) {
						mDownView = child;
						break;
					}
				}

				if (mDownView != null) {
					mDownX = motionEvent.getRawX();
					mDownY = motionEvent.getRawY();
					mDownPosition = mListView.getPositionForView(mDownView);
					if (mCallbacks.canDismiss(mDownPosition)) {
						mVelocityTracker = VelocityTracker.obtain();
						mVelocityTracker.addMovement(motionEvent);
					} else {
						mDownView = null;
					}
				}
				view.onTouchEvent(motionEvent);
				return true;
			}

			case MotionEvent.ACTION_UP: {
				if (mVelocityTracker == null) {
					break;
				}

				final float deltaX = motionEvent.getRawX() - mDownX;
				mVelocityTracker.addMovement(motionEvent);
				mVelocityTracker.computeCurrentVelocity(1000);
				final float velocityX = mVelocityTracker.getXVelocity();
				final float absVelocityX = Math.abs(velocityX);
				final float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
				boolean dismiss = false;
				boolean dismissRight = false;
				if (Math.abs(deltaX) > (mViewWidth / 2)) {
					dismiss = true;
					dismissRight = deltaX > 0;
				} else if ((mMinFlingVelocity <= absVelocityX) && (absVelocityX <= mMaxFlingVelocity) && (absVelocityY < absVelocityX)) {
					// dismiss only if flinging in the same direction as
					// dragging
					dismiss = (velocityX < 0) == (deltaX < 0);
					dismissRight = mVelocityTracker.getXVelocity() > 0;
				}
				if (dismiss && !mSkip) {
					// dismiss
					final View downView = mDownView; // mDownView gets null'd
					// before animation ends
					final int downPosition = mDownPosition;
					++mDismissAnimationRefCount;
					mDownView.animate().translationX(dismissRight ? mViewWidth : -mViewWidth).alpha(0).setDuration(mAnimationTime)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(final Animator animation) {
									performDismiss(downView, downPosition);
								}
							});
				} else {
					// cancel
					mDownView.animate().translationX(0).alpha(1).setDuration(mAnimationTime).setListener(null);
				}
				mVelocityTracker.recycle();
				mVelocityTracker = null;
				mDownX = 0;
				mDownY = 0;
				mDownView = null;
				mDownPosition = AdapterView.INVALID_POSITION;
				setSwiping(false);

				mSkip = false;
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				if ((mVelocityTracker == null) || mPaused) {
					break;
				}

				if (Math.abs(mDownY - motionEvent.getRawY()) > 48) {
					setSwiping(false);
					mSkip = true;
					mListView.requestDisallowInterceptTouchEvent(true);
					mDownView.animate().translationX(0).alpha(1).setDuration(mAnimationTime).setListener(null);

					// Cancel ListView's touch (un-highlighting the item)
					// final MotionEvent cancelEvent = MotionEvent
					// .obtain(motionEvent);
					// cancelEvent
					// .setAction(MotionEvent.ACTION_CANCEL
					// | (motionEvent.getActionIndex() <<
					// MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					// mListView.onTouchEvent(cancelEvent);
					// cancelEvent.recycle();
					return false;
				}

				mVelocityTracker.addMovement(motionEvent);
				final float deltaX = motionEvent.getRawX() - mDownX;
				if (Math.abs(deltaX) > mSlop) {
					setSwiping(true);
					mListView.requestDisallowInterceptTouchEvent(true);

					// Cancel ListView's touch (un-highlighting the item)
					final MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
					cancelEvent.setAction(
							MotionEvent.ACTION_CANCEL | (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					mListView.onTouchEvent(cancelEvent);
					cancelEvent.recycle();
				}

				if (mSwiping) {
					mDownView.setTranslationX(deltaX);
					mDownView.setAlpha(Math.max(0f, Math.min(1f, 1f - ((2f * Math.abs(deltaX)) / mViewWidth))));
					return true;
				}
				break;
			}
		}
		return false;
	}

	private void setSwiping(boolean isSwiping) {
		mSwiping = isSwiping;
		if (swipeRefreshLayout != null) {
			swipeRefreshLayout.setEnabled(!isSwiping);
		}
	}

	private void performDismiss(final View dismissView, final int dismissPosition) {
		// Animate the dismissed list item to zero-height and fire the dismiss
		// callback when
		// all dismissed list item animations have completed. This triggers
		// layout on each animation
		// frame; in the future we may want to do something smarter and more
		// performant.

		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		final int originalHeight = dismissView.getHeight();
		dismissView.setTag(R.id.dismissed_original_height, originalHeight);

		final ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				--mDismissAnimationRefCount;
				if (mDismissAnimationRefCount == 0) {
					// No active animations, process all pending dismisses.
					// Sort by descending position
					Collections.sort(mPendingDismisses);
					final int[] dismissPositions = new int[mPendingDismisses.size()];
					for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
						dismissPositions[i] = mPendingDismisses.get(i).position;
					}
					mCallbacks.onDismiss(mListView, dismissPositions);
				}
			}

			@Override
			public void onAnimationStart(final Animator animation) {
				for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
					final PendingDismissData pendingDismissData = mPendingDismisses.get(i);
					ViewCompat.setHasTransientState(pendingDismissData.view, true);
				}
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

		mPendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
		animator.start();
	}

	/**
	 * Restore removed views state (height and x-position) Call this when
	 * receives notification from ContentObserver or activity/fragment pauses
	 */
	public void releaseTransientViews() {
		for (final PendingDismissData pendingDismiss : mPendingDismisses) {
			final View view = pendingDismiss.view;
			view.setAlpha(1f);
			view.setTranslationX(0);
			final ViewGroup.LayoutParams lp = view.getLayoutParams();
			lp.height = (Integer) view.getTag(R.id.dismissed_original_height);
			view.setLayoutParams(lp);
		}
		for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
			final PendingDismissData pendingDismissData = mPendingDismisses.get(i);
			ViewCompat.setHasTransientState(pendingDismissData.view, false);
		}
		mPendingDismisses.clear();
	}

	/**
	 * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
	 *
	 * @param enabled Whether or not to watch for gestures.
	 */
	public void setEnabled(final boolean enabled) {
		mPaused = !enabled;
	}

	/**
	 * The callback interface used by {@link SwipeDismissListViewTouchListener}
	 * to inform its client about a successful dismissal of one or more list
	 * item positions.
	 */
	public interface DismissCallbacks {
		/**
		 * Called to determine whether the given position can be dismissed.
		 */
		boolean canDismiss(int position);

		/**
		 * Called when the user has indicated they she would like to dismiss one
		 * or more list item positions.
		 *
		 * @param listView               The originating {@link ListView}.
		 * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
		 *                               order for convenience.
		 */
		void onDismiss(ListView listView, int[] reverseSortedPositions);
	}

	class PendingDismissData implements Comparable<PendingDismissData> {
		public int position;
		public View view;

		public PendingDismissData(final int position, final View view) {
			this.position = position;
			this.view = view;
		}

		@Override
		public int compareTo(final PendingDismissData other) {
			// Sort by descending position
			return other.position - position;
		}
	}
}
