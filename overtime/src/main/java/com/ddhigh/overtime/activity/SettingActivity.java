package com.ddhigh.overtime.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.ddhigh.overtime.MyApplication;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.PreferenceKey;

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();


    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);
        }

        @Override
        public void onResume() {
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PreferenceKey.SETTING_ENABLE_PUSH) || key.equals(PreferenceKey.SETTING_SILENT_MODE)) {
                Activity activity = getActivity();
                MyApplication application = (MyApplication) activity.getApplication();
                application.setPushWork(activity);
            }
        }


    }


}
