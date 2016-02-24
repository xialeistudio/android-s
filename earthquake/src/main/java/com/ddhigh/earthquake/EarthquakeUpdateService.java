package com.ddhigh.earthquake;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/24 0024
 */
public class EarthquakeUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addNewQuake(Quake quake) {
        ContentResolver cr = getContentResolver();
        String w = EarthquakeProvider.KEY_DATE + " = " + quake.getDate().getTime();
        //如果不存在，则插入
        Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        assert query != null;
        if (query.getCount() == 0) {
            ContentValues values = new ContentValues();

            values.put(EarthquakeProvider.KEY_DATE, quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_DETAILS, quake.getDefails());
            values.put(EarthquakeProvider.KEY_SUMMARY, quake.toString());

            double lat = quake.getLocation().getLatitude();
            double lng = quake.getLocation().getLongitude();
            values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
            values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
            values.put(EarthquakeProvider.KEY_LINK, quake.getLink());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, quake.getMagnitude());
            cr.insert(EarthquakeProvider.CONTENT_URI, values);
        }
        query.close();
    }

    public void refreshEarthquakes() {
        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);

            URLConnection connection;
            Log.d(EarthquakeListFragment.TAG, "openConnection...");
            connection = url.openConnection();

            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            int responseCode = httpURLConnection.getResponseCode();


            Log.d(EarthquakeListFragment.TAG, "completeConnection...");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                String result = readInStream(inputStream);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("features");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    Location l = new Location("dummyGPS");
                    JSONArray coordinates = item.getJSONObject("geometry").getJSONArray("coordinates");
                    l.setLatitude(coordinates.getDouble(0));
                    l.setLongitude(coordinates.getDouble(1));
                    final Quake quake = new Quake(
                            new Date(item.getJSONObject("properties").getLong("time")),
                            item.getJSONObject("properties").getString("title"),
                            l,
                            item.getJSONObject("properties").getDouble("mag"),
                            item.getJSONObject("properties").getString("url")
                    );


                    addNewQuake(quake);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private Timer updateTimer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //检索SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int updateFreq = Integer.parseInt(sharedPreferences.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        boolean autoUpdateChecked = sharedPreferences.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        if (autoUpdateChecked) {
            updateTimer = new Timer("earthquakeUpdates");
            updateTimer.scheduleAtFixedRate(doRefresh, 0, updateFreq * 60 * 1000);
        } else {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    refreshEarthquakes();
                }
            });
            t.start();
        }
        return Service.START_STICKY;
    }

    private TimerTask doRefresh = new TimerTask() {
        @Override
        public void run() {
            refreshEarthquakes();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        updateTimer = new Timer("earthquakeUpdates");
    }
}
