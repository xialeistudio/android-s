package com.ddhigh.joke.item;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.model.JokeModel;
import com.ddhigh.joke.util.HttpUtil;
import com.ddhigh.joke.widget.ImageViewActivity;
import com.ddhigh.mylibrary.widget.NoScrollGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

@ContentView(R.layout.activity_item_view)
public class ViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    TextView txtNickname;
    @ViewInject(R.id.txtContent)
    TextView txtContent;
    @ViewInject(R.id.gridImages)
    NoScrollGridView gridImages;
    @ViewInject(R.id.txtPraiseCount)
    TextView txtPraiseCount;
    @ViewInject(R.id.txtUnPraiseCount)
    TextView txtUnPraiseCount;
    @ViewInject(R.id.txtCommentCount)
    TextView txtCommentCount;

    List<String> images;
    ImageAdapter adapter;
    JSONObject item;
    JokeModel jokeModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        jokeModel  = new JokeModel();
        //加载远程数据
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中");
        HttpUtil.get("/jokes/" + id + "?expand=user", null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    HttpUtil.handleError(response.toString());
                    jokeModel.parse(response);
                    txtNickname.setText(jokeModel.getUser().getNickname());
                    DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .displayer(new CircleBitmapDisplayer())
                            .build();
                    ImageLoader.getInstance().displayImage(jokeModel.getUser().getAvatar() + "?imageView2/1/w/96", imageAvatar, displayImageOptions);
                    txtContent.setText(jokeModel.getText());
                    item = response;

                    images = jokeModel.getImages();
                    adapter = new ImageAdapter(ViewActivity.this, images);
                    gridImages.setAdapter(adapter);
                    txtPraiseCount.setText(String.valueOf(jokeModel.getPraiseCount()));
                    txtUnPraiseCount.setText(String.valueOf(jokeModel.getUnpraiseCount()));
                    txtCommentCount.setText(String.valueOf(jokeModel.getCommentCount()));
                } catch (JSONException | JokeException | ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(ViewActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(ViewActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });

        gridImages.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuShare:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                Log.d(MyApplication.TAG, "share: " + item.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = images.get(position);
        Log.d(MyApplication.TAG, "xxx===>" + url);
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putExtra("path", url);
        startActivity(intent);
    }

    @Event(R.id.txtPraiseCount)
    private void onPraised(View view) {
        JSONObject json = new JSONObject();
        try {
            json.put("praiseCount", jokeModel.getPraiseCount() + 1);
            HttpUtil.put(this, "/jokes/" + jokeModel.getId(), json, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        HttpUtil.handleError(response.toString());
                        JokeModel _joke = new JokeModel();
                        _joke.parse(response);
                        jokeModel.setPraiseCount(_joke.getPraiseCount());
                        txtPraiseCount.setText(String.valueOf(jokeModel.getPraiseCount()));
                    } catch (JSONException | JokeException | ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(ViewActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(ViewActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
        }
    }
    @Event(R.id.txtUnPraiseCount)
    private void onUnPraised(View view) {
        JSONObject json = new JSONObject();
        try {
            json.put("unpraiseCount", jokeModel.getUnpraiseCount() + 1);
            HttpUtil.put(this, "/jokes/" + jokeModel.getId(), json, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        HttpUtil.handleError(response.toString());
                        JokeModel _joke = new JokeModel();
                        _joke.parse(response);
                        jokeModel.setUnpraiseCount(_joke.getUnpraiseCount());
                        txtUnPraiseCount.setText(String.valueOf(jokeModel.getUnpraiseCount()));
                    } catch (JSONException | JokeException | ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(ViewActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(ViewActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
        }
    }
}
