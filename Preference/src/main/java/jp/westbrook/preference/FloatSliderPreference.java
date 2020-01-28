package jp.westbrook.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FloatSliderPreference extends SliderPreference<Float> {
    public FloatSliderPreference(Context context) { super(context); }
    public FloatSliderPreference(Context context, AttributeSet attrs) { super(context, attrs); }
    public FloatSliderPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
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
