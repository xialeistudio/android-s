package com.ddhigh.earthquake;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/23 0023
 */
public class EarthquakeSearchResults extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "0";
        if (args != null) {
            query = args.getString(QUERY_EXTRA_KEY);
        }

        String[] projection = {EarthquakeProvider.KEY_ID, EarthquakeProvider.KEY_SUMMARY};
        String where = EarthquakeProvider.KEY_SUMMARY + " LIKE \"%" + query + "%\"";

        String[] whereArgs = null;
        String sortOrder = EarthquakeProvider.KEY_SUMMARY + " collate localized asc";

        return new CursorLoader(this, EarthquakeProvider.CONTENT_URI, projection, where, whereArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[]{EarthquakeProvider.KEY_SUMMARY}, new int[]{android.R.id.text1}, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(getIntent());
    }

    private static final String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

    private void parseIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);


            Bundle args = new Bundle();
            args.putString(QUERY_EXTRA_KEY, searchQuery);

            getLoaderManager().restartLoader(0, args, this);
        }
    }
}
