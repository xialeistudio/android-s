package com.ddhigh.joke.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.item.ImageAdapter;
import com.ddhigh.joke.item.ViewActivity;
import com.ddhigh.joke.model.JokeModel;
import com.ddhigh.mylibrary.widget.NoScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.List;

/**
 * @project android-s
 * @package com.ddhigh.joke.main
 * @user xialeistudio
 * @date 2016/3/7 0007
 */
public class JokeAdapter extends BaseAdapter {
    Context mContent;
    List<JokeModel> jokes;

    public JokeAdapter(Context mContent, List<JokeModel> jokes) {
        this.mContent = mContent;
        this.jokes = jokes;
    }

    @Override
    public int getCount() {
        return jokes.size();
    }

    @Override
    public Object getItem(int position) {
        return jokes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContent);
            convertView = inflater.inflate(R.layout.list_item_joke, null);
            viewHolder = new ViewHolder();

            viewHolder.imageAvatar = (ImageView) convertView.findViewById(R.id.imageAvatar);
            viewHolder.txtNickname = (TextView) convertView.findViewById(R.id.txtNickname);
            viewHolder.txtText = (TextView) convertView.findViewById(R.id.txtText);
            viewHolder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridImages);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final JokeModel jokeModel = (JokeModel) getItem(position);

        viewHolder.gridView.setVisibility(View.GONE);
        //图片处理
        if (jokeModel.getImages().size() > 0) {
            viewHolder.gridView.setVisibility(View.VISIBLE);
            ImageAdapter imageAdapter = new ImageAdapter(mContent, jokeModel.getImages());
            viewHolder.gridView.setAdapter(imageAdapter);
        }
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new CircleBitmapDisplayer())
                .build();
        ImageLoader.getInstance().displayImage(jokeModel.getUser().getAvatar() + "?imageView2/1/w/64", viewHolder.imageAvatar, imageOptions);
        viewHolder.txtNickname.setText(jokeModel.getUser().getNickname());
        viewHolder.txtText.setText(jokeModel.getText());
        return convertView;
    }

    private static class ViewHolder{
        ImageView imageAvatar;
        TextView  txtNickname;
        TextView txtText;
        NoScrollGridView gridView;
    }
}
