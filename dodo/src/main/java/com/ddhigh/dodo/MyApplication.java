package com.ddhigh.dodo;

import android.app.Application;

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
    }
}
