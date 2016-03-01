package com.ddhigh.dodo.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.dodo.Config;
import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.BitmapUtil;
import com.ddhigh.dodo.util.HttpUtil;
import com.ddhigh.dodo.widget.LoadingDialog;
import com.ddhigh.dodo.widget.SelectImageActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

/**
 * @project Study
 * @package com.ddhigh.dodo.user
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        displayUser();


        userChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(MyApplication.TAG, "user changed");
                displayUser();
            }
        };
        Log.d(MyApplication.TAG, "register user change receiver");
        registerReceiver(userChangeReceiver, new IntentFilter(Config.Constants.BROADCAST_USER_CHANGED));
    }

    @ViewInject(R.id.imageAvatar)
    private ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    private TextView txtNickname;
    @ViewInject(R.id.txtSex)
    private TextView txtSex;
    @ViewInject(R.id.txtEmail)
    private TextView txtEmail;
    @ViewInject(R.id.txtMobile)
    private TextView txtMobile;

    private void displayUser() {
        MyApplication app = (MyApplication) getApplication();
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(65), DensityUtil.dip2px(65))
                .setRadius(DensityUtil.dip2px(4))
                .setFadeIn(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.img_avatar_placeholder)
                .setFailureDrawableId(R.drawable.img_avatar_placeholder)
                .build();
        x.image().bind(imageAvatar, BitmapUtil.thumbQiniu(app.user.getAvatar(), "/1/w/128"), imageOptions);

        txtNickname.setText(app.user.getNickname());
        txtSex.setText(app.user.getReadableSex());
        txtEmail.setText(app.user.getEmail());
        txtMobile.setText(app.user.getMobile());
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

    @Event(R.id.btnAvatar)
    private void onBtnAvatarClicked(View view) {
        //打开图片选择
        startActivityForResult(new Intent(this, SelectImageActivity.class), Config.Constants.CODE_PICK_IMAGE);
    }

    @Event(R.id.btnNickname)
    private void onBtnNicknameClicked(View view) {
        startActivity(new Intent(this, ModifyNicknameActivity.class));
    }

    @Event(R.id.btnSex)
    private void onBtnSexClicked(View view) {
        final MyApplication application = (MyApplication) getApplication();
        new AlertDialog.Builder(this)
                .setTitle("性别")
                .setSingleChoiceItems(new String[]{"男", "女", "其他"}, application.user.getSex(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d(MyApplication.TAG, "checked sex: " + which);
                        application.user.setSex(which);
                        application.user.async(new Callback.CommonCallback<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    application.user.parse(result);
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sp.edit();
                                    //写入本地存储
                                    editor.putString(User.PREF_USER, result.toString());
                                    editor.apply();
                                    //发送广播
                                    Intent intent = new Intent();
                                    intent.setAction(Config.Constants.BROADCAST_USER_CHANGED);
                                    sendBroadcast(intent);
                                } catch (JSONException e) {
                                    onError(e, true);
                                }
                            }

                            private void showToast(String msg) {
                                Toast.makeText(UserInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                ex.printStackTrace();
                                showToast("保存失败");
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.Constants.CODE_PICK_IMAGE:
                if (data != null) {
                    String path = data.getStringExtra("path");
                    if (path != null) {
                        //上传
                        final File f = new File(path);
                        RequestParams requestParams = HttpUtil.prepare("/mcm/api/file");
                        requestParams.addBodyParameter("file", f);
                        requestParams.addBodyParameter("filename", f.getName());
                        requestParams.addBodyParameter("type", "image/jpeg");
                        final LoadingDialog loadingDialog = new LoadingDialog(this);
                        loadingDialog.setTitle("上传中");
                        loadingDialog.setCancelable(false);
                        loadingDialog.show();
                        x.http().post(requestParams, new Callback.CommonCallback<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                if (result.has("error")) {
                                    try {
                                        JSONObject error = result.getJSONObject("error");
                                        showToast(error.getString("message"));
                                    } catch (JSONException e) {
                                        onError(e, true);
                                    }
                                } else {
                                    f.delete();
                                    try {
                                        String url = result.getString("url");
                                        //上传成功
                                        showToast("上传成功");
                                        //更新用户信息
                                        Log.d(MyApplication.TAG, "upload url: " + url);

                                        updateUserAvatar(loadingDialog, url);
                                    } catch (JSONException e) {
                                        onError(e, true);
                                    }
                                }
                            }

                            private void showToast(String message) {
                                Toast.makeText(UserInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                ex.printStackTrace();
                                showToast("上传失败");
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {
                            }
                        });
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUserAvatar(final LoadingDialog loadingDialog, final String url) {
        loadingDialog.setTitle("更新中");
        final MyApplication app = (MyApplication) getApplication();
        app.user.setAvatar(url);
        app.user.async(new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                if (result.has("error")) {
                    try {
                        JSONObject error = result.getJSONObject("error");
                        showToast(error.getString("message"));
                    } catch (JSONException e) {
                        onError(e, true);
                    }
                } else {
                    try {
                        app.user.parse(result);
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sp.edit();
                        //写入本地存储
                        editor.putString(User.PREF_USER, result.toString());
                        editor.apply();

                        Intent intent = new Intent();
                        intent.setAction(Config.Constants.BROADCAST_USER_CHANGED);
                        sendBroadcast(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void showToast(String message) {
                Toast.makeText(UserInfoActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                showToast("更新失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadingDialog.dismiss();
            }
        });
    }

    BroadcastReceiver userChangeReceiver;


    @Override
    protected void onDestroy() {
        unregisterReceiver(userChangeReceiver);
        super.onDestroy();
    }
}
