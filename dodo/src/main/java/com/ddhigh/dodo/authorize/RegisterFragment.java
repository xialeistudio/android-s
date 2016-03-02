package com.ddhigh.dodo.authorize;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.RegexUtil;
import com.ddhigh.dodo.widget.IosAlertDialog;
import com.ddhigh.dodo.widget.LoadingDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;


/**
 * @project Study
 * @package com.ddhigh.dodo.authorize
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
public class RegisterFragment extends Fragment {
    @ViewInject(R.id.btnRegister)
    Button btnRegister;
    @ViewInject(R.id.btnGoLogin)
    Button btnGoLogin;
    @ViewInject(R.id.txtUsername)
    EditText txtUsername;
    @ViewInject(R.id.txtPassword)
    EditText txtPassword;
    @ViewInject(R.id.txtPassword2)
    EditText txtPassword2;
    @ViewInject(R.id.txtEmail)
    EditText txtEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        x.view().inject(this, v);
        return v;
    }

    @Event(R.id.btnGoLogin)
    private void onBtnGoLogin(View view) {
        LoginFragment loginFragment;
        FragmentManager fragmentManager = getFragmentManager();
        loginFragment = (LoginFragment) fragmentManager.findFragmentByTag("loginFragment");
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.fragmentContainer, loginFragment, "loginFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.register);
    }

    @Event(R.id.btnRegister)
    private void onBtnRegister(View view) {
        final String username, password, password2, email;
        username = txtUsername.getText().toString();
        password = txtPassword.getText().toString();
        password2 = txtPassword2.getText().toString();
        email = txtEmail.getText().toString();

        if (TextUtils.isEmpty(username)) {
            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("请输入账号")
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } else if (TextUtils.isEmpty(password)) {
            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("请输入密码")
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } else if (TextUtils.isEmpty(password2)) {
            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("确认密码不一致")
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } else if (TextUtils.isEmpty(email)) {
            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("请输入邮箱")
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } else if (TextUtils.isEmpty(email)) {
            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("请输入邮箱")
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } else if (!RegexUtil.isEmail(email)) {
            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("邮箱格式错误")
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } else {
            //注册
            final LoadingDialog dialog = new LoadingDialog(getActivity());
            dialog.setTitle("注册中");
            dialog.setCancelable(false);
            //发送数据
            dialog.show();
            final User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);


            try {
                user.register(getActivity(),new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (response.has("error")) {
                            Log.e(MyApplication.TAG, "register: " + response.toString());
                            try {
                                JSONObject error = response.getJSONObject("error");
                                String msg = error.getString("message");
                                if (msg.equals("username:already exists")) {
                                    Toast.makeText(getActivity(), "用户名已存在", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "注册失败: " + msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            IosAlertDialog dialog = new IosAlertDialog.Builder(getActivity())
                                    .setTitle("提示")
                                    .setMessage("注册成功，请验证邮箱后登录")
                                    .setButton("返回登录", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            onBtnGoLogin(btnGoLogin);
                                        }
                                    })
                                    .create();
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        throwable.printStackTrace();
                        Log.e(MyApplication.TAG, "register: " + errorResponse.toString());
                        Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }
                });
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
