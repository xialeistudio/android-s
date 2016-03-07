package com.ddhigh.joke.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.joke.R;
import com.ddhigh.joke.model.JokeModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
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
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContent);
            convertView = inflater.inflate(R.layout.list_item_joke, null);
        }

        ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.imageAvatar);
        TextView txtNickname = (TextView) convertView.findViewById(R.id.txtNickname);
        TextView txtText = (TextView) convertView.findViewById(R.id.txtText);
        GridView gridImage = (GridView) convertView.findViewById(R.id.gridImages);

        JokeModel jokeModel = (JokeModel) getItem(position);
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .displayer(new CircleBitmapDisplayer())
                .build();
        ImageLoader.getInstance().displayImage(jokeModel.getUser().getAvatar() + "?imageView2/1/w/64", imageAvatar, imageOptions);
        txtNickname.setText(jokeModel.getUser().getAvatar());
        txtText.setText(jokeModel.getText());
        //TODO:列表图片
        return convertView;
    }
}
