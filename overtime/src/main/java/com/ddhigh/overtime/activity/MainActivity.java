package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddhigh.overtime.MyApplication;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    MyApplication application;
    DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        application = (MyApplication) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:弹出新增请求
            }
        });
        checkLogin();
        init();
        db = x.getDb(application.getDaoConfig());
    }

    private void init() {
        Intent intent = getIntent();
        if ((intent.hasExtra("isLogin") && intent.getBooleanExtra("isLogin", true)) ||
                !application.getAccessToken().isGuest()) {
            Log.i("main", "ready for init");
            //如果本地没有用户数据
            if (application.getUser().getRealname() != null) {
                Log.i("user", "local user from local: " + application.getUser().toString());
            } else {
                HttpUtil.get("/user/view", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("user", "load user from remote fail", throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //同步本地
                        try {
                            application.getUser().decode(response);
                            db.saveOrUpdate(application.getUser());
                            Log.i("user", "save user success: " + application.getUser().toString());
                        } catch (JSONException | IllegalAccessException | DbException e) {
                            Log.e("user", "save user fail", e);
                        }
                    }
                });
            }
        }
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
}
