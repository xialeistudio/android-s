package com.ddhigh.dodo.authorize;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.dodo.R;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.RegexUtil;
import com.ddhigh.dodo.widget.IosAlertDialog;
import com.ddhigh.dodo.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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
        fragmentTransaction.replace(R.id.fragmentContainer, loginFragment, "loginFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

            user.register(new Callback.CommonCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (result.has("error")) {
                        try {
                            JSONObject error = result.getJSONObject("error");
                            handleError(error.getString("message"));
                        } catch (JSONException e) {
                            onError(e, true);
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

                private void handleError(String msg) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    handleError("注册失败");
                    ex.printStackTrace();
                }


                @Override
                public void onCancelled(CancelledException cex) {
                    dialog.dismiss();
                }

                @Override
                public void onFinished() {
                    dialog.dismiss();
                }
            });
        }
    }
}
