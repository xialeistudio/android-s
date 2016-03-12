package com.ddhigh.overtime.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class CheckUpdateActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showActionBar();
        Log.d("checkUpdate", "onCreate");
    }
}
