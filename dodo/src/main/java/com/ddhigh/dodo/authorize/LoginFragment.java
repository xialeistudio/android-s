package com.ddhigh.dodo.authorize;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import com.ddhigh.dodo.main.MainActivity;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.HttpUtil;
import com.ddhigh.dodo.widget.IosAlertDialog;
import com.ddhigh.dodo.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.authorize
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
public class LoginFragment extends Fragment {
    @ViewInject(R.id.btnLogin)
    private Button btnLogin;
    @ViewInject(R.id.btnGoRegister)
    private Button btnGoRegister;
    @ViewInject(R.id.txtUsername)
    EditText txtUsername;
    @ViewInject(R.id.txtPassword)
    EditText txtPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        x.view().inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.login);
    }

    @Event(R.id.btnLogin)
    private void onBtnLoginClicked(final View view) {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

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
        } else {

            final LoadingDialog dialog = new LoadingDialog(getActivity());
            dialog.setTitle("登录中");
            dialog.setCancelable(false);
            //发送数据
            dialog.show();
            final User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.login(new Callback.CommonCallback<JSONObject>() {
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
                        try {
                            //处理登录成功
                            String userId = result.getString("userId");
                            String token = result.getString("id");
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.loginSuccess(userId, token);
                        } catch (JSONException e) {
                            onError(e, true);
                        }
                    }
                }

                private void handleError(String msg) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    handleError("账号或密码错误或邮箱未验证");
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

    @Event(R.id.btnGoRegister)
    private void onBtnGoRegister(View view) {
        RegisterFragment registerFragment;
        FragmentManager fragmentManager = getFragmentManager();
        registerFragment = (RegisterFragment) fragmentManager.findFragmentByTag("registerFragment");
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.fragmentContainer, registerFragment, "registerFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
