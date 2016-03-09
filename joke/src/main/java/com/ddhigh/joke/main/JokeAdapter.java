package com.ddhigh.joke.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.item.ImageAdapter;
import com.ddhigh.joke.item.ViewActivity;
import com.ddhigh.joke.model.JokeModel;
import com.ddhigh.joke.util.HttpUtil;
import com.ddhigh.mylibrary.widget.NoScrollGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
            viewHolder.gridView.setClickable(false);
            viewHolder.gridView.setPressed(false);
            viewHolder.gridView.setEnabled(false);
            viewHolder.txtPraiseCount = (TextView) convertView.findViewById(R.id.txtPraiseCount);
            viewHolder.txtUnPraiseCount = (TextView) convertView.findViewById(R.id.txtUnPraiseCount);
            viewHolder.txtCommentCount = (TextView) convertView.findViewById(R.id.txtCommentCount);
            convertView.setTag(viewHolder);
        } else {
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
        viewHolder.txtPraiseCount.setText(String.valueOf(jokeModel.getPraiseCount()));
        viewHolder.txtUnPraiseCount.setText(String.valueOf(jokeModel.getUnpraiseCount()));
        viewHolder.txtCommentCount.setText(String.valueOf(jokeModel.getCommentCount()));
        //事件监听
        viewHolder.txtPraiseCount.setOnClickListener(new OnClickListener(position));
        viewHolder.txtUnPraiseCount.setOnClickListener(new OnClickListener(position));
        viewHolder.txtCommentCount.setOnClickListener(new OnClickListener(position));
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageAvatar;
        TextView txtNickname;
        TextView txtText;
        NoScrollGridView gridView;
        TextView txtPraiseCount;
        TextView txtUnPraiseCount;
        TextView txtCommentCount;
    }

    private class OnClickListener implements View.OnClickListener {
        private int position;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtPraiseCount:
                    praise(position);
                    break;
                case R.id.txtUnPraiseCount:
                    unpraise(position);
                    break;
                case R.id.txtCommentCount:
                    comment(position);
                    break;
            }
        }

        private void comment(int position) {
            //跳转到详情
            Log.d(MyApplication.TAG, "comment clicked: " + position + "/" + jokes.size());
            Intent i = new Intent(mContent, ViewActivity.class);
            JokeModel j = jokes.get(position);
            i.putExtra("id", j.getId());
            mContent.startActivity(i);
        }

        private void unpraise(int position) {
            final JokeModel joke = jokes.get(position);
            JSONObject json = new JSONObject();
            try {
                json.put("unpraiseCount", joke.getUnpraiseCount() + 1);
                HttpUtil.put(mContent, "/jokes/" + joke.getId(), json, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            HttpUtil.handleError(response.toString());
                            JokeModel _joke = new JokeModel();
                            _joke.parse(response);
                            joke.setUnpraiseCount(_joke.getUnpraiseCount());
                            notifyDataSetChanged();
                        } catch (JSONException | JokeException | ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(mContent, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        throwable.printStackTrace();
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(mContent, "操作失败", Toast.LENGTH_SHORT).show();
            }
        }

        private void praise(int position) {
            final JokeModel joke = jokes.get(position);
            JSONObject json = new JSONObject();
            try {
                json.put("praiseCount", joke.getPraiseCount() + 1);
                HttpUtil.put(mContent, "/jokes/" + joke.getId(), json, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            HttpUtil.handleError(response.toString());
                            JokeModel _joke = new JokeModel();
                            _joke.parse(response);
                            joke.setPraiseCount(_joke.getPraiseCount());
                            notifyDataSetChanged();
                        } catch (JSONException | JokeException | ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(mContent, "点赞失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        throwable.printStackTrace();
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(mContent, "点赞失败", Toast.LENGTH_SHORT).show();
            }
        }

        public OnClickListener(int position) {
            this.position = position;
        }
    }
}
