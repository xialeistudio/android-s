package com.ddhigh.joke.item;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Config;
import com.ddhigh.mylibrary.util.BitmapUtil;
import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.mylibrary.widget.NoScrollGridView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        adapter = new ImageAdapter(this,images);
        gridImages.setAdapter(adapter);
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

    private void post() {
        String content = txtContent.getText().toString().trim();//文本内容
        Log.d(MyApplication.TAG, "content: " + content);
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
