package com.ddhigh.dodo.authorize;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
public class RegisterFragment extends Fragment {
    @ViewInject(R.id.btnRegister)
    Button btnRegister;
    @ViewInject(R.id.btnGoLogin)
    Button btnGoLogin;

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
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragmentContainer, loginFragment, "loginFragment");
        fragmentTransaction.commit();

        getActivity().setTitle("登录");
    }
}
