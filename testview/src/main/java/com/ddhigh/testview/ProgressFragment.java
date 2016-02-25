package com.ddhigh.testview;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddhigh.testview.widget.CircleProgressView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @project Study
 * @package com.ddhigh.testview
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
public class ProgressFragment extends DialogFragment {
    CircleProgressView circleProgressView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_progress, container, false);
        circleProgressView = (CircleProgressView) v.findViewById(R.id.circleProgressView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int progress = circleProgressView.getProgress();
                if (progress < 100) {
                    progress++;
                    circleProgressView.setProgressNotInUiThread(progress);
                } else {
                    timer.cancel();
                }
            }
        }, 0, 100);
    }
}
