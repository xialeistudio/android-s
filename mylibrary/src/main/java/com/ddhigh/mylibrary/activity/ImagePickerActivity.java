package com.ddhigh.mylibrary.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.mylibrary.R;
import com.ddhigh.mylibrary.bean.FolderBean;
import com.ddhigh.mylibrary.util.LocalImageLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImagePickerActivity extends AppCompatActivity {

    private static final String TAG = "IMP_IMAGEPICKER";
    private GridView mGridView;
    private List<String> mImgs;
    private ImageAdapter mImgAdapter;

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
            }
        }
    };

    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg");
            }
        }));
        mImgAdapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath());
        mGridView.setAdapter(mImgAdapter);

        mDirCount.setText(mMaxCount + "");
        mDirName.setText(mCurrentDir.getName());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imp_activity_imagepicker);

        initActionBar();
        initView();
        initDatas();
        initEvents();


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
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.id_gridView);
        mBottomLayout = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mDirName = (TextView) findViewById(R.id.id_dir_name);
        mDirCount = (TextView) findViewById(R.id.id_dir_count);
    }

    private class ImageAdapter extends BaseAdapter {
        private List<String> mImgPaths;
        private String mDirPath;
        private LayoutInflater mInflater;

        public ImageAdapter(Context context, List<String> mDatas, String dirPath) {
            this.mImgPaths = mDatas;
            this.mDirPath = dirPath;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mImgPaths.size();
        }

        @Override
        public Object getItem(int position) {
            return mImgPaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.imp_gridview_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
                viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //重置状态
            viewHolder.mImg.setImageResource(R.drawable.pictures_no);
            viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);

            LocalImageLoader.getInstance(3, LocalImageLoader.Type.LIFO).loadImage(mDirPath + "/" + mImgPaths.get(position), viewHolder.mImg);


            return convertView;
        }

        private class ViewHolder {
            private ImageView mImg;
            private ImageButton mSelect;
        }
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
                    Log.d(TAG, "dir path: " + dirPath);
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
                            return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg");
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

    private void initEvents() {
    }
}
