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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.ddhigh.joke.util.HttpUtil;
import com.ddhigh.mylibrary.util.RegexUtil;
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
@ContentView(R.layout.activity_auth_register)
public class RegisterActivity extends AppCompatActivity {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @ViewInject(R.id.txtUsername)
    EditText txtUsername;
    @ViewInject(R.id.txtPassword)
    EditText txtPassword;
    @ViewInject(R.id.txtPassword2)
    EditText txtPassword2;
    @ViewInject(R.id.txtEmail)
    EditText txtEmail;

    @Event(R.id.btnRegister)
    private void onBtnRegisterClicked(View view) {

        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String password2 = txtPassword2.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        boolean isValidated = true;
        if (TextUtils.isEmpty(username)) {
            builder.setMessage("请输入用户名");
            isValidated = false;
        } else if (TextUtils.isEmpty(password)) {
            builder.setMessage("请输入密码");
            isValidated = false;
        } else if (!password2.equals(password)) {
            builder.setMessage("确认密码不一致");
            isValidated = false;
        } else if (TextUtils.isEmpty(email)) {
            builder.setMessage("请输入邮箱");
            isValidated = false;
        } else if (!RegexUtil.isEmail(email)) {
            builder.setMessage("邮箱格式错误");
            isValidated = false;
        }

        if (!isValidated) {
            //显示错误消息
            builder.create().show();
            return;
        }
        //注册
        Log.d(MyApplication.TAG, "注册");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", email);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("注册中");
            progressDialog.setCancelable(false);

            HttpUtil.post(this, "/user", jsonObject, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        HttpUtil.handleError(response.toString());
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JokeException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                }
            });
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "请勿请输入特殊字符", Toast.LENGTH_SHORT).show();
        }
    }
}
