package com.ddhigh.dodo.widget;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.ddhigh.dodo.Config;
import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.util.BitmapUtil;
import com.ddhigh.dodo.util.DateUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @project Study
 * @package com.ddhigh.dodo.widget
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
@ContentView(R.layout.activity_select_image)
public class SelectImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }

    @Event(R.id.btnPhoto)
    private void onBtnPhotoClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Config.Constants.CODE_PICK_IMAGE_FROM_PHOTO);
    }

    @Event(R.id.btnCamera)
    private void onBtnCameraClicked(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Config.Constants.CODE_PICK_IMAGE_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.Constants.CODE_PICK_IMAGE_FROM_CAMERA:
                if (data != null && data.hasExtra("data")) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    bitmap = BitmapUtil.scale(bitmap, 640.0f / bitmap.getWidth());
                    try {
                        File path = new File(((MyApplication) getApplication()).appPath, DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        Intent intent = new Intent();
                        intent.putExtra("path", path.getAbsolutePath());
                        setResult(RESULT_OK, intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finish();
                break;
            case Config.Constants.CODE_PICK_IMAGE_FROM_PHOTO:
                if(data != null){
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                        bitmap = BitmapUtil.scale(bitmap, 640.0f / bitmap.getWidth());
                        File path = new File(((MyApplication) getApplication()).appPath, DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        Intent intent = new Intent();
                        intent.putExtra("path", path.getAbsolutePath());
                        setResult(RESULT_OK, intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
