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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        x.view().inject(this, inflater, container);
        Log.d(MyApplication.TAG, btnLogin.toString());
        Log.d(MyApplication.TAG, btnGoRegister.toString());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Event(value = R.id.btnGoRegister)
    private void onBtnGoRegisterClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnGoRegisterClicked");
    }
}
