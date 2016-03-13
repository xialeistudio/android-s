package com.ddhigh.mylibrary.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.mylibrary.R;
import com.ddhigh.mylibrary.adapter.ImageAdapter;
import com.ddhigh.mylibrary.bean.FolderBean;
import com.ddhigh.mylibrary.dialog.ListImageDirPopupWindow;
import com.ddhigh.mylibrary.util.DateUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImagePickerActivity extends AppCompatActivity implements ImageAdapter.onCameraClickedListener {
    public static final int MULTI_SELECT = 440;
    public static final int SINGLE_SELECT = 54;
    private File appPath;
    private static final String TAG = "IMP_IMAGEPICKER";
    private static final int CAMERA = 735;


    private ListImageDirPopupWindow mDirPopupWindow;
    private GridView mGridView;
    private List<String> mImgs;
    private ImageAdapter mImgAdapter;
    private int maxSelectCount = 9;

    private RelativeLayout mBottomLayout;
    private TextView mDirName;
    private TextView mDirCount;
    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> mFolderBeans = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    private static final int DATA_LOADED = 128;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOADED) {
                mProgressDialog.dismiss();

                //绑定数据到view中
                data2View();
                //初始化dirPopupWindow
                initDirPopupWindow();
            }
        }
    };

    private void initDirPopupWindow() {
        mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
        mDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        mDirPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectListener() {
            @Override
            public void onSelected(FolderBean bean) {
                mCurrentDir = new File(bean.getDir());
                mImgs = new ArrayList<String>();
                mImgs.add(0, "camera");
                mImgs.addAll(Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return isImgFile(filename);
                    }
                })));

                mImgAdapter = new ImageAdapter(ImagePickerActivity.this, mImgs, mCurrentDir.getAbsolutePath(), ImagePickerActivity.this);
                mImgAdapter.setMaxSelectedImage(maxSelectCount);
                mGridView.setAdapter(mImgAdapter);

                mDirName.setText(bean.getName());
                mDirCount.setText(bean.getCount() + "");
                mDirPopupWindow.dismiss();
            }
        });
    }

    /**
     * 内容区域变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }


    /**
     * 内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = new ArrayList<>();
        mImgs.add(0, "camera");
        mImgs.addAll(Arrays.asList(mCurrentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return isImgFile(filename);
            }
        })));

        mImgAdapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath(), this);
        mImgAdapter.setMaxSelectedImage(maxSelectCount);
        mGridView.setAdapter(mImgAdapter);

        mDirCount.setText(mMaxCount + "");
        mDirName.setText(mCurrentDir.getName());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imp_activity_imagepicker);
        initAppPath();
        initActionBar();
        initView();
        initDatas();
        initEvent();

        Intent intent = getIntent();
        if (intent.hasExtra("maxSelectCount")) {
            maxSelectCount = intent.getIntExtra("maxSelectCount", 9);
        }
    }

    private void initAppPath() {
        appPath = new File(Environment.getExternalStorageDirectory(), getPackageName());
        if (!appPath.exists() && !appPath.isDirectory()) {
            appPath.mkdir();
        }
    }

    private void initEvent() {
        mBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
                mDirPopupWindow.showAsDropDown(mBottomLayout, 0, 0);
                lightOff();
            }
        });
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menuDone) {
            //将图片路径回传
            if (mImgAdapter.getSelectedImg().size() > 0) {
                Intent i = new Intent();
                if (mImgAdapter.getSelectedImg().size() == 1) {
                    String image = (String) mImgAdapter.getSelectedImg().toArray()[0];
                    i.putExtra("image", image);
                    i.putExtra("type", SINGLE_SELECT);
                } else {
                    i.putExtra("type", MULTI_SELECT);
                    String[] images = (String[]) mImgAdapter.getSelectedImg().toArray();
                    i.putExtra("image", images);
                    i.putExtra("type", MULTI_SELECT);
                }
                setResult(RESULT_OK, i);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.id_gridView);
        mBottomLayout = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mDirName = (TextView) findViewById(R.id.id_dir_name);
        mDirCount = (TextView) findViewById(R.id.id_dir_count);
    }


    /**
     * 利用ContentProvider扫描手机中所有图片
     */
    private void initDatas() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        //开启线程扫描图片
        new Thread() {
            @Override
            public void run() {
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = ImagePickerActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImgUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                if (cursor == null) {
                    return;
                }
                //遍历过的文件夹
                Set<String> mDirPaths = new HashSet<>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImagePath(path);
                    }

                    if (parentFile.list() == null) {
                        continue;
                    }

                    //图片数量
                    int picCount = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return isImgFile(filename);
                        }
                    }).length;
                    folderBean.setCount(picCount);
                    mFolderBeans.add(folderBean);

                    if (picCount > mMaxCount) {
                        mMaxCount = picCount;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                //通知handler扫描完成
                mHandler.sendEmptyMessage(DATA_LOADED);
            }
        }.start();

    }

    private boolean isImgFile(String filename) {
        return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg");
    }

    @Override
    public void onCameraClicked() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA:
                if (data != null) {
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    File t = new File(appPath, DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
                    try {
                        BufferedOutputStream bfo = new BufferedOutputStream(new FileOutputStream(t));
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, bfo);
                        bfo.flush();
                        bfo.close();

                        Intent i = new Intent();
                        i.putExtra("image", t.getAbsolutePath());
                        i.putExtra("type", SINGLE_SELECT);
                        setResult(RESULT_OK, i);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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
