package com.ddhigh.joke.item;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.joke.config.Config;
import com.ddhigh.joke.util.HttpUtil;
import com.ddhigh.mylibrary.util.BitmapUtil;
import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.mylibrary.widget.NoScrollGridView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_item_post)
public class PostActivity extends AppCompatActivity {
    @ViewInject(R.id.txtContent)
    EditText txtContent;
    @ViewInject(R.id.gridImages)
    NoScrollGridView gridImages;

    List<String> images;
    ImageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        images = new ArrayList<>();
        adapter = new ImageAdapter(this, images);
        gridImages.setAdapter(adapter);

        gridImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                images.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuDone:
                post();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    List<String> urls = new ArrayList<>();

    private void post() {
        String content = txtContent.getText().toString().trim();//文本内容
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入段子内容", Toast.LENGTH_SHORT).show();
            return;
        }
        //上传图片
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.show();
        if (images.size() > 0) {
            dialog.setMessage("上传中");
            final int[] progress = {0};
            final int max = images.size();
            for (String path : images) {
                RequestParams params = new RequestParams();
                try {
                    HttpUtil.client.setTimeout(30000);
                    File f = new File(path);
                    params.put("file", f);
                    HttpUtil.post("/file", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                HttpUtil.handleError(response.toString());
                                String url = response.getString("url");
                                urls.add(url);
                                Log.d(MyApplication.TAG, "upload image: " + url);
                            } catch (JSONException | JokeException e) {
                                e.printStackTrace();
                            }
                            progress[0]++;
                            handleUpload(dialog, progress[0], max);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            progress[0]++;
                            handleUpload(dialog, progress[0], max);
                            throwable.printStackTrace();
                        }
                    });
                } catch (FileNotFoundException e) {
                    progress[0]++;
                    handleUpload(dialog, progress[0], max);
                    e.printStackTrace();
                }
            }
        } else {
            dialog.setMessage("发表中");
            handleUpload(dialog, 0, 0);
        }
    }

    private void handleUpload(final ProgressDialog dialog, int progress, int max) {
        if (progress == max) {
            Log.d(MyApplication.TAG, "upload completed.");
            //开始发表
            dialog.setMessage("发表中");
            MyApplication application = (MyApplication) getApplication();
            JSONObject json = new JSONObject();
            try {
                json.put("text", txtContent.getText().toString().trim());
                json.put("images", new JSONArray(urls));
                json.put("viewCount", 0);
                json.put("userId", application.user.getId());
                json.put("praiseCount", 0);
                json.put("unpraiseCount", 0);
                json.put("commentCount", 0);

                HttpUtil.post(this, "/jokes", json, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            response = HttpUtil.handleMongoId(response);
                            HttpUtil.handleError(response.toString());
                            String id = response.getString("_id");
                            //发送广播
                            Intent i = new Intent();
                            i.setAction(Actions.ACTION_NEW_JOKE);
                            sendBroadcast(i);
                            Intent intent = new Intent(PostActivity.this, ViewActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();

                        } catch (JSONException | JokeException e) {
                            e.printStackTrace();
                            Toast.makeText(PostActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PostActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(this, "发表失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Event(R.id.imageAddImage)
    private void addImage(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Config.REQUEST_CODE_FROM_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MyApplication application = (MyApplication) getApplication();
        switch (requestCode) {
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
                        //添加到list
                        images.add(path.getAbsolutePath());
                        //显示出来
                        adapter.notifyDataSetChanged();
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
}
