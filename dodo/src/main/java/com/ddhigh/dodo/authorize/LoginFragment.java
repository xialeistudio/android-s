package com.ddhigh.dodo.authorize;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
            try{
                user.login(getActivity(),new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String userId = null;
                        try {
                            userId = response.getString("userId");
                            String token = response.getString("id");
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.loginSuccess(userId, token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        throwable.printStackTrace();
                        Log.e(MyApplication.TAG, "login: " + errorResponse.toString());

                        try {
                            JSONObject error = errorResponse.getJSONObject("error");
                            String msg = error.getString("message");
                            if (msg.contains("login failed")) {
                                Toast.makeText(getActivity(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                            } else if (msg.contains("The mailbox is not verified")) {
                                Toast.makeText(getActivity(), "邮箱未验证", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "登录失败: " + msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
            }
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
