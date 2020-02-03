package jp.westbrook.android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Slider preference base template class
 * @param <N> type
 */
public abstract class SliderPreference<N extends Number> extends Preference
{
    protected enum LAYOUT_RESOURCE_MODE {
        SEEKBAR_LAYOUT_RESOURCE,
        LAYOUT_RESOURCE,
        WIDGET_LAYOUT_RESOURCE,
    }
    // static
    protected static final LAYOUT_RESOURCE_MODE mLayoutResourceMode =  LAYOUT_RESOURCE_MODE.SEEKBAR_LAYOUT_RESOURCE;
    protected static int mSeekBarPreferenceResourceId = 0;

    // value settings
    protected BigDecimal mDefaultValue;
    protected BigDecimal mMinimum;
    protected BigDecimal mMaximum;
    protected BigDecimal mTick;
    protected boolean mShowValue;
    protected boolean mUpdatesContinuously;
    protected boolean mAdjustable;

    // controls
    protected SeekBar mSeekBar;
    protected TextView mValueTextView;
    // value
    protected BigDecimal mValue;

    /////////////////////////////////////////////////////////////////////////////////////////////
    // constructors

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     */
    public SliderPreference(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     */
    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     * @param defStyleRes int: A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     */
    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // implements
    protected void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        switch (mLayoutResourceMode) {
            case LAYOUT_RESOURCE:
                setLayoutResource(R.layout.slider_preference);
                break;
            case WIDGET_LAYOUT_RESOURCE:
                setWidgetLayoutResource(R.layout.slider_preference_widget);
                break;
            case SEEKBAR_LAYOUT_RESOURCE:
                if (mSeekBarPreferenceResourceId == 0) {
                    mSeekBarPreferenceResourceId = new SeekBarPreference(context).getLayoutResource();
                }
                setLayoutResource(mSeekBarPreferenceResourceId);
                break;
        }
        // initialize flags
        mShowValue = false;
        mAdjustable = true;
        mUpdatesContinuously = false;
        // initialize values
        valueInitialize();
        // get xml value
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderPreference, defStyleAttr, defStyleRes);
        try {
            mMinimum = typedArrayGetValue(a, R.styleable.SliderPreference_minimum, mMinimum);
            mMaximum = typedArrayGetValue(a, R.styleable.SliderPreference_maximum, mMaximum);
            mTick = typedArrayGetValue(a, R.styleable.SliderPreference_tick, mTick);

            mShowValue = a.getBoolean(R.styleable.SliderPreference_showValue, mShowValue);
            mAdjustable = a.getBoolean(R.styleable.SliderPreference_adjustable, mAdjustable);
            mUpdatesContinuously = a.getBoolean(R.styleable.SliderPreference_updatesContinuously, mUpdatesContinuously);
        }
        finally {
            a.recycle();
        }
        // values
        valueInitializeFix();
    }

    protected abstract void valueInitialize();
    protected void valueInitializeFix() {}
    protected abstract int getValueScale();

    protected int convertToSeekBarProgress(BigDecimal value)
    {
        return value.subtract(mMinimum).divide(mTick, getValueScale(), RoundingMode.HALF_UP).intValue();
    }

    protected BigDecimal convertFromSeekBarProgress(int value)
    {
        return BigDecimal.valueOf(value).multiply(mTick).add(mMinimum).setScale(getValueScale(), RoundingMode.HALF_UP);
    }

    protected abstract BigDecimal convertToValue(N value);
    protected abstract N convertFromValue(BigDecimal value);

    protected String typedArrayGetString(TypedArray a, int index, @Nullable String defaultValue) {
        String string = a.getString(index);
        if (string != null) {
            return string;
        }
        return defaultValue;
    }

    protected BigDecimal typedArrayGetValue(TypedArray a, int index, @Nullable BigDecimal defaultValue) {
        String string = a.getString(index);
        if (string != null) {
            try {
                return new BigDecimal(string);
            }
            catch(Exception e) {}
        }
        return defaultValue;
    }
    protected abstract BigDecimal getPersistedValue(@Nullable Object defaultValue);
    protected abstract void persistValue(BigDecimal value);
    protected abstract boolean callChangeListener(BigDecimal newValue);

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        mDefaultValue = typedArrayGetValue(a, index, null);
        return mDefaultValue;
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        mValue = getPersistedValue(defaultValue);
        super.onSetInitialValue(defaultValue);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        //
        view.itemView.setOnKeyListener(mSeekBarKeyListener);
        mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
        mValueTextView = (TextView)view.findViewById(R.id.seekbar_value);
        if (mSeekBar == null) {
            return;
        }
        if (mValueTextView != null) {
            if (mShowValue) {
                mValueTextView.setVisibility(View.VISIBLE);
            } else {
                mValueTextView.setVisibility(View.GONE);
                mValueTextView = null;
            }
        }
        //
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setMax(convertToSeekBarProgress(mMaximum));
        updateSeekBarProgress(mValue);
        updateValueLabel(mValue);
        mSeekBar.setEnabled(isEnabled());
    }

    protected void setValueInternal(BigDecimal value, boolean notifyChanged) {
        if (value.compareTo(mMinimum) < 0) {
            value = mMinimum;
        }
        if (value.compareTo(mMaximum) > 0) {
            value = mMaximum;
        }
        if (value != mValue) {
            mValue = value;
            updateSeekBarProgress(mValue);
            persistValue(mValue);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    /**
     * Persist the SeekBar value
     */
    protected void syncValue() {
        BigDecimal seekBarValue = convertFromSeekBarProgress(mSeekBar.getProgress());
        if (seekBarValue != mValue) {
            if (callChangeListener(seekBarValue)) {
                setValueInternal(seekBarValue, false);
            } else {
                updateSeekBarProgress(mValue);
                updateValueLabel(mValue);
            }
        }
    }
    /**
     * Attempts to update the TextView label that displays the current value.
     */
    protected void updateSeekBarProgress(BigDecimal value) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(convertToSeekBarProgress(value));
        }
    }
    /**
     * Attempts to update the TextView label that displays the current value.
     */
    protected void updateValueLabel(BigDecimal value) {
        if (mValueTextView != null) {
            mValueTextView.setText(value.toPlainString());
        }
    }

    /**
     * seek bar lisener
     */
    protected SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        boolean mTrackingTouch;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && (mUpdatesContinuously || !mTrackingTouch)) {
                syncValue();
            } else {
                // always update text while the seek bar is being dragged
                updateValueLabel(convertFromSeekBarProgress(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTrackingTouch = false;
            syncValue();
        }
    };
    /**
     * keylisener
     */
    /**
     * Listener reacting to the user pressing DPAD left/right keys if {@code
     * adjustable} attribute is set to true; it transfers the key presses to the SeekBar
     * to be handled accordingly.
     */
    private View.OnKeyListener mSeekBarKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            if (!mAdjustable && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                // Right or left keys are pressed when in non-adjustable mode; Skip the keys.
                return false;
            }
            // We don't want to propagate the click keys down to the SeekBar view since it will
            // create the ripple effect for the thumb.
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                return false;
            }
            if (mSeekBar == null) {
                return false;
            }
            return mSeekBar.onKeyDown(keyCode, event);
        }
    };
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // public interface
    /**
     * Gets the current progress of the SeekBar.
     *
     * @return The current progress of the SeekBar
     */
    public N getValue()
    {
        return convertFromValue(mValue);
    }

    /**
     * Sets the current progress of the SeekBar.
     *
     * @param value The current progress of the SeekBar
     */
    public void setValue(N value)
    {
        setValueInternal(convertToValue(value), true);
    }
    /**
     * Gets the lower bound set on the SeekBar.
     *
     * @return The lower bound set
     */
    public N getMin()
    {
        return convertFromValue(mMinimum);
    }

    /**
     * Sets the lower bound on the SeekBar.
     *
     * @param min The lower bound to set
     */
    public void setMin(N min)
    {
        BigDecimal value = convertToValue(min);
        if (value.compareTo(mMaximum) > 0) {
            value = mMaximum;
        }
        if (value != mMinimum) {
            mMinimum = value;
            notifyChanged();
        }
    }

    /**
     * Gets the upper bound set on the SeekBar.
     *
     * @return The upper bound set
     */
    public N getMax() {
        return convertFromValue(mMaximum);
    }
    /**
     * Sets the upper bound on the SeekBar.
     *
     * @param max The upper bound to set
     */
    public final void setMax(N max) {
        BigDecimal value = convertToValue(max);
        if (value.compareTo(mMinimum) < 0) {
            value = mMinimum;
        }
        if (value != mMaximum) {
            mMaximum = value;
            notifyChanged();
        }
    }

    /**
     * Gets whether the current SeekBar value is displayed to the user.
     *
     * @return Whether the current SeekBar value is displayed to the user
     * @see #setShowValue(boolean)
     */
    public boolean getShowValue()
    {
        return mShowValue;
    }
    /**
     * Sets whether the current SeekBar value is displayed to the user.
     *
     * @param showValue Whether the current SeekBar value is displayed to the user
     * @see #getShowValue()
     */
    public void setShowValue(boolean showValue) {
        mShowValue = showValue;
        notifyChanged();
    }
    /**
     * Gets whether the SeekBar should respond to the left/right keys.
     *
     * @return Whether the SeekBar should respond to the left/right keys
     */
    public boolean isAdjustable()
    {
        return mAdjustable;
    }
    /**
     * Sets whether the SeekBar should respond to the left/right keys.
     *
     * @param adjustable Whether the SeekBar should respond to the left/right keys
     */
    public void setAdjustable(boolean adjustable)
    {
        mAdjustable = adjustable;
    }
    /**
     * Gets whether the {@link SliderPreference} should continuously save the SeekBar value
     * while it is being dragged. Note that when the value is true,
     * {@link Preference.OnPreferenceChangeListener} will be called continuously as well.
     *
     * @return Whether the {@link SliderPreference} should continuously save the SeekBar
     * value while it is being dragged
     * @see #setUpdatesContinuously(boolean)
     */
    public boolean getUpdatesContinuously()
    {
        return mUpdatesContinuously;
    }
    /**
     * Sets whether the {@link SeekBarPreference} should continuously save the SeekBar value
     * while it is being dragged.
     *
     * @param updatesContinuously Whether the {@link SeekBarPreference} should continuously save
     *                            the SeekBar value while it is being dragged
     * @see #getUpdatesContinuously()
     */
    public void setUpdatesContinuously(boolean updatesContinuously)
    {
        mUpdatesContinuously = updatesContinuously;
    }
}
