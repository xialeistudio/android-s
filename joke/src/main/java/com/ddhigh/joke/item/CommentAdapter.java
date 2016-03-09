package com.ddhigh.joke.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.joke.R;
import com.ddhigh.joke.model.CommentModel;
import com.ddhigh.mylibrary.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.List;

/**
 * @project android-s
 * @package com.ddhigh.joke.item
 * @user xialeistudio
 * @date 2016/3/9 0009
 */
public class CommentAdapter extends BaseAdapter {
    Context mContent;
    List<CommentModel> list;

    public CommentAdapter(Context mContent, List<CommentModel> list) {
        this.mContent = mContent;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContent).inflate(R.layout.widget_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.imageAvatar = (ImageView) convertView.findViewById(R.id.imageAvatar);
            viewHolder.txtNickname = (TextView) convertView.findViewById(R.id.txtNickname);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CommentModel commentModel = list.get(position);

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new CircleBitmapDisplayer())
                .build();
        ImageLoader.getInstance().displayImage(commentModel.getUser().getAvatar() + "?imageView2/1/w/128", viewHolder.imageAvatar, imageOptions);

        viewHolder.txtNickname.setText(commentModel.getUser().getNickname());
        viewHolder.txtTime.setText(DateUtil.format(commentModel.getCreatedAt(), "MM-dd HH:mm"));
        viewHolder.txtContent.setText(commentModel.getText());
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageAvatar;
        TextView txtNickname;
        TextView txtTime;
        TextView txtContent;
    }
}
