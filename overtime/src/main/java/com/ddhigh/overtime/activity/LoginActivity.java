package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.mylibrary.dialog.LoadingDialog;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.txtUsername)
    EditText txtUsername;
    @ViewInject(R.id.txtPassword)
    EditText txtPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
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
                    login(username, password);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Event(R.id.btnLogin)
    private void onLogin(View view) {
        String username = txtUsername.getText().toString().trim(),
                password = txtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "用户名"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "密码"), Toast.LENGTH_SHORT).show();
            return;
        }
        login(username, password);
    }

    private void login(String username, String password) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.logining));
        final User user = new User();
        user.login(username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    application.getAccessToken().decode(response);
                    Log.i("user", "login success: " + application.getAccessToken().toString());
                    application.getUser().setUser_id(application.getAccessToken().getUser_id());
                    application.getUser().saveUserId(getApplicationContext());
                    dbManager.save(application.getAccessToken());
                    Log.i("user", "save accessToken success");
                    HttpUtil.setToken(application.getAccessToken().getToken());
                    Intent launchIntent = getIntent();
                    if (launchIntent.hasExtra("isCallback")) {
                        setResult(RESULT_OK);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("isLogin", true);
                        startActivity(intent);
                    }
                    finish();
                } catch (JSONException | IllegalAccessException e) {
                    onFailure(statusCode, headers, e, response);
                } catch (DbException e) {
                    Log.e("user", "save accessToken fail", e);
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable instanceof HttpHostConnectException) {
                    Toast.makeText(LoginActivity.this, R.string.connection_server_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    long lastBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastBackTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                lastBackTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            //结束程序
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
