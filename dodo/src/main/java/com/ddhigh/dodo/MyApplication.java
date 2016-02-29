package com.ddhigh.dodo;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.HttpUtil;

import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
public class MyApplication extends Application {
    public final static String TAG = "dodo-1";

    public User user;
    public User.AccessToken accessToken;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        HttpUtil.setApi("https://d.apicloud.com");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString(User.PREF_USER_ID, null);
        String token = sharedPreferences.getString(User.PREF_USER_TOKEN, null);
        user = new User();
        accessToken = new User.AccessToken();
        if (userId != null && !TextUtils.isEmpty(userId)) {
            user.setId(userId);
            accessToken.setUserId(userId);
        }
        if (token != null && !TextUtils.isEmpty(token)) {
            accessToken.setId(token);
        }
    }
}
