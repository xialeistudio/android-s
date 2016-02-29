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

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.widget.IosAlertDialog;
import com.ddhigh.dodo.widget.IosConfirmDialog;
import com.ddhigh.dodo.widget.LoadingDialog;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

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
            IosConfirmDialog.Builder dialogBuilder = new IosConfirmDialog.Builder(getActivity());
            dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.d(MyApplication.TAG, "clicked ok");
                }
            });
            dialogBuilder.setNegativeButton("取消2", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.d(MyApplication.TAG, "clicked cancel");
                }
            });

            IosConfirmDialog dialog = dialogBuilder.create();
            dialog.setCancelable(false);
            dialog.show();

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
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragmentContainer, registerFragment, "registerFragment");
        fragmentTransaction.commit();

        getActivity().setTitle("注册");
    }
}
