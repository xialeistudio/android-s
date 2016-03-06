package com.ddhigh.joke.item;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ddhigh.joke.R;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    Context mContext;
    List<String> list;

    public ImageAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.griditem_image, null);
        }
        ImageView imageThumbnail = (ImageView) convertView.findViewById(R.id.imageThumbnail);
        imageThumbnail.setImageBitmap(BitmapFactory.decodeFile((String) getItem(position)));
        return convertView;
    }
}
