package com.ddhigh.overtime.activity;

import android.util.Log;
import android.view.View;

import com.ddhigh.overtime.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/11 0011
 */
@ContentView(R.layout.activity_overtime_cu)
public class OvertimeCreateActivity extends OvertimeFormBaseActivity {

    @Event(R.id.btnSubmit)
    private void onSubmit(View view) {
        Log.d("overtime-form", "btnSubmit clicked");
    }
}
