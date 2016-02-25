package com.ddhigh.dodo.authorize;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        x.view().inject(this, v);
        return v;
    }

    @Event(R.id.btnLogin)
    private void onBtnLoginClicked(final View view) {
        Log.d(MyApplication.TAG, "12341234");
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
