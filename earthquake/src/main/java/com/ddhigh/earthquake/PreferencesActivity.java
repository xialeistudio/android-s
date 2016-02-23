package com.ddhigh.earthquake;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class PreferencesActivity extends PreferenceActivity {
    public final static String PREF_MIN_MAG = "PREF_MIN_MAG";
    public final static String PREF_UPDATE_FREQ= "PREF_UPDATE_FREQ";
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }
}
