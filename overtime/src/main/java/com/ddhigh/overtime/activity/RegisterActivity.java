package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.mylibrary.dialog.LoadingDialog;
import com.ddhigh.mylibrary.util.RegexUtil;
import com.ddhigh.overtime.MyApplication;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    @ViewInject(R.id.txtUsername)
    EditText txtUsername;
    @ViewInject(R.id.txtPassword)
    EditText txtPassword;
    @ViewInject(R.id.txtConfirmPassword)
    EditText txtConfirmPassword;
    @ViewInject(R.id.txtRealname)
    EditText txtRealname;
    @ViewInject(R.id.txtPhone)
    EditText txtPhone;
    @ViewInject(R.id.checkRealInfo)
    CheckBox checkRealInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    @Event(R.id.btnRegister)
    private void onRegister(View view) {
        final String username = txtUsername.getText().toString().trim(),
                password = txtPassword.getText().toString().trim(),
                confirmPassword = txtConfirmPassword.getText().toString().trim(),
                realname = txtRealname.getText().toString().trim(),
                phone = txtPhone.getText().toString().trim();
        boolean isRealInfoChecked = checkRealInfo.isChecked();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "用户名"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "密码"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "确认密码"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.confirm_password_incorrect, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(realname)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "姓名"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "手机号码"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!RegexUtil.isMobile(phone)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.format_error), "手机号码"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isRealInfoChecked) {
            Toast.makeText(this, "请确认使用真实姓名注册", Toast.LENGTH_SHORT).show();
            return;
        }

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.registering));
        final User user = new User();
        user.setRealname(realname);
        user.setPhone(phone);
        user.register(username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user.decode(response);
                    //写入数据库
                    DbManager dbManager = x.getDb(((MyApplication) getApplication()).getDaoConfig());
                    try {
                        dbManager.save(user);
                        Log.i("user", "add to local database: " + user.getUser_id());
                    } catch (DbException e) {
                        Log.e("user", "add to local database error", e);
                    }
                    Intent data = new Intent();
                    data.putExtra(User.PREF_USERNAME, username);
                    data.putExtra(User.PREF_PASSWORD, password);
                    setResult(RESULT_OK, data);
                    finish();
                    Log.i(User.PREF_USER, "send user to loginActivity");
                } catch (JSONException | IllegalAccessException e) {
                    onFailure(statusCode, headers, e, response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable instanceof HttpHostConnectException) {
                    Toast.makeText(RegisterActivity.this, R.string.connection_server_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinish() {
                dialog.hide();
            }
        });
    }
}
