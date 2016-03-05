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

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;

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
                Log.d(MyApplication.TAG, "login success");
            }
        };
        registerReceiver(loginSuccessReceiver, new IntentFilter(Actions.ACTION_LOGIN_SUCCESS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_person, menu);
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
                    Log.d(MyApplication.TAG, "user logined");
                }
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
