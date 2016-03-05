package com.ddhigh.joke.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.mylibrary.util.AppUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.joke.user
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
@ContentView(R.layout.activity_user)
public class UserActivity extends AppCompatActivity {
    MyApplication application;

    @ViewInject(R.id.txtAppInfo)
    TextView txtAppInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        application = (MyApplication) getApplication();
        //TODO:加载用户信息
        //显示App信息
        String appInfo = "段子 " + AppUtil.getAppInfo(this).versionName;
        txtAppInfo.setText(appInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Event(R.id.btnLogout)
    private void onBtnLogoutClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定退出当前账号？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //发送退出登录广播
                Intent intent = new Intent();
                intent.setAction(Actions.ACTION_LOGOUT);
                sendBroadcast(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
