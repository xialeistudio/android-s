package com.ddhigh.joke.user;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.joke.config.Config;
import com.ddhigh.joke.util.HttpUtil;
import com.ddhigh.mylibrary.util.BitmapUtil;
import com.ddhigh.mylibrary.util.DateUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @project Study
 * @package com.ddhigh.joke.user
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
@ContentView(R.layout.activity_user_edit)
public class EditActivity extends AppCompatActivity {
    MyApplication application;
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    TextView txtNickname;
    @ViewInject(R.id.txtEmail)
    TextView txtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        application = (MyApplication) getApplication();
        ImageLoader imageLoader = ImageLoader.getInstance();
        //显示用户信息
        if (!TextUtils.isEmpty(application.user.getAvatar())) {
            imageLoader.displayImage(application.user.getAvatar() + "?imageView2/1/w/128", imageAvatar);
        }
        if (!TextUtils.isEmpty(application.user.getNickname())) {
            txtNickname.setText(application.user.getNickname());
        }
        if (!TextUtils.isEmpty(application.user.getEmail())) {
            txtEmail.setText(application.user.getEmail());
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

    @Event(R.id.btnAvatar)
    private void onBtnAvatarClicked(View view) {
        //显示alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.d(MyApplication.TAG, "select image: " + which);
                if (which == 0) {
                    //拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Config.REQUEST_CODE_FROM_CAMERA);
                } else {
                    //相册
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Config.REQUEST_CODE_FROM_PHOTO);
                }
            }
        });
        builder.setTitle("请选择图片");
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_CODE_FROM_CAMERA:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    bitmap = BitmapUtil.scale(bitmap, 640.0f / bitmap.getWidth());
                    //上传
                    try {
                        File path = new File(application.applicationPath, DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        //上传
                        uploadAvatar(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "处理图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Config.REQUEST_CODE_FROM_PHOTO:
                if (data != null) {
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                        bitmap = BitmapUtil.scale(bitmap, 640.0f / bitmap.getWidth());
                        File path = new File(application.applicationPath, DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        //上传
                        uploadAvatar(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "处理图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 上传头像
     *
     * @param path 头像路径
     * @throws FileNotFoundException
     */
    private void uploadAvatar(File path) throws FileNotFoundException {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("上传中");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);

        RequestParams params = new RequestParams();
        params.put("file", path);

        HttpUtil.post("/file", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    HttpUtil.handleError(response.toString());
                    String url = response.getString("url");
                    updateUserAvatar(url);
                } catch (JSONException | JokeException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(EditActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                progressDialog.setProgress((int) (bytesWritten * 100 / totalSize));
            }
        });
    }

    /**
     * 更新头像
     *
     * @param url 头像url地址
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    private void updateUserAvatar(final String url) throws JSONException, UnsupportedEncodingException {
        Log.d(MyApplication.TAG, "update avatar: " + url);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("更新中");
        progressDialog.setCancelable(false);

        JSONObject json = new JSONObject();
        json.put("avatar", url);

        HttpUtil.put(this, "/users/" + application.user.getId(), json, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    HttpUtil.handleError(response.toString());
                    application.user.setAvatar(url);
                    application.user.save(getApplicationContext());
                    //ui
                    ImageLoader.getInstance().displayImage(url + "?imageView2/1/w/128", imageAvatar);
                    //广播用户信息改变
                    Intent intent = new Intent();
                    intent.setAction(Actions.ACTION_USER_CHANGED);
                    sendBroadcast(intent);
                    Toast.makeText(EditActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                } catch (JSONException | JokeException e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.hide();
            }
        });
    }

    @Event(R.id.btnNickname)
    private void onBtnNicknameClicked(View view) {
        final EditText txtNickname = new EditText(this);
        txtNickname.setText(application.user.getNickname());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入昵称");
        builder
                .setView(txtNickname)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        updateUserNickname(txtNickname.getText().toString().trim());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void updateUserNickname(final String trim) {
        if (!TextUtils.isEmpty(trim) && !trim.equals(application.user.getNickname())) {
            try {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("更新中");
                progressDialog.setCancelable(false);

                JSONObject json = new JSONObject();
                json.put("nickname", trim);

                try {
                    HttpUtil.put(this, "/users/" + application.user.getId(), json, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                HttpUtil.handleError(response.toString());
                                application.user.setNickname(trim);
                                application.user.save(getApplicationContext());
                                //ui
                                txtNickname.setText(trim);
                                //广播用户信息改变
                                Intent intent = new Intent();
                                intent.setAction(Actions.ACTION_USER_CHANGED);
                                sendBroadcast(intent);
                                Toast.makeText(EditActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            } catch (JSONException | JokeException e) {
                                e.printStackTrace();
                                Toast.makeText(EditActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EditActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onStart() {
                            progressDialog.show();
                        }

                        @Override
                        public void onFinish() {
                            progressDialog.hide();
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
