package com.ddhigh.takephoto;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    Button btnCamera;
    Button btnPhoto;
    ImageView imageView;

    private final int CODE_CAMERA = 1;
    private final int CODE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void onClick(View view) {
        //创建输出文件
        File f = new File(Environment.getExternalStorageDirectory(), "test-1.jpg");
        if (view.getId() == R.id.btnCamera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(intent, CODE_CAMERA);
        }else if(view.getId() == R.id.btnPhoto){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,CODE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_CAMERA) {
            //检测结果是否包含缩略图
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                //如果没有缩略图，说明图片在Uri

                int width = imageView.getWidth();
                int height = imageView.getHeight();

                BitmapFactory.Options options = new BitmapFactory.Options();
                File f = new File(Environment.getExternalStorageDirectory(), "test-1.jpg");
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(f.getPath(), options);

                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;
                //缩放比率
                int scaleFactor = Math.min(imageWidth / width, imageHeight / height);
                //解码图像文件
                options.inJustDecodeBounds = false;
                options.inSampleSize = scaleFactor;
                options.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);
                imageView.setImageBitmap(bitmap);
            }
        }else if (requestCode == CODE_PHOTO){
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
