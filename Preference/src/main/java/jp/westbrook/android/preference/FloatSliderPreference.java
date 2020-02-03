package jp.westbrook.android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * float value type slider preference
 */
public class FloatSliderPreference extends SliderPreference<Float> {
    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     */
    public FloatSliderPreference(Context context) { super(context); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     */
    public FloatSliderPreference(Context context, AttributeSet attrs) { super(context, attrs); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    public FloatSliderPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    /**
     * constructor
     * @param context Context: The Context this is associated with, through which it can access the current theme, resources, SharedPreferences, etc.
     * @param attrs AttributeSet: The attributes of the XML tag that is inflating the preference
     * @param defStyleAttr int: An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     * @param defStyleRes int: A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     */
    public FloatSliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void valueInitialize() {
        mDefaultValue = new BigDecimal("0");
        mMinimum = new BigDecimal("0");
        mMaximum = new BigDecimal("1");
        mTick = new BigDecimal("0.01");
        mValue = new BigDecimal("0");
    }

    protected int getValueScale() {
        return mTick.scale();
    }

    protected BigDecimal convertToValue(Float value)
    {
        if (value == null) { return null; }
        return BigDecimal.valueOf(value).setScale(getValueScale(), RoundingMode.HALF_UP);
    }

    protected Float convertFromValue(BigDecimal value)
    {
        if (value == null) { return null; }
        return value.setScale(getValueScale(), RoundingMode.HALF_UP).floatValue();
    }

    protected BigDecimal getPersistedValue(@Nullable Object defaultValue) {
        Float value;
        if (defaultValue == null) {
            value = mDefaultValue.floatValue();
        }
        else if (defaultValue instanceof Float) {
            value = (Float)defaultValue;
        }
        else if (defaultValue instanceof BigDecimal) {
            value = ((BigDecimal)defaultValue).floatValue();
        }
        else {
            value = mDefaultValue.floatValue();
        }
        // get persisted value
        value = getPersistedFloat(value);
        // return by BigDecimal
        return convertToValue(value);
    }

    protected void persistValue(BigDecimal value)
    {
        persistFloat(convertFromValue(value));
    }

    protected boolean callChangeListener(BigDecimal newValue)
    {
        return callChangeListener(newValue.floatValue());
    }
}
