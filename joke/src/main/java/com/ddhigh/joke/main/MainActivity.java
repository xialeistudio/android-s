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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.joke.item.PostActivity;
import com.ddhigh.joke.item.ViewActivity;
import com.ddhigh.joke.model.JokeModel;
import com.ddhigh.joke.user.UserActivity;
import com.ddhigh.joke.util.HttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements PullToRefreshBase.OnRefreshListener2 {
    MyApplication application;

    BroadcastReceiver loginSuccessReceiver;
    BroadcastReceiver logoutReceiver;
    BroadcastReceiver newJokeReceiver;
    @ViewInject(R.id.listJoke)
    PullToRefreshListView listJoke;
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
                        application.user.setToken(jsonObject.getString("_id"));
                        application.user.save(getApplicationContext());
                        HttpUtil.setToken(jsonObject.getString("_id"));
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
        //注册新段子广播

        newJokeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                doRefresh();
                Log.d(MyApplication.TAG, "doRefresh");
            }
        };
        registerReceiver(newJokeReceiver, new IntentFilter(Actions.ACTION_NEW_JOKE));
        //加载数据
        listJoke.setOnRefreshListener(this);
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
        unregisterReceiver(newJokeReceiver);
        super.onDestroy();
    }


    int currentPage = 1;

    /**
     * 加载最新
     */
    private void doRefresh() {
        Log.d(MyApplication.TAG, "refresh start");
        RequestParams query = new RequestParams();
        query.put("expand", "user");
        HttpUtil.get("/jokes", query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //获取分页大小
                Log.d(MyApplication.TAG, "refresh complete ===> " + response.toString());
                try {
                    jokes.clear();
                    HttpUtil.handleError(response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);
                        JokeModel joke = new JokeModel();
                        joke.parse(item);
                        jokes.add(joke);
                    }
                    jokeAdapter.notifyDataSetChanged();
                    listJoke.onRefreshComplete();
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
    }

    /**
     * 分页加载
     */
    private void doLoadMore() {

    }

    @Event(value = R.id.listJoke, type = AdapterView.OnItemClickListener.class)
    private void jokeClicked(AdapterView<?> parent, View view, int position, long id) {
        JokeModel joke = jokes.get(position);
        Intent i = new Intent(this, ViewActivity.class);
        i.putExtra("id", joke.getId());
        startActivity(i);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        doRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        Log.d(MyApplication.TAG, "pull up");
    }
}
