package com.ddhigh.joke.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @project android-s
 * @package com.ddhigh.mylibrary.activity
 * @user xialeistudio
 * @date 2016/3/8 0008
 */
public class ImageViewActivity extends Activity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        imageView = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        Log.d(MyApplication.TAG, "path: " + path);
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists()) {
                //本地图片
                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //网络图片
                ImageLoader.getInstance().displayImage(path, imageView);
            }
        }
    }
}
