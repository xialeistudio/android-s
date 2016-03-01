package com.ddhigh.dodo.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;

import org.xutils.view.annotation.Event;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.main
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class RemindListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        x.view().inject(this, v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuCreate:
                Log.d(MyApplication.TAG,"!~");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
