package jp.westbrook.android.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * long value type slider preference
 */
public class LongSliderPreference  extends SliderPreference<Long> {
    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     */
    public LongSliderPreference(Context context) { super(context); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     */
    public LongSliderPreference(Context context, AttributeSet attrs) { super(context, attrs); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    public LongSliderPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     * @param defStyleRes int: A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     */
    public LongSliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void valueInitialize() {
        mDefaultValue = new BigDecimal("0");
        mMinimum = new BigDecimal("0");
        mMaximum = new BigDecimal("100");
        mTick = new BigDecimal("1");
        mValue = new BigDecimal("0");
    }

    protected void valueInitializeFix()
    {
        mTick = mTick.setScale(0, RoundingMode.HALF_UP);
    }

    protected int getValueScale() {
        return 0;
    }

    protected BigDecimal convertToValue(Long value)
    {
        if (value == null) { return null; }
        return BigDecimal.valueOf(value);
    }

    protected Long convertFromValue(BigDecimal value)
    {
        if (value == null) { return null; }
        return value.longValue();
    }

    protected BigDecimal getPersistedValue(@Nullable Object defaultValue) {
        Long value;
        if (defaultValue == null) {
            value = mDefaultValue.longValue();
        }
        else if (defaultValue instanceof Integer) {
            value = (Long)defaultValue;
        }
        else if (defaultValue instanceof Long) {
            value = (Long)defaultValue;
        }
        else if (defaultValue instanceof BigDecimal) {
            value = ((BigDecimal)defaultValue).longValue();
        }
        else {
            value = mDefaultValue.longValue();
        }
        // get persisted value
        value = getPersistedLong(value);
        // return by BigDecimal
        return convertToValue(value);
    }

    protected void persistValue(BigDecimal value)
    {
        persistLong(convertFromValue(value));
    }

    protected boolean callChangeListener(BigDecimal newValue)
    {
        return callChangeListener(newValue.longValue());
    }
}
