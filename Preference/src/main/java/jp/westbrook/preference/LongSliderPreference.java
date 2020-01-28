package jp.westbrook.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LongSliderPreference  extends SliderPreference<Long> {
    public LongSliderPreference(Context context) { super(context); }
    public LongSliderPreference(Context context, AttributeSet attrs) { super(context, attrs); }
    public LongSliderPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
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
