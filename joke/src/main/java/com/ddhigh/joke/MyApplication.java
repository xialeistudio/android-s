package com.ddhigh.joke;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import com.ddhigh.joke.model.UserModel;
import com.ddhigh.joke.util.HttpUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

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
        //初始化用户
        user = new UserModel();
        user.restore(getApplicationContext());
        if (!user.isGuest()) {
            HttpUtil.setToken(user.getToken());
        }
        //初始化请求类
        HttpUtil.setApi("http://jokexx.duapp.com/v1");
        //初始化应用存储目录
        applicationPath = new File(Environment.getExternalStorageDirectory(), getPackageName());
        if (!applicationPath.isDirectory()) {
            applicationPath.mkdir();
        }
        //初始化ImageLoader
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCacheExtraOptions(640, 1136)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(100)
                .diskCache(new UnlimitedDiskCache(new File(applicationPath, "image-cache")))
                .defaultDisplayImageOptions(displayImageOptions)
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5000, 30000))
                .build();

        ImageLoader.getInstance().init(configuration);
    }
}
