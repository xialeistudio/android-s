package com.ddhigh.dodo.main;

import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.authorize.LoginFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.imageList)
    private ImageView imageList;
    @ViewInject(R.id.imageMy)
    private ImageView imageMy;
    @ViewInject(R.id.txtList)
    private TextView txtList;
    @ViewInject(R.id.txtMy)
    private TextView txtMy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        FragmentManager fragmentManager = getFragmentManager();
        RemindListFragment fragment = new RemindListFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, "remindListFragment")
                .show(fragment)
                .commit();
    }

    @Event(R.id.btnList)
    private void onBtnListClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnListClicked");
        //字体颜色处理
        Resources resources = getResources();
        imageList.setImageDrawable(resources.getDrawable(R.drawable.icon_list_blue));
        txtList.setTextColor(resources.getColor(R.color.tabSelectedColor));
        imageMy.setImageDrawable(resources.getDrawable(R.drawable.icon_user_gray));
        txtMy.setTextColor(resources.getColor(R.color.tabNormalColor));
        //fragment处理
        FragmentManager fragmentManager = getFragmentManager();
        RemindListFragment fragment = (RemindListFragment) fragmentManager.findFragmentByTag("reminderListFragment");
        if (fragment == null) {
            fragment = new RemindListFragment();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, "reminderListFragment")
                .show(fragment)
                .commit();
    }

    @Event(R.id.btnMy)
    private void onBtnMyClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnMyClicked");
        //字体颜色处理
        Resources resources = getResources();
        imageList.setImageDrawable(resources.getDrawable(R.drawable.icon_list_gray));
        txtList.setTextColor(resources.getColor(R.color.tabNormalColor));
        imageMy.setImageDrawable(resources.getDrawable(R.drawable.icon_user_blue));
        txtMy.setTextColor(resources.getColor(R.color.tabSelectedColor));
        //fragment处理
        FragmentManager fragmentManager = getFragmentManager();
        //TODO://判断登录状态显示不同fragment
        LoginFragment fragment = (LoginFragment) fragmentManager.findFragmentByTag("loginFragment");
        if (fragment == null) {
            fragment = new LoginFragment();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, "loginFragment")
                .show(fragment)
                .commit();
    }

    /**
     * 登录成功
     * @param userId
     * @param token
     */
    public void loginSuccess(String userId, String token) {
        Log.d(MyApplication.TAG, "loginSuccess ===> userId: " + userId + ", token: " + token);
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }
}
