package com.ddhigh.dodo.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
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
//
//    @Event(R.id.btnCreate)
//    private void onBtnCreateClicked(View view) {
//        Log.d(MyApplication.TAG, "onBtnCreateClicked");
//    }
}
