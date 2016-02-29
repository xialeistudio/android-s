package com.ddhigh.dodo.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ddhigh.dodo.R;
import com.ddhigh.dodo.authorize.LoginFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public Fragment[] fragments = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        fragments[0] = new RemindListFragment();
        //TODO:判断登录状态显示UserFragment
        fragments[1] = new LoginFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragments[0], "mainFragment")
                .add(R.id.fragmentContainer, fragments[1], "userFragment")
                .hide(fragments[0])
                .hide(fragments[1])
                .show(fragments[0])
                .commit();
    }
}
