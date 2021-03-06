package com.ddhigh.mylibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ddhigh.mylibrary.R;
import com.ddhigh.mylibrary.util.LocalImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {

    public interface onCameraClickedListener {
        void onCameraClicked();
    }

    private int maxSelectedImage = 9;
    private Set<String> mSelectedImg = new HashSet<>();
    private List<String> mImgPaths;
    private String mDirPath;
    private LayoutInflater mInflater;
    private onCameraClickedListener onCameraClickedListener;


    public void setMaxSelectedImage(int maxSelectedImage) {
        this.maxSelectedImage = maxSelectedImage;
    }

    public ImageAdapter(Context context, List<String> mDatas, String dirPath, onCameraClickedListener onCameraClickedListener) {
        this.mImgPaths = mDatas;
        this.mDirPath = dirPath;
        mInflater = LayoutInflater.from(context);
        this.onCameraClickedListener = onCameraClickedListener;
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

    public Set<String> getSelectedImg() {
        return mSelectedImg;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //检查当前是否为camera
        final String currentImg = mImgPaths.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.imp_gridview_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
            viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String filepath = mDirPath + "/" + mImgPaths.get(position);
        if (currentImg.equals("camera")) {
            viewHolder.mImg.setImageResource(R.drawable.ic_photo_camera_white_48dp);
            convertView.setBackgroundColor(Color.parseColor("#333333"));
            viewHolder.mImg.setScaleType(ImageView.ScaleType.CENTER);
            viewHolder.mSelect.setVisibility(View.INVISIBLE);
            viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCameraClickedListener.onCameraClicked();
                }
            });
        } else {
            //重置状态
            viewHolder.mSelect.setVisibility(View.VISIBLE);
            viewHolder.mImg.setImageResource(R.drawable.pictures_no);
            viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
            LocalImageLoader.getInstance(3, LocalImageLoader.Type.LIFO).loadImage(mDirPath + "/" + mImgPaths.get(position), viewHolder.mImg);
            viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //已被选择
                    if (mSelectedImg.contains(filepath)) {
                        mSelectedImg.remove(filepath);
                        viewHolder.mImg.setColorFilter(null);
                        viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
                    } else {
                        if (mSelectedImg.size() < maxSelectedImage) {
                            //未选择
                            mSelectedImg.add(filepath);
                            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                            viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
                        }
                    }
                }
            });
        }
        if (mSelectedImg.contains(filepath)) {
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView mImg;
        private ImageButton mSelect;
    }
}