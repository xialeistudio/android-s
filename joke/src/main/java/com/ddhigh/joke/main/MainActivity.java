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
import android.widget.Toast;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    MyApplication application;

    BroadcastReceiver loginSuccessReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        application = (MyApplication) getApplication();
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
                    Log.d(MyApplication.TAG, "user center clicked");
                }
                return true;
            case R.id.menuAdd:
                Log.d(MyApplication.TAG, "post joke clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(loginSuccessReceiver);
        super.onDestroy();
    }
}
