package com.ddhigh.joke.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.joke.config.Config;
import com.ddhigh.joke.util.HttpUtil;
import com.ddhigh.mylibrary.util.AppUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
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
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    TextView txtNickname;

    BroadcastReceiver userChangedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        application = (MyApplication) getApplication();
        //显示用户信息
        loadUserInfo();
        //显示App信息
        String appInfo = "段子 " + AppUtil.getAppInfo(this).versionName;
        txtAppInfo.setText(appInfo);
        //注册用户信息改变监听器
        userChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                displayUserInfo();
            }
        };
        registerReceiver(userChangedReceiver, new IntentFilter(Actions.ACTION_USER_CHANGED));
    }

    private void displayUserInfo() {
        if (!TextUtils.isEmpty(application.user.getNickname())) {
            txtNickname.setText(application.user.getNickname());
        }
        if (!TextUtils.isEmpty(application.user.getAvatar())) {
            ImageLoader.getInstance().displayImage(application.user.getAvatar() + "?imageView2/1/w/128", imageAvatar);
        }
    }

    private void loadUserInfo() {
        final ImageLoader imageLoader = ImageLoader.getInstance();
        if (TextUtils.isEmpty(application.user.getNickname()) || TextUtils.isEmpty(application.user.getAvatar())) {
            //从远程加载用户数据
            HttpUtil.get("/user/" + application.user.getId(), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        HttpUtil.handleError(response.toString());
                        Log.d(MyApplication.TAG, "load user: " + response.toString());
                        if (response.has("nickname")) {
                            application.user.setNickname(response.getString("nickname"));
                        }
                        if (response.has("avatar")) {
                            application.user.setAvatar(response.getString("avatar"));
                        }

                        if (response.has("email")) {
                            application.user.setEmail(response.getString("email"));
                        }

                        application.user.save(getApplicationContext());

                        if (!TextUtils.isEmpty(application.user.getNickname())) {
                            txtNickname.setText(application.user.getNickname());
                        }

                        if (!TextUtils.isEmpty(application.user.getAvatar())) {
                            imageLoader.displayImage(application.user.getAvatar() + "?imageView2/1/w/128", imageAvatar);
                        }
                    } catch (JSONException | JokeException e) {
                        e.printStackTrace();
                        Toast.makeText(UserActivity.this, "加载用户信息失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (statusCode == 401) {
                        //用户注销广播
                        Intent i = new Intent();
                        i.setAction(Actions.ACTION_LOGOUT);
                        sendBroadcast(i);
                        //登录
                        Intent intent = new Intent();
                        intent.setAction(Actions.ACTION_LOGIN_REQUIRED);
                        sendBroadcast(intent);
                        finish();
                        return;
                    }
                    throwable.printStackTrace();
                    Toast.makeText(UserActivity.this, "加载用户信息失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //加载本地数据
            txtNickname.setText(application.user.getNickname());
            imageLoader.displayImage(application.user.getAvatar() + "?imageView2/1/w/200", imageAvatar);
        }
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

    @Event(R.id.btnUser)
    private void onBtnUserClicked(View view) {
        Intent i = new Intent(this, EditActivity.class);
        startActivityForResult(i, Config.REQUEST_CODE_EDIT_USER);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(userChangedReceiver);
        super.onDestroy();
    }
}
