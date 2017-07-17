package com.android.settings.Legend.tabs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.utils.Utils;
import com.android.settings.chameleonos.SeekBarPreference;
import com.android.settings.cyanogenmod.SystemSettingSwitchPreference;

public class LockScreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockScreen";

    private static final String KEYGUARD_TOGGLE_TORCH = "keyguard_toggle_torch";
    private static final String LOCKSCREEN_SECURITY_ALPHA = "lockscreen_security_alpha";
    private static final String LOCKSCREEN_ALPHA = "lockscreen_alpha";

    private SwitchPreference mKeyguardTorch;
    private SeekBarPreference mLsAlpha;
    private SeekBarPreference mLsSecurityAlpha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen);
	PreferenceScreen prefSet = getPreferenceScreen();

	ContentResolver resolver = getActivity().getContentResolver();

	mLsSecurityAlpha = (SeekBarPreference) findPreference(LOCKSCREEN_SECURITY_ALPHA);
        float alpha2 = Settings.System.getFloat(resolver,
                Settings.System.LOCKSCREEN_SECURITY_ALPHA, 0.75f);
        mLsSecurityAlpha.setValue((int)(100 * alpha2));
        mLsSecurityAlpha.setOnPreferenceChangeListener(this);

        mLsAlpha = (SeekBarPreference) findPreference(LOCKSCREEN_ALPHA);
        float alpha = Settings.System.getFloat(resolver,
                Settings.System.LOCKSCREEN_ALPHA, 0.45f);
        mLsAlpha.setValue((int)(100 * alpha));
        mLsAlpha.setOnPreferenceChangeListener(this);

	mKeyguardTorch = (SwitchPreference) findPreference(KEYGUARD_TOGGLE_TORCH);
        mKeyguardTorch.setOnPreferenceChangeListener(this);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            prefSet.removePreference(mKeyguardTorch);
        } else {
        mKeyguardTorch.setChecked((Settings.System.getInt(resolver,
                Settings.System.KEYGUARD_TOGGLE_TORCH, 0) == 1));
        }
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
       ContentResolver resolver = getActivity().getContentResolver();
        if  (preference == mKeyguardTorch) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_TOGGLE_TORCH, checked ? 1:0);
            return true;
        } else if (preference == mLsSecurityAlpha) {
            int alpha2 = (Integer) objValue;
            Settings.System.putFloat(resolver,
                    Settings.System.LOCKSCREEN_SECURITY_ALPHA, alpha2 / 100.0f);
            return true;
        } else if (preference == mLsAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putFloat(resolver,
                    Settings.System.LOCKSCREEN_ALPHA, alpha / 100.0f);
            return true;
	}
        return false;
    }
}
