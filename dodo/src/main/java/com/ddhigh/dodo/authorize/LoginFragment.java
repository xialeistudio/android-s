package com.ddhigh.dodo.authorize;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.authorize
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
@ContentView(R.layout.fragment_login)
public class LoginFragment extends Fragment {
    @ViewInject(R.id.btnLogin)
    private Button btnLogin;
    @ViewInject(R.id.btnGoRegister)
    private Button btnGoRegister;
    @ViewInject(R.id.btnGoRegister)
    private Button btnRegister;
    @ViewInject(R.id.btnGoLogin)
    private Button btnGoLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Event(value = R.id.btnLogin)
    private void onBtnLoginClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnLoginClicked");
    }

    @Event(value = R.id.btnGoLogin)
    private void onBtnGoRegisterClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnGoRegisterClicked");
    }
}
