package com.android.settings.Legend.tabs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.content.ContentResolver;
import android.os.UserHandle;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.chameleonos.SeekBarPreference;

public class GeneralUI extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "GeneralUI";
    private static final String SCREENSHOT_DELAY = "screenshot_delay";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	
	      ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.generalui); 

	mScreenshotDelay = (SeekBarPreference) findPreference(SCREENSHOT_DELAY);
        int screenshotDelay = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_DELAY, 1000);
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
