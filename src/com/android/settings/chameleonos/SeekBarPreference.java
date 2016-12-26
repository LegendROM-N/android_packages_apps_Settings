package com.android.settings.chameleonos;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewParent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.preference.*;

import com.android.settings.R;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private final String TAG = getClass().getName();
    private static final String SETTINGS_NS = "http://schemas.android.com/apk/res/com.android.settings";
    private static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";
    private static final int DEFAULT_VALUE = 50;

    private int mMin = 0;
    private int mInterval = 1;
    private int mCurrentValue;
    private int mDefaultValue = -1;
    private int mMax = 100;
    private String mUnits = "";
    private SeekBar mSeekBar;
    private TextView mTitle;
    private ImageView mImagePlus;
    private ImageView mImageMinus;
    private Drawable mProgressThumb;

    private TextView mStatusText;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CustomSeekBarPreference);

        mMax = attrs.getAttributeIntValue(SETTINGS_NS, "maximum", 100);
        mMin = attrs.getAttributeIntValue(SETTINGS_NS, "minimum", 0);
        mDefaultValue = attrs.getAttributeIntValue(ANDROIDNS, "defaultValue", -1);
        mUnits = getAttributeStringValue(attrs, SETTINGS_NS, "units", "");

        Integer id = a.getResourceId(R.styleable.CustomSeekBarPreference_units, 0);
        if (id > 0) {
            mUnits = context.getResources().getString(id);
        }

        try {
            String newInterval = attrs.getAttributeValue(SETTINGS_NS, "interval");
            if (newInterval != null)
                mInterval = Integer.parseInt(newInterval);
        } catch (Exception e) {
            Log.e(TAG, "Invalid interval value", e);
        }

        a.recycle();
        mSeekBar = new SeekBar(context, attrs);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setOnSeekBarChangeListener(this);
        setLayoutResource(R.layout.preference_custom_seekbar);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    private String getAttributeStringValue(AttributeSet attrs, String namespace, String name,
            String defaultValue) {
        String value = attrs.getAttributeValue(namespace, name);
        if (value == null)
            value = defaultValue;

        return value;
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);
        this.setShouldDisableView(true);
        if (mTitle != null)
            mTitle.setEnabled(!disableDependent);
        if (mSeekBar != null)
            mSeekBar.setEnabled(!disableDependent);
        if (mStatusText != null)
            mStatusText.setEnabled(!disableDependent);
	if (mImagePlus != null)
            mImagePlus.setEnabled(!disableDependent);
        if (mImageMinus != null)
            mImageMinus.setEnabled(!disableDependent);
    }

    protected View onCreateView(ViewGroup parent){

        RelativeLayout layout =  null;
        try {
            LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout)mInflater.inflate(R.layout.seek_bar_preference, parent, false);
            mTitle = (TextView) layout.findViewById(android.R.id.title);
            mImagePlus = (ImageView) layout.findViewById(R.id.imagePlus);
            mImagePlus.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     mSeekBar.setProgress((mCurrentValue + 1) - mMinValue);
                 }
             });
             mImagePlus.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(View view) {
                     mSeekBar.setProgress((mCurrentValue + 10) - mMinValue);
                    return true;
                }
            });
            mImageMinus = (ImageView) layout.findViewById(R.id.imageMinus);
            mImageMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSeekBar.setProgress((mCurrentValue - 1) - mMinValue);
                }
            });
            mImageMinus.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSeekBar.setProgress((mCurrentValue - 10) - mMinValue);
                    return true;
                }
            });
	    mProgressThumb = mSeekBar.getThumb();
	}
        catch(Exception e)
        {
            Log.e(TAG, "Error creating seek bar preference", e);
        }
        return layout;
    }

    public void onBindViewHolder(PreferenceViewHolder view) {
        try
        {
            // move our seekbar to the new view we've been given
            ViewParent oldContainer = mSeekBar.getParent();
            ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);

            if (oldContainer != newContainer) {
                // remove the seekbar from the old view
                if (oldContainer != null) {
                    ((ViewGroup) oldContainer).removeView(mSeekBar);
                }
                // remove the existing seekbar (there may not be one) and add ours
                newContainer.removeAllViews();
                newContainer.addView(mSeekBar, ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error binding view: " + ex.toString());
        }
        mStatusText = (TextView) view.findViewById(R.id.seekBarPrefValue);
        mStatusText.setText(String.valueOf(mCurrentValue) + mUnits);
        mStatusText.setMinimumWidth(30);
        mSeekBar.setProgress(mCurrentValue - mMin);
        mTitle = (TextView) view.findViewById(android.R.id.title);
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setMin(int min) {
        mMin = min;
    }

    public void setIntervalValue(int value) {
        mInterval = value;
    }

    public void setValue(int value) {
        mCurrentValue = value;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = progress + mMin;
        if (newValue > mMax)
            newValue = mMax;
        else if (newValue < mMin)
            newValue = mMin;
        else if (mInterval != 1 && newValue % mInterval != 0)
            newValue = Math.round(((float) newValue) / mInterval) * mInterval;

        // change rejected, revert to the previous value
        if (!callChangeListener(newValue)) {
            seekBar.setProgress(mCurrentValue - mMin);
            return;
        }
        // change accepted, store it
        mCurrentValue = newValue;
	if (mCurrentValue == mDefaultValue && mDefaultValue != -1) {
            mStatusText.setText(R.string.default_string);
            int redColor = getContext().getResources().getColor(R.color.seekbar_dot_color);
            mProgressThumb.setColorFilter(redColor, PorterDuff.Mode.SRC_IN);
        } else {
            mStatusText.setText(String.valueOf(newValue));
            mProgressThumb.clearColorFilter();
        }
        persistInt(newValue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index) {
        int defaultValue = ta.getInt(index, DEFAULT_VALUE);
        return defaultValue;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            mCurrentValue = getPersistedInt(mCurrentValue);
        }
        else {
            int temp = 0;
            try {
                temp = (Integer) defaultValue;
            } catch (Exception ex) {
                Log.e(TAG, "Invalid default value: " + defaultValue.toString());
            }
            persistInt(temp);
            mCurrentValue = temp;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mSeekBar != null && mStatusText != null && mTitle != null) {
            mSeekBar.setEnabled(enabled);
            mStatusText.setEnabled(enabled);
            mTitle.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }
}
