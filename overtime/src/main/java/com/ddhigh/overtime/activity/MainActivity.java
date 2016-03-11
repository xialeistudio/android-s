package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ddhigh.overtime.R;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    @ViewInject(R.id.listOvertime)
    PullToRefreshListView listView;
    List<Overtime> overtimes;
    OvertimeAdapter overtimeAdapter;
    RequestParams requestParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OvertimeCreateActivity.class);
                startActivity(intent);
            }
        });
        checkLogin();
        requestParams = new RequestParams();
        init();
        overtimes = new ArrayList<>();
        listView.setOnRefreshListener(this);
        overtimeAdapter = new OvertimeAdapter(this, overtimes);
        listView.setAdapter(overtimeAdapter);
        loadFromLocal();
    }

    //本地加载数据
    private void loadFromLocal() {
        try {
            List<Overtime> newData = dbManager.selector(Overtime.class).orderBy("id", true).limit(pageSize).findAll();
            overtimes.addAll(newData);
            if (overtimes.size() >= pageSize) {
                listView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            overtimeAdapter.notifyDataSetChanged();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Intent intent = getIntent();
        if ((intent.hasExtra("isLogin") && intent.getBooleanExtra("isLogin", true)) ||
                !application.getAccessToken().isGuest()) {
            //如果本地没有用户数据
            if (application.getUser().getRealname() == null) {
                HttpUtil.get("/user/view", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (statusCode == 401) {
                            User.loginRequired(MainActivity.this, false);
                            finish();
                            return;
                        }
                        Log.e("user", "load user from remote fail", throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //同步本地
                        try {
                            application.getUser().decode(response);
                            dbManager.saveOrUpdate(application.getUser());
                            Log.i("user", "save user success: " + application.getUser().toString());
                        } catch (JSONException | IllegalAccessException | DbException e) {
                            Log.e("user", "save user fail", e);
                        }
                    }
                });
            }
        }
    }

    int currentPage = 1;
    int pageSize = 10;

    /**
     * 加载最新数据
     */
    private void loadOnRefresh() {
        requestParams.put("id", application.getUser().getUser_id());
        requestParams.put("page", 1);
        requestParams.put("size", pageSize);
        HttpUtil.get("/overtime/list", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response.length() > 0)
                    overtimes.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Overtime overtime = new Overtime();
                        overtime.decode(object);
                        dbManager.saveOrUpdate(overtime);
                        overtimes.add(overtime);
                    } catch (JSONException | IllegalAccessException e) {
                        Log.e("overtime-list", e.getMessage(), e);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        e.printStackTrace();
                        Log.e("overtime-list", "save to db fail: " + e.getMessage(), e);
                    }
                }
                if (response.length() > 0) {
                    overtimeAdapter.notifyDataSetChanged();
                    Log.d("overtime-list", "notifyDataSetChanged");
                }
                currentPage = 1;
                if (response.length() < pageSize) {
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }


            @Override
            public void onFinish() {
                listView.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(MainActivity.this, false);
                    finish();
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    Log.e("overtime-list", e.getMessage(), e);
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadOnLoadMore() {
        requestParams.put("page", ++currentPage);
        requestParams.put("size", pageSize);
        HttpUtil.get("/overtime/list", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Overtime overtime = new Overtime();
                        overtime.decode(object);
                        dbManager.saveOrUpdate(overtime);
                        overtimes.add(overtime);
                    } catch (JSONException | IllegalAccessException e) {
                        Log.e("overtime-list", e.getMessage(), e);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        e.printStackTrace();
                        Log.e("overtime-list", "save to db fail: " + e.getMessage(), e);
                    }
                }
                if (response.length() > 0)
                    overtimeAdapter.notifyDataSetChanged();
                if (response.length() < pageSize) {
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                }
            }

            @Override
            public void onFinish() {
                listView.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(MainActivity.this, false);
                    finish();
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    Log.e("overtime-list", e.getMessage(), e);
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkLogin() {
        if (application.getAccessToken().isGuest()) {
            User.loginRequired(this, false);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadOnRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadOnLoadMore();
    }
}
