package com.ddhigh.overtime.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.xutils.x;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/12 0012
 */
public class UserActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        TextView v = new TextView(this);
        v.setText("!~!");
        setContentView(v);
    }
}
