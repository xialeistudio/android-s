package com.ddhigh.earthquake;

import android.app.ListFragment;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class EarthquakeListFragment extends ListFragment {
    ArrayAdapter<Quake> aa;
    ArrayList<Quake> al = new ArrayList<>();
    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<>(getActivity(), layoutID, al);
        setListAdapter(aa);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthquakes();
            }
        });
        t.start();
    }

    public void refreshEarthquakes() {
        al.clear();
        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            int responseCode = httpURLConnection.getResponseCode();

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

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addNewQuake(quake);
                        }
                    });
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void addNewQuake(Quake quake) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (quake.getMagnitude() > mainActivity.minimunMagnitude) {
            al.add(quake);
            aa.notifyDataSetChanged();
        }
    }

    private String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
