package com.ddhigh.dodo.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.authorize.LoginFragment;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@SuppressWarnings({"unused", "deprecation"})
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    MyApplication application;
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

        application = (MyApplication) getApplication();
        //fragment初始化
        FragmentManager fragmentManager = getFragmentManager();
        RemindListFragment fragment = new RemindListFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, "remindListFragment")
                .show(fragment)
                .commit();

        setTitle(R.string.app_name);
    }

    @Event(R.id.btnList)
    private void onBtnListClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnListClicked");
        setTitle(R.string.app_name);
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
        Fragment fragment;
        String tag;
        if (application.user.isGuest()) {
            tag = "loginFragment";
            fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new LoginFragment();
            }
        } else {
            tag = "userFragment";
            fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new UserFragment();
            }
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .show(fragment)
                .commit();
    }

    /**
     * 登录成功
     *
     * @param userId 用户ID
     * @param token  授权码
     */
    public void loginSuccess(String userId, String token) {
        Log.d(MyApplication.TAG, "loginSuccess ===> userId: " + userId + ", token: " + token);
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        //持久化
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.PREF_USER_ID, userId);
        editor.putString(User.PREF_USER_TOKEN, token);
        editor.apply();
        //更新user对象
        application.user = new User();
        application.user.setId(userId);
        //更新授权对象
        application.accessToken = new User.AccessToken();
        application.accessToken.setId(token);
        application.accessToken.setUserId(userId);
        //读取用户数据
        HttpUtil.setToken(token);
        application.user.loadUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //同步至磁盘
                try {
                    application.user.parse(response);
                    editor.putString(User.PREF_USER, response.toString());
                    editor.apply();

                    FragmentManager fragmentManager = getFragmentManager();
                    UserFragment fragment = (UserFragment) fragmentManager.findFragmentByTag("userFragment");
                    if (fragment == null) {
                        fragment = new UserFragment();
                    }
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment, "userFragment")
                            .show(fragment)
                            .commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "读取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
