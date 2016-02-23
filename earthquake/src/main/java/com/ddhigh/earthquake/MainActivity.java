package com.ddhigh.earthquake;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    static final private int MENU_PREFERENCES = Menu.FIRST + 1;
    static final private int MENU_UPDATE = Menu.FIRST + 2;

    private static final int SHOW_PREFERENCE = 1;


    public int minimunMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateFromPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_PREFERENCES:
                Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? PreferencesActivity.class : FragmentPreferences.class;
                Intent intent = new Intent(this, c);
                startActivityForResult(intent, SHOW_PREFERENCE);
                return true;
        }
        return false;
    }


    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);

        minimunMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFERENCE) {
            updateFromPreferences();
            FragmentManager fragmentManager = getFragmentManager();
            final EarthquakeListFragment earthquakeListFragment = (EarthquakeListFragment) fragmentManager.findFragmentById(R.id.EarthquakeListFragment);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    earthquakeListFragment.refreshEarthquakes();
                }
            });
            t.start();
        }
    }
}
