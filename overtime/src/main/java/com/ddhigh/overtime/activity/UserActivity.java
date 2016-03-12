package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.PreferenceKey;
import com.ddhigh.overtime.model.Role;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.model.UserRole;
import com.ddhigh.overtime.util.AppUtil;
import com.ddhigh.overtime.util.HttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/12 0012
 */
@ContentView(R.layout.activity_user)
public class UserActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    @ViewInject(R.id.scrollView)
    PullToRefreshScrollView scrollView;
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtRealname)
    TextView txtRealname;
    @ViewInject(R.id.txtPhone)
    TextView txtPhone;
    @ViewInject(R.id.txtTotalTime)
    TextView txtTotalTime;

    @ViewInject(R.id.txtRole)
    TextView txtRole;
    @ViewInject(R.id.txtCreatedAt)
    TextView txtCreatedAt;

    @ViewInject(R.id.btnSetting)
    RelativeLayout btnSetting;
    @ViewInject(R.id.btnAbout)
    RelativeLayout btnAbout;
    @ViewInject(R.id.btnLogout)
    RelativeLayout btnLogout;
    @ViewInject(R.id.viewRedPointer)
    View viewRedPointer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        showActionBar();

        onLoaded();
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(this);

        checkUpdate();
    }

    private void checkUpdate() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isAutoCheck = sp.getBoolean(PreferenceKey.SETTING_ENABLE_AUTOUPDATE, false);
        if (!isAutoCheck) {
            return;
        }


        PackageInfo packageInfo = AppUtil.getAppInfo(this);
        RequestParams params = new RequestParams();
        params.put("platform", "android");
        params.put("version", packageInfo.versionName);
        final int currentVersionCode = packageInfo.versionCode;

        HttpUtil.get("/app/update", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int versionCode = response.getInt("versionCode");
                    if (currentVersionCode < versionCode) {
                        viewRedPointer.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onLoaded() {
        if (!TextUtils.isEmpty(application.getUser().getAvatar())) {
            ImageLoader.getInstance().displayImage(application.getUser().getAvatar() + "?imageView2/1/w/128", imageAvatar);
        }
        txtRealname.setText(application.getUser().getRealname());
        txtPhone.setText(application.getUser().getPhone());
        txtTotalTime.setText(String.format("%.2f小时", ((float) application.getUser().getTotal_time() / 60)));
        txtCreatedAt.setText(DateUtil.format(new Date((long) application.getUser().getCreated_at() * 1000), "yyyy-MM-dd") + "加入");
        try {
            UserRole userRole = dbManager.selector(UserRole.class).where("user_id", "=", application.getUser().getUser_id()).findFirst();
            if (userRole != null) {
                txtRole.setVisibility(View.VISIBLE);
                txtRole.setText(userRole.getRole_id() == Role.ROLE_DIRECTOR ? "主管" : "员工");
            } else {
                txtRole.setVisibility(View.GONE);
            }
        } catch (DbException e) {
            e.printStackTrace();
            txtRole.setVisibility(View.GONE);
        }
    }

    @Event(R.id.btnLogout)
    private void onLogout(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("确定退出当前账号?");
        b.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                User.loginRequired(UserActivity.this, false);
                finish();
            }
        });
        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.create().show();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadDataFromRemote();
    }

    private void loadDataFromRemote() {
        HttpUtil.get("/user/view", null, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(UserActivity.this, false);
                    finish();
                    return;
                }
                Log.e("user", "load user from remote fail", throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //同步本地
                try {
                    application.getUser().decode(response);
                    if (response.has("userRole")) {
                        JSONObject j = response.getJSONObject("userRole");
                        UserRole userRole = new UserRole();
                        userRole.decode(j);
                        dbManager.saveOrUpdate(userRole);
                    }
                    dbManager.saveOrUpdate(application.getUser());
                    onLoaded();
                    Log.i("user", "save user success: " + application.getUser().toString());
                } catch (JSONException | IllegalAccessException | DbException e) {
                    Log.e("user", "save user fail", e);
                }
            }

            @Override
            public void onFinish() {
                scrollView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Event(R.id.btnSetting)
    private void onSetting(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }
}
