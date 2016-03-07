package com.ddhigh.joke.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.joke.item.PostActivity;
import com.ddhigh.joke.model.JokeModel;
import com.ddhigh.joke.user.UserActivity;
import com.ddhigh.joke.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    MyApplication application;

    BroadcastReceiver loginSuccessReceiver;
    BroadcastReceiver logoutReceiver;

    @ViewInject(R.id.listJoke)
    ListView listJoke;
    List<JokeModel> jokes;
    JokeAdapter jokeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        //列表处理
        jokes = new ArrayList<>();
        jokeAdapter = new JokeAdapter(this, jokes);
        listJoke.setAdapter(jokeAdapter);
        listJoke.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));

        application = (MyApplication) getApplication();
        //注册登录成功广播
        loginSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                if (data != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        application.user.setId(jsonObject.getString("userId"));
                        application.user.setToken(jsonObject.getString("id"));
                        application.user.save(getApplicationContext());
                        HttpUtil.setToken(jsonObject.getString("id"));
                        //显示发表段子的加号
                        supportInvalidateOptionsMenu();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "参数解析失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        registerReceiver(loginSuccessReceiver, new IntentFilter(Actions.ACTION_LOGIN_SUCCESS));
        //注册退出登录广播
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HttpUtil.setToken(null);
                application.user.clean(getApplicationContext());
                //更新菜单
                supportInvalidateOptionsMenu();
            }
        };
        registerReceiver(logoutReceiver, new IntentFilter(Actions.ACTION_LOGOUT));
        //加载数据
        doRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_person, menu);
        if (!application.user.isGuest()) {
            inflater.inflate(R.menu.menu_add, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPerson:
                //检测是否是游客
                if (application.user.isGuest()) {
                    Intent intent = new Intent();
                    intent.setAction(Actions.ACTION_LOGIN_REQUIRED);
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(this, UserActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menuAdd:
                Intent intent = new Intent(this, PostActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(loginSuccessReceiver);
        unregisterReceiver(logoutReceiver);
        super.onDestroy();
    }


    final int pageSize = 10;

    /**
     * 加载最新
     */
    private void doRefresh() {
        Log.d(MyApplication.TAG, "refresh start");
        JSONObject query = new JSONObject();
        try {
            query.put("order", "createdAt DESC");
            query.put("skip", jokes.size());
            query.put("limit", pageSize);
            HttpUtil.get(this, "/joke", query, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(MyApplication.TAG, "refresh complete ===> " + response.toString());
                    try {
                        HttpUtil.handleError(response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            JokeModel joke = new JokeModel();
                            joke.parse(item);
                            jokes.add(joke);
                        }
                        jokeAdapter.notifyDataSetChanged();
                    } catch (JSONException | JokeException | ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "解析服务器响应失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(MainActivity.this, "服务器响应错误", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "服务器响应错误", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分页加载
     */
    private void doLoadMore() {

    }
}
