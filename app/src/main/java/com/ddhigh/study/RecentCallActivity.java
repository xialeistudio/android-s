package com.ddhigh.study;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * @project Study
 * @package com.ddhigh.study
 * @user xialeistudio
 * @date 2016/2/22 0022
 */
public class RecentCallActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_call);

        listView = (ListView) findViewById(R.id.listRecentCall);


        LoaderManager.LoaderCallbacks<Cursor> loaded = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(RecentCallActivity.this, CallLog.Calls.CONTENT_URI, null, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                String[] fromColumns = new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER};

                int[] toLayoutIDS = new int[]{R.id.nameTextView, R.id.numberTextView};

                SimpleCursorAdapter myAdapter;
                myAdapter = new SimpleCursorAdapter(RecentCallActivity.this, R.layout.mysimplecursorlayout, data, fromColumns, toLayoutIDS);

                listView.setAdapter(myAdapter);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };

        getLoaderManager().initLoader(0, null, loaded);
    }
}
