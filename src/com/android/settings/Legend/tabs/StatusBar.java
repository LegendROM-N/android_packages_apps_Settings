package com.android.settings.Legend.tabs;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.RemoteException;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.chameleonos.SeekBarPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "StatusBar";
    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String FORCE_EXPANDED_NOTIFICATIONS = "force_expanded_notifications";
    private static final String DISABLE_IMMERSIVE_MESSAGE = "disable_immersive_message";
    private static final String PREF_QS_EASY_TOGGLE = "qs_easy_toggle";
    private static final String DAYLIGHT_HEADER_PACK = "daylight_header_pack";
    private static final String DEFAULT_HEADER_PACKAGE = "com.android.systemui";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";
    private static final String CUSTOM_HEADER_PROVIDER = "custom_header_provider";
    private static final String CUSTOM_HEADER_BROWSE = "custom_header_browse";
    private static final String PREF_COLUMNS = "qs_layout_columns";
    private static final String PREF_ROWS_PORTRAIT = "qs_rows_portrait";
    private static final String PREF_ROWS_LANDSCAPE = "qs_rows_landscape";
    private static final String PREF_SYSUI_QQS_COUNT = "sysui_qqs_count_key";

    private ListPreference mSmartPulldown;
    private SwitchPreference mForceExpanded;
    private SwitchPreference mDisableIM;
    private SwitchPreference mEasyToggle;
    private ListPreference mDaylightHeaderPack;
    private ListPreference mHeaderProvider;
    private SeekBarPreference mHeaderShadow;
    private PreferenceScreen mHeaderBrowse;
    private String mDaylightHeaderProvider;
    private SeekBarPreference mQsColumns;
    private SeekBarPreference mRowsPortrait;
    private SeekBarPreference mRowsLandscape;
    private SeekBarPreference mSysuiQqsCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.statusbar);

	PreferenceScreen prefSet = getPreferenceScreen();
  		ContentResolver resolver = getActivity().getContentResolver();

	int defaultValue;

	mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
	mSmartPulldown.setOnPreferenceChangeListener(this);
	int smartPulldown = Settings.System.getInt(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

	mForceExpanded = (SwitchPreference) findPreference(FORCE_EXPANDED_NOTIFICATIONS);
        mForceExpanded.setOnPreferenceChangeListener(this);
        int ForceExpanded = Settings.System.getInt(getContentResolver(),
                FORCE_EXPANDED_NOTIFICATIONS, 0);
        mForceExpanded.setChecked(ForceExpanded != 0);

	mDisableIM = (SwitchPreference) findPreference(DISABLE_IMMERSIVE_MESSAGE);
        mDisableIM.setOnPreferenceChangeListener(this);
        int DisableIM = Settings.System.getInt(getContentResolver(),
                DISABLE_IMMERSIVE_MESSAGE, 0);
        mDisableIM.setChecked(DisableIM != 0);

	mEasyToggle = (SwitchPreference) findPreference(PREF_QS_EASY_TOGGLE);
        mEasyToggle.setOnPreferenceChangeListener(this);
        mEasyToggle.setChecked((Settings.Secure.getInt(resolver,
                Settings.Secure.QS_EASY_TOGGLE, 0) == 1));

	String settingHeaderPackage = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK);
        if (settingHeaderPackage == null) {
            settingHeaderPackage = DEFAULT_HEADER_PACKAGE;
        }

        mDaylightHeaderPack = (ListPreference) findPreference(DAYLIGHT_HEADER_PACK);

        List<String> entries = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        getAvailableHeaderPacks(entries, values);
        mDaylightHeaderPack.setEntries(entries.toArray(new String[entries.size()]));
        mDaylightHeaderPack.setEntryValues(values.toArray(new String[values.size()]));

        int valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
        if (valueIndex == -1) {
            settingHeaderPackage = DEFAULT_HEADER_PACKAGE;
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, settingHeaderPackage);
            valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
        }

        mDaylightHeaderPack.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntry());
        mDaylightHeaderPack.setOnPreferenceChangeListener(this);

        mHeaderShadow = (SeekBarPreference) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
        final int headerShadow = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 80);
        mHeaderShadow.setValue(headerShadow);
        mHeaderShadow.setOnPreferenceChangeListener(this);

        mDaylightHeaderProvider = getResources().getString(R.string.daylight_header_provider);
        String providerName = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_PROVIDER);
        if (providerName == null) {
            providerName = mDaylightHeaderProvider;
        }

        mHeaderProvider = (ListPreference) findPreference(CUSTOM_HEADER_PROVIDER);
        valueIndex = mHeaderProvider.findIndexOfValue(providerName);
        mHeaderProvider.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mHeaderProvider.setSummary(mHeaderProvider.getEntry());
        mHeaderProvider.setOnPreferenceChangeListener(this);
        mDaylightHeaderPack.setEnabled(providerName.equals(mDaylightHeaderProvider));

        mHeaderBrowse = (PreferenceScreen) findPreference(CUSTOM_HEADER_BROWSE);
        mHeaderBrowse.setEnabled(isBrowseHeaderAvailable());

	mQsColumns = (SeekBarPreference) findPreference(PREF_COLUMNS);
	int columnsQs = Settings.System.getInt(resolver,
		Settings.System.QS_LAYOUT_COLUMNS, 3);
	mQsColumns.setValue(columnsQs / 1);
	mQsColumns.setOnPreferenceChangeListener(this);

	mRowsPortrait = (SeekBarPreference) findPreference(PREF_ROWS_PORTRAIT);
        int rowsPortrait = Settings.System.getInt(resolver,
                Settings.System.QS_ROWS_PORTRAIT, 3);
        mRowsPortrait.setValue(rowsPortrait / 1);
        mRowsPortrait.setOnPreferenceChangeListener(this);

        defaultValue = getResources().getInteger(com.android.internal.R.integer.config_qs_num_rows_landscape_default);
        mRowsLandscape = (SeekBarPreference) findPreference(PREF_ROWS_LANDSCAPE);
        int rowsLandscape = Settings.System.getInt(resolver,
                Settings.System.QS_ROWS_LANDSCAPE, defaultValue);
        mRowsLandscape.setValue(rowsLandscape / 1);
        mRowsLandscape.setOnPreferenceChangeListener(this);

	mSysuiQqsCount = (SeekBarPreference) findPreference(PREF_SYSUI_QQS_COUNT);
        int SysuiQqsCount = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.QQS_COUNT, 6);
        mSysuiQqsCount.setValue(SysuiQqsCount / 1);
        mSysuiQqsCount.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
  	ContentResolver resolver = getActivity().getContentResolver();

	int intValue;
	int index;

	if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_SMART_PULLDOWN, smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
            return true;
	} else if (preference == mForceExpanded) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), FORCE_EXPANDED_NOTIFICATIONS,
                    value ? 1 : 0);
            return true;
	} else if (preference == mDisableIM) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), DISABLE_IMMERSIVE_MESSAGE,
                    value ? 1 : 0);
            return true;
	} else if  (preference == mEasyToggle) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.QS_EASY_TOGGLE, checked ? 1:0);
            return true;
	} else if (preference == mQsColumns) {
            int qsColumns = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_LAYOUT_COLUMNS, qsColumns * 1);
            return true;
	} else if (preference == mRowsPortrait) {
            int rowsPortrait = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_ROWS_PORTRAIT, rowsPortrait * 1);
            return true;
        } else if (preference == mRowsLandscape) {
            int rowsLandscape = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_ROWS_LANDSCAPE, rowsLandscape * 1);
            return true;
	} else if (preference == mSysuiQqsCount) {
            int SysuiQqsCount = (Integer) objValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.QQS_COUNT, SysuiQqsCount * 1);
            return true;
	} else if (preference == mDaylightHeaderPack) {
            String value = (String) newValue;
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, value);
            int valueIndex = mDaylightHeaderPack.findIndexOfValue(value);
            mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntries()[valueIndex]);
            return true;
         } else if (preference == mHeaderShadow) {
            Integer headerShadow = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, headerShadow);
            return true;
         } else if (preference == mHeaderProvider) {
            String value = (String) newValue;
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_PROVIDER, value);
            int valueIndex = mHeaderProvider.findIndexOfValue(value);
            mHeaderProvider.setSummary(mHeaderProvider.getEntries()[valueIndex]);
            mDaylightHeaderPack.setEnabled(value.equals(mDaylightHeaderProvider));
            return true;
        }
	return false;
    }

    private void getAvailableHeaderPacks(List<String> entries, List<String> values) {
        Intent i = new Intent();
        PackageManager packageManager = getPackageManager();
        i.setAction("org.omnirom.DaylightHeaderPack");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            if (packageName.equals(DEFAULT_HEADER_PACKAGE)) {
                values.add(0, packageName);
            } else {
                values.add(packageName);
            }
            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = r.activityInfo.packageName;
            }
            if (packageName.equals(DEFAULT_HEADER_PACKAGE)) {
                entries.add(0, label);
            } else {
                entries.add(label);
            }
        }
        i.setAction("org.omnirom.DaylightHeaderPack1");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            values.add(packageName  + "/" + r.activityInfo.name);

            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = packageName;
            }
            entries.add(label);
        }
    }

    private boolean isBrowseHeaderAvailable() {
        PackageManager pm = getPackageManager();
        Intent browse = new Intent();
        browse.setClassName("org.omnirom.omnistyle", "org.omnirom.omnistyle.BrowseHeaderActivity");
        return pm.resolveActivity(browse, 0) != null;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.LEGENDROM;
    }

    private void updateSmartPulldownSummary(int value) {
         Resources res = getResources();
 
         if (value == 0) {
             // Smart pulldown deactivated
             mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
         } else if (value == 3) {
             mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
         } else {
             String type = res.getString(value == 1
                     ? R.string.smart_pulldown_dismissable
                     : R.string.smart_pulldown_ongoing);
             mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
         }
     }

   /*@Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }*/
}
