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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.mylibrary.activity.ImagePickerActivity;
import com.ddhigh.mylibrary.dialog.LoadingDialog;
import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.mylibrary.util.LocalImageLoader;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.PreferenceKey;
import com.ddhigh.overtime.constants.RequestCode;
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

import java.io.File;
import java.io.FileNotFoundException;
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
    private
    PullToRefreshScrollView scrollView;
    @ViewInject(R.id.imageAvatar)
    private
    ImageView imageAvatar;
    @ViewInject(R.id.txtRealname)
    private
    TextView txtRealname;
    @ViewInject(R.id.txtPhone)
    private
    TextView txtPhone;
    @ViewInject(R.id.txtTotalTime)
    private
    TextView txtTotalTime;

    @ViewInject(R.id.txtRole)
    private
    TextView txtRole;
    @ViewInject(R.id.txtCreatedAt)
    private
    TextView txtCreatedAt;

    @ViewInject(R.id.btnSetting)
    RelativeLayout btnSetting;
    @ViewInject(R.id.btnAbout)
    RelativeLayout btnAbout;
    @ViewInject(R.id.btnLogout)
    RelativeLayout btnLogout;
    @ViewInject(R.id.viewRedPointer)
    private
    View viewRedPointer;

    private String newVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        showActionBar();

        onLoaded();
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(this);

        checkUpdate();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
                        newVersion = "新版本：" + response.getString("versionName");
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

    @Event(R.id.btnAbout)
    private void onAbout(View v) {
        Intent i = new Intent(this, AboutActivity.class);
        if (!TextUtils.isEmpty(newVersion)) {
            i.putExtra("newVersion", newVersion);
        }
        startActivity(i);
    }

    @Event(R.id.imageAvatar)
    private void onAvatarClicked(View v) {
        Intent i = new Intent(this, ImagePickerActivity.class);
        i.putExtra("maxSelectCount", 1);
        startActivityForResult(i, RequestCode.PICKER_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.PICKER_IMAGE:
                if (resultCode == RESULT_OK) {
                    String image = data.getStringExtra("image");
                    initUpload(image);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initUpload(final String image) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.setMessage("上传中");
        dialog.show();
        try {
            uploadAvatar(image, dialog);
        } catch (FileNotFoundException e) {
            Toast.makeText(UserActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadAvatar(final String image, final LoadingDialog dialog) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("file", new File(image));
        HttpUtil.post("/file/upload", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String url = response.getString("url");
                    LocalImageLoader.getInstance().loadImage(image, imageAvatar);
                    updateAvatar(url, dialog);
                } catch (JSONException e) {
                    onFailure(statusCode, headers, e, response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(UserActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void updateAvatar(String url, final LoadingDialog dialog) {
        dialog.setMessage("更新中");
        RequestParams params = new RequestParams();
        params.put("avatar", url);
        HttpUtil.post("/user/edit", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(UserActivity.this, true);
                    finish();
                    return;
                }
                Log.d("uploadAvatar", errorResponse.toString(), throwable);
                Toast.makeText(UserActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        switch (resultCode) {
            case RequestCode.LOGIN:
                if (resultCode == RESULT_OK) {
                    onLoaded();
                }
                break;
            default:
                super.onActivityReenter(resultCode, data);
        }
    }
}
