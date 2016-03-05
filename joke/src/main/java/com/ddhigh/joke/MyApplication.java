package com.ddhigh.joke;

import android.app.Application;

import com.ddhigh.joke.model.UserModel;

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
    }
}
