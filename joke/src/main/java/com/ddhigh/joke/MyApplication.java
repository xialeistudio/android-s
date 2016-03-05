package com.ddhigh.joke;

import android.app.Application;

import com.ddhigh.joke.model.UserModel;
import com.ddhigh.joke.util.HttpUtil;

/**
 * @project Study
 * @package com.ddhigh.joke
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class MyApplication extends Application {
    public final static String TAG = "joke-log";
    public UserModel user;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new UserModel();
        //初始化请求类
        HttpUtil.setApi("https://d.apicloud.com/mcm/api");
    }
}
