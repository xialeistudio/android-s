package com.ddhigh.overtime.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ddhigh.overtime.MyApplication;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/11 0011
 */
public class BaseActivity extends AppCompatActivity {
    MyApplication application;
    DbManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MyApplication) getApplication();
        dbManager = x.getDb(application.getDaoConfig());
        application.add(this);
    }

    /**
     * 显示动作栏
     */
    protected void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
