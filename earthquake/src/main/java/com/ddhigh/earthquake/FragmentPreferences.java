package com.ddhigh.earthquake;

import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.List;
import java.util.Objects;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class FragmentPreferences extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        Log.d("checkecheck",fragmentName);
        return fragmentName.equals(UserPreferenceFragment.class.getName());
    }
}
