package com.wrapp.floatlabelededittext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

// import com.nineoldandroids.animation.Animator;
// import com.nineoldandroids.animation.AnimatorListenerAdapter;
// import com.nineoldandroids.animation.AnimatorSet;
// import com.nineoldandroids.animation.ObjectAnimator;

public class FloatLabeledEditText extends LinearLayout
{

	static class FloatEditTextSavedState extends BaseSavedState
	{
		String															text;

		String															hint;

		int																inputType;

		int																imeOptions;

		int																imeActionId;

		String															imeActionLabel;

		boolean															singleLine;

		ColorStateList													hintColor;

		ColorStateList													textColor;

		// required field that makes Parcelables from a Parcel
		public static final Parcelable.Creator<FloatEditTextSavedState>	CREATOR	= new Parcelable.Creator<FloatEditTextSavedState>()
																				{
																					@Override
																					public FloatEditTextSavedState createFromParcel(
																							final Parcel in)
																					{
																						return new FloatEditTextSavedState(
																								in);
																					}

																					@Override
																					public FloatEditTextSavedState[] newArray(
																							final int size)
																					{
																						return new FloatEditTextSavedState[size];
																					}
																				};

		private FloatEditTextSavedState(final Parcel in)
		{
			super(in);
			text = in.readString();
			hint = in.readString();
			inputType = in.readInt();
			imeOptions = in.readInt();
			imeActionId = in.readInt();
			imeActionLabel = in.readString();
			singleLine = in.readInt() == 1;
			hintColor = in.readParcelable(ColorStateList.class.getClassLoader());
			textColor = in.readParcelable(ColorStateList.class.getClassLoader());
		}

		FloatEditTextSavedState(final Parcelable superState)
		{
			super(superState);
		}

		@Override
		public void writeToParcel(final Parcel out, final int flags)
		{
			super.writeToParcel(out, flags);
			out.writeString(text);
			out.writeString(hint);
			out.writeInt(inputType);
			out.writeInt(imeOptions);
			out.writeInt(imeActionId);
			out.writeString(imeActionLabel);
			out.writeInt(singleLine ? 1 : 0);
			out.writeParcelable(hintColor, flags);
			out.writeParcelable(textColor, flags);
		}
	}

	private static boolean isIcsOrAbove()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	private String						hint;

	private int							inputType;

	private int							imeOptions;

	private int							imeActionId;

	private String						imeActionLabel;

	private boolean						singleLine;

	private ColorStateList				hintColor;

	private ColorStateList				textColor;

	private TextView					hintTextView;

	private EditText					editText;

	private final Context				mContext;

	private final TextWatcher			onTextChanged	= new TextWatcher()
														{
															@Override
															public void afterTextChanged(final Editable editable)
															{
																setShowHint(editable.length() != 0);
															}

															@Override
															public void beforeTextChanged(
																	final CharSequence charSequence, final int i,
																	final int i2, final int i3)
															{
															}

															@Override
															public void onTextChanged(final CharSequence charSequence,
																	final int i, final int i2, final int i3)
															{
															}
														};

	private final OnFocusChangeListener	onFocusChanged	= new OnFocusChangeListener()
														{
															@Override
															public void onFocusChange(final View view,
																	final boolean gotFocus)
															{
																if(gotFocus)
																{
																	ObjectAnimator.ofFloat(hintTextView, "alpha",
																			0.33f, 1f).start();
																}
																else
																{
																	ObjectAnimator.ofFloat(hintTextView, "alpha", 1f,
																			0.33f).start();
																}
															}
														};

	public FloatLabeledEditText(final Context context)
	{
		super(context);
		mContext = context;
		initialize();
	}

	public FloatLabeledEditText(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		setAttributes(attrs);
		initialize();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FloatLabeledEditText(final Context context, final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		setAttributes(attrs);
		initialize();
	}

	/**
	 * See {@link android.widget.EditText#extendSelection(int)}.
	 */
	public void extendSelection(final int index)
	{
		editText.extendSelection(index);
	}

	public EditText getEditText()
	{
		return editText;
	}

	public String getHint()
	{
		return editText.getHint().toString();
	}

	public Editable getText()
	{
		return editText.getText();
	}

	/**
	 * Convenience for {@link #setText} to get the {@link java.lang.String} of what is in the EditText.
	 */
	public String getTextString()
	{
		return editText.getText().toString();
	}

	private void initialize()
	{
		setOrientation(VERTICAL);
		if(isInEditMode())
		{
			return;
		}

		final View view = LayoutInflater.from(mContext).inflate(R.layout.widget_float_labeled_edit_text, this);

		hintTextView = (TextView)view.findViewById(R.id.FloatLabeledEditTextHint);
		editText = (EditText)view.findViewById(R.id.FloatLabeledEditTextEditText);

		if(hint != null)
		{
			setHint(hint);
		}

		if(inputType != InputType.TYPE_NULL)
		{
			editText.setInputType(inputType);
		}
		editText.setImeOptions(imeOptions);
		if((imeActionId > -1) && !TextUtils.isEmpty(imeActionLabel))
		{
			editText.setImeActionLabel(imeActionLabel, imeActionId);
		}
		editText.setSingleLine(singleLine);
		hintTextView.setTextColor(hintColor != null ? hintColor : ColorStateList.valueOf(Color.BLACK));
		editText.setTextColor(textColor != null ? textColor : ColorStateList.valueOf(Color.BLACK));

		hintTextView.setVisibility(View.INVISIBLE);
		editText.addTextChangedListener(onTextChanged);
		editText.setOnFocusChangeListener(onFocusChanged);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onInitializeAccessibilityEvent(final AccessibilityEvent event)
	{
		if(isIcsOrAbove())
		{
			super.onInitializeAccessibilityEvent(event);
			editText.onInitializeAccessibilityEvent(event);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo info)
	{
		if(isIcsOrAbove())
		{
			super.onInitializeAccessibilityNodeInfo(info);
			editText.onInitializeAccessibilityNodeInfo(info);
		}
	}

	@Override
	public void onRestoreInstanceState(final Parcelable state)
	{
		if(!(state instanceof FloatEditTextSavedState))
		{
			super.onRestoreInstanceState(state);
			return;
		}
		final FloatEditTextSavedState ss = (FloatEditTextSavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());
		inputType = ss.inputType;
		imeOptions = ss.imeOptions;
		hint = ss.hint;
		final String text = ss.text;
		if(!TextUtils.isEmpty(text))
		{
			editText.setText(text);
		}
	}

	@Override
	public Parcelable onSaveInstanceState()
	{
		final Parcelable parcelable = super.onSaveInstanceState();
		final FloatEditTextSavedState ss = new FloatEditTextSavedState(parcelable);
		ss.hint = hint;
		ss.inputType = inputType;
		ss.imeOptions = imeOptions;
		ss.imeActionId = imeActionId;
		ss.imeActionLabel = imeActionLabel;
		ss.singleLine = singleLine;
		ss.text = editText.getText().toString();
		ss.hintColor = hintColor;
		ss.textColor = textColor;
		return ss;
	}

	/**
	 * Requests focus on the EditText.
	 */
	public void requestFieldFocus()
	{
		editText.requestFocus();
	}

	/**
	 * See {@link android.widget.EditText#selectAll()}.
	 */
	public void selectAll()
	{
		editText.selectAll();
	}

	private void setAttributes(final AttributeSet attrs)
	{
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatLabeledEditText);
		inputType = InputType.TYPE_NULL;
		try
		{
			hint = a.getString(R.styleable.FloatLabeledEditText_fletFloatingHint);
			inputType = a.getInt(R.styleable.FloatLabeledEditText_fletInputType, InputType.TYPE_NULL);
			imeOptions = a.getInt(R.styleable.FloatLabeledEditText_fletImeOptions, EditorInfo.IME_ACTION_DONE);
			imeActionId = a.getInt(R.styleable.FloatLabeledEditText_fletImeActionId, -1);
			imeActionLabel = a.getString(R.styleable.FloatLabeledEditText_fletImeActionLabel);
			singleLine = a.getBoolean(R.styleable.FloatLabeledEditText_fletSingleLine, false);
			hintColor = a.getColorStateList(R.styleable.FloatLabeledEditText_fletHintTextColor);
			textColor = a.getColorStateList(R.styleable.FloatLabeledEditText_fletTextColor);
		}
		finally
		{
			a.recycle();
		}
	}

	/**
	 * See {@link android.widget.EditText#setEllipsize(android.text.TextUtils.TruncateAt)}.
	 */
	public void setEllipsize(final TextUtils.TruncateAt ellipsize)
	{
		editText.setEllipsize(ellipsize);
	}

	/**
	 * See {@link android.widget.TextView#setError(CharSequence)}.
	 */
	public void setError(final CharSequence text)
	{
		editText.setError(text);
	}

	/**
	 * See {@link android.widget.TextView#setError(CharSequence, android.graphics.drawable.Drawable)}.
	 */
	public void setError(final CharSequence text, final Drawable icon)
	{
		editText.setError(text, icon);
	}

	/**
	 * See {@link android.widget.TextView#setError(CharSequence)}.
	 * 
	 * @param resourceId
	 */
	public void setErrorResource(final int resourceId)
	{
		editText.setError(mContext.getString(resourceId));
	}

	/**
	 * See {@link android.widget.TextView#setError(CharSequence, Drawable)}.
	 * 
	 * @param resourceId
	 */
	public void setErrorResource(final int resourceId, final Drawable icon)
	{
		editText.setError(mContext.getString(resourceId), icon);
	}

	public void setHint(final String hint)
	{
		this.hint = hint;
		editText.setHint(hint);
		editText.setHintTextColor(hintColor);
		hintTextView.setText(hint);
	}

	public void setHintTextColor(final ColorStateList colors)
	{
		hintTextView.setTextColor(colors);
	}

	public void setHintTextColor(final int color)
	{
		hintTextView.setTextColor(color);
	}

	/**
	 * See {@link android.widget.EditText#setImeActionLabel(CharSequence, int)}.
	 */
	public void setImeActionLabel(final CharSequence label, final int actionId)
	{
		editText.setImeActionLabel(label, actionId);
	}

	/**
	 * Sets the {@link android.widget.TextView.OnEditorActionListener}.
	 * 
	 * @param listener
	 */
	public void setOnEditorActionListener(final TextView.OnEditorActionListener listener)
	{
		editText.setOnEditorActionListener(listener);
	}

	/**
	 * See {@link android.widget.EditText#setSelection(int)}.
	 */
	public void setSelection(final int index)
	{
		editText.setSelection(index);
	}

	/**
	 * See {@link android.widget.EditText#setSelection(int, int)}.
	 */
	public void setSelection(final int start, final int stop)
	{
		editText.setSelection(start, stop);
	}

	private void setShowHint(final boolean show)
	{
		AnimatorSet animation = null;
		if((hintTextView.getVisibility() == VISIBLE) && !show)
		{
			animation = new AnimatorSet();
			final ObjectAnimator move = ObjectAnimator.ofFloat(hintTextView, "translationY", 0,
					hintTextView.getHeight() / 8);
			final ObjectAnimator fade = ObjectAnimator.ofFloat(hintTextView, "alpha", 1, 0);
			animation.playTogether(move, fade);
		}
		else if((hintTextView.getVisibility() != VISIBLE) && show)
		{
			animation = new AnimatorSet();
			final ObjectAnimator move = ObjectAnimator.ofFloat(hintTextView, "translationY",
					hintTextView.getHeight() / 8, 0);
			final ObjectAnimator fade = ObjectAnimator.ofFloat(hintTextView, "alpha", 0, 1);
			animation.playTogether(move, fade);
		}

		if(animation != null)
		{
			animation.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(final Animator animation)
				{
					super.onAnimationEnd(animation);
					hintTextView.setVisibility(show ? VISIBLE : INVISIBLE);
				}

				@Override
				public void onAnimationStart(final Animator animation)
				{
					super.onAnimationStart(animation);
					hintTextView.setVisibility(VISIBLE);
				}
			});
			animation.start();
		}
	}

	// Dealing with saving the state

	/**
	 * Sets the text on the EditText. See {@link android.widget.EditText#setText(CharSequence)}.
	 * 
	 * @param text
	 */
	public void setText(final CharSequence text)
	{
		editText.setText(text);
	}

	/**
	 * See {@link android.widget.EditText#setText(CharSequence, android.widget.TextView.BufferType)}.
	 * 
	 * @param text
	 * @param type
	 */
	public void setText(final CharSequence text, final TextView.BufferType type)
	{
		editText.setText(text, type);
	}

	public void setTextColor(final ColorStateList colors)
	{
		editText.setTextColor(colors);
	}

	public void setTextColor(final int color)
	{
		editText.setTextColor(color);
	}
}
