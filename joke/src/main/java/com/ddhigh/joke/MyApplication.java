package com.ddhigh.joke;

import android.app.Application;
import android.os.Environment;

import com.ddhigh.joke.model.UserModel;
import com.ddhigh.joke.util.HttpUtil;

import java.io.File;

/**
 * @project Study
 * @package com.ddhigh.joke
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class MyApplication extends Application {
    public final static String TAG = "joke-log";
    public UserModel user;
    public File applicationPath;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new UserModel();
        user.restore(getApplicationContext());
        //初始化请求类
        HttpUtil.setApi("https://d.apicloud.com/mcm/api");
        //初始化应用存储目录
        applicationPath = new File(Environment.getExternalStorageDirectory(), getPackageName());
        if (!applicationPath.isDirectory()) {
            applicationPath.mkdir();
        }
    }
}
