package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ddhigh.overtime.MyApplication;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.model.User;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {
    DbManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        dbManager = x.getDb(((MyApplication) getApplication()).getDaoConfig());

        testDb();
    }

    private void testDb() {
        try {
            User user = dbManager.selector(User.class).where("user_id", "=", 14).findFirst();
            Log.i("db", user.toString());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuRegister) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, RequestCode.REGISTER);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.REGISTER:
                if (resultCode == RESULT_OK) {
                    String username = data.getStringExtra(User.PREF_USERNAME);
                    String password = data.getStringExtra(User.PREF_PASSWORD);
                    int userId = data.getIntExtra(User.PREF_USER_ID, 0);
                    User user = null;
                    try {
                        user = dbManager.selector(User.class).where("user_id", "=", userId).findFirst();
                        //TODO:登录
                        Log.i("user", "login ===> " + username + ":" + password + ":" + userId + "\n" + user);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
