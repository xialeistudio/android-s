package com.ddhigh.dodo.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.ddhigh.dodo.Config;
import com.ddhigh.dodo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

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
                if (data != null) {
                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        //裁剪
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setType("image/*");
                        intent.putExtra("data", thumbnail);
                        intent.putExtra("crop", true);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, Config.Constants.CODE_CROP_IMAGE);
                    } else {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            case Config.Constants.CODE_CROP_IMAGE:
                if (data != null) {
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            case Config.Constants.CODE_PICK_IMAGE_FROM_PHOTO:
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
