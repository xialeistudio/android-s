package com.ddhigh.earthquake;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Date;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "x1x1x1";
    SimpleCursorAdapter simpleCursorAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[]{EarthquakeProvider.KEY_SUMMARY}, new int[]{android.R.id.text1}, 0);
        setListAdapter(simpleCursorAdapter);
        getLoaderManager().initLoader(0, null, this);
        refreshEarthquakes();
    }

    public void refreshEarthquakes() {

        getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);

        getActivity().stopService(new Intent(getActivity(), EarthquakeUpdateService.class));
        getActivity().startService(new Intent(getActivity(), EarthquakeUpdateService.class));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                EarthquakeProvider.KEY_ID,
                EarthquakeProvider.KEY_SUMMARY
        };
        MainActivity activity = (MainActivity) getActivity();
        String where = EarthquakeProvider.KEY_MAGNITUDE + ">" + activity.minimunMagnitude;
        return new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI, projection, where, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ContentResolver cr = getActivity().getContentResolver();
        Cursor result = cr.query(ContentUris.withAppendedId(EarthquakeProvider.CONTENT_URI, id), null, null, null, null);

        assert result != null;
        if (result.moveToFirst()) {
            Date date = new Date(result.getLong(result.getColumnIndex(EarthquakeProvider.KEY_DATE)));
            String defailt = result.getString(result.getColumnIndex(EarthquakeProvider.KEY_DETAILS));
            double magnitude = result.getDouble(result.getColumnIndex(EarthquakeProvider.KEY_MAGNITUDE));
            String link = result.getString(result.getColumnIndex(EarthquakeProvider.KEY_LINK));
            double lat = result.getDouble(result.getColumnIndex(EarthquakeProvider.KEY_LOCATION_LAT));
            double lng = result.getDouble(result.getColumnIndex(EarthquakeProvider.KEY_LOCATION_LNG));

            Location location = new Location("db");
            location.setLatitude(lat);
            location.setLongitude(lng);

            Quake quake = new Quake(date, defailt, location, magnitude, link);

            DialogFragment newFragment = EarthquakeDialog.newInstance(getActivity(),quake);
            newFragment.show(getFragmentManager(),"dialog");
        }
    }
}
