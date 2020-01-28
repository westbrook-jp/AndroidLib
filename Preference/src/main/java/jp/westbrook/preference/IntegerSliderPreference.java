package jp.westbrook.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IntegerSliderPreference extends SliderPreference<Integer> {
    public IntegerSliderPreference(Context context) { super(context); }
    public IntegerSliderPreference(Context context, AttributeSet attrs) { super(context, attrs); }
    public IntegerSliderPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
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
