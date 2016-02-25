package com.ddhigh.earthquake;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
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

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/24 0024
 */
public class EarthquakeUpdateService extends IntentService {

    private Notification.Builder earthquakeNotificationBuilder;
    public static final int NOTIFICATION_ID = 1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public EarthquakeUpdateService(String name) {
        super(name);
    }

    public EarthquakeUpdateService() {
        super("EarthquakeUpdateService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int updateFreq = Integer.parseInt(sharedPreferences.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        boolean autoUpdateChecked = sharedPreferences.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
        if (autoUpdateChecked) {
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq * 60 * 1000;
            alarmManager.setInexactRepeating(alarmType, timeToRefresh, updateFreq * 60 * 1000, alarmIntent);
        } else {
            alarmManager.cancel(alarmIntent);
        }

        refreshEarthquakes();
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

            broadcastNotification(quake);

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


    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String ALARM_ACTION = EarthquakeAlarmReceiver.ACTION_REFRESH_EARTHQUAKE_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);

        earthquakeNotificationBuilder = new Notification.Builder(this);
        earthquakeNotificationBuilder.setAutoCancel(true)
                .setTicker("Earthquake detected")
                .setSmallIcon(R.drawable.notification_template_icon_bg);
    }

    private void broadcastNotification(Quake quake) {
        Log.d("x1x1x1", "notification");
        Intent startActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0);


        earthquakeNotificationBuilder
                .setContentIntent(pendingIntent)
                .setWhen(quake.getDate().getTime())
                .setContentTitle("M: " + quake.getMagnitude())
                .setContentText(quake.getDefails());

        if (quake.getMagnitude() > 6) {
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            earthquakeNotificationBuilder.setSound(ringUri);
        }

        //振动器
        double vibrateLength = 100 * Math.exp(0.53 * quake.getMagnitude());
        long[] vibrate = new long[]{100, 100, (long) vibrateLength};
        earthquakeNotificationBuilder.setVibrate(vibrate);
        //LED灯
        int color;
        if (quake.getMagnitude() < 5.4) {
            color = Color.GREEN;
        } else if (quake.getMagnitude() < 6) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }

        earthquakeNotificationBuilder.setLights(color, (int) vibrateLength, (int) vibrateLength);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, earthquakeNotificationBuilder.getNotification());
    }
}
