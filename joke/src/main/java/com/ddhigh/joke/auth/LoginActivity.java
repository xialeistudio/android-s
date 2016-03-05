package com.ddhigh.joke.auth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.R;
import com.ddhigh.joke.config.Actions;
import com.ddhigh.joke.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * @project Study
 * @package com.ddhigh.joke.auth
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
@ContentView(R.layout.activity_auth_login)
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_person_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuPersonAdd:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @ViewInject(R.id.txtUsername)
    EditText txtUsername;
    @ViewInject(R.id.txtPassword)
    EditText txtPassword;

    @Event(R.id.btnLogin)
    private void onBntLoginClicked(View view) {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        boolean isValidated = true;
        if (TextUtils.isEmpty(username)) {
            isValidated = false;
            builder.setMessage("请输入用户名");
        } else if (TextUtils.isEmpty(password)) {
            isValidated = false;
            builder.setMessage("请输入密码");
        }

        if (!isValidated) {
            builder.create().show();
            return;
        }
        //登录
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("登录中");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            HttpUtil.post(this, "/user/login", jsonObject, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        HttpUtil.handleError(response.toString());
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        //发送广播
                        Intent intent = new Intent();
                        intent.setAction(Actions.ACTION_LOGIN_SUCCESS);
                        intent.putExtra("data", response.toString());
                        sendBroadcast(intent);
                        finish();
                    } catch (JokeException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (statusCode == 401) {
                        try {
                            HttpUtil.handleError(errorResponse.toString());
                        } catch (JSONException | JokeException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    throwable.printStackTrace();
                    Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "请勿请输入特殊字符", Toast.LENGTH_SHORT).show();
        }
    }
}
