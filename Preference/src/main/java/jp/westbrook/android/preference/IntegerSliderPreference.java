package jp.westbrook.android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * integer value type slider preference
 */
public class IntegerSliderPreference extends SliderPreference<Integer> {
    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     */
    public IntegerSliderPreference(Context context) { super(context); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     */
    public IntegerSliderPreference(Context context, AttributeSet attrs) { super(context, attrs); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    public IntegerSliderPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     * @param defStyleRes int: A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     */
    public IntegerSliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void valueInitialize() {
        mDefaultValue = new BigDecimal("0");
        mMinimum = new BigDecimal("0");
        mMaximum = new BigDecimal("100");
        mTick = new BigDecimal("1");
        mValue = new BigDecimal("0");
    }

    protected void valueInitializeFix() {
        mTick = mTick.setScale(0, RoundingMode.HALF_UP);
    }

    protected int getValueScale() {
        return 0;
    }

    protected BigDecimal convertToValue(Integer value)
    {
        if (value == null) { return null; }
        return BigDecimal.valueOf(value);
    }

    protected Integer convertFromValue(BigDecimal value)
    {
        if (value == null) { return null; }
        return value.intValue();
    }

    protected BigDecimal getPersistedValue(@Nullable Object defaultValue) {
        Integer value;
        if (defaultValue == null) {
            value = mDefaultValue.intValue();
        }
        else if (defaultValue instanceof Integer) {
            value = (Integer)defaultValue;
        }
        else if (defaultValue instanceof BigDecimal) {
            value = ((BigDecimal)defaultValue).intValue();
        }
        else {
            value = mDefaultValue.intValue();
        }
        // get persisted value
        value = getPersistedInt(value);
        // return by BigDecimal
        return convertToValue(value);
    }

    protected void persistValue(BigDecimal value)
    {
        persistInt(convertFromValue(value));
    }

    protected boolean callChangeListener(BigDecimal newValue)
    {
        return callChangeListener(newValue.intValue());
    }

}
