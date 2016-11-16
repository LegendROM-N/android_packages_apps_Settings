package com.android.settings.Legend.tabs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.content.ContentResolver;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.chameleonos.SeekBarPreference;

public class GeneralUI extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "GeneralUI";
    private static final String SCREENSHOT_DELAY = "screenshot_delay";

    private SeekBarPreference mScreenshotDelay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	      ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.generalui);

	mScreenshotDelay = (SeekBarPreference) findPreference(SCREENSHOT_DELAY);
        int screenshotDelay = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_DELAY, 100);
        mScreenshotDelay.setValue(screenshotDelay / 1);
        mScreenshotDelay.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.LEGENDROM;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mScreenshotDelay) {
            int screenshotDelay = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREENSHOT_DELAY, screenshotDelay * 1);
            return true;
        }
	return false;
    }
} 
