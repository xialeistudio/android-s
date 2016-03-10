package com.ddhigh.overtime;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.ddhigh.overtime.model.AccessToken;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

public class MyApplication extends Application {
    private File applicationPath;
    private DbManager.DaoConfig daoConfig;
    private AccessToken accessToken;
    private User user;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }

    public File getApplicationPath() {
        return applicationPath;
    }

    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化HttpUtil
        HttpUtil.setApi("https://overtime.ddhigh.com/v1");
        //初始化应用存储目录
        initApplicationDirectory();
        //初始化xUtils
        initXUtils();
        //初始化ImageLoader
        initImageLoader();
        //初始化用户
        initUser();
    }

    private void initUser() {
        //读取本地缓存的userId
        user = new User();
        accessToken = new AccessToken();
        int userId = User.getUserIdFromLocal(getApplicationContext());
        if (userId != 0) {
            //读取本地数据库
            accessToken.setUser_id(userId);
            user.setUser_id(userId);
            DbManager db = x.getDb(daoConfig);
            try {
                accessToken = db.selector(AccessToken.class).where("user_id", "=", userId).findFirst();
                user = db.findById(User.class, userId);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    private void initXUtils() {
        x.Ext.init(this);
        daoConfig = new DbManager.DaoConfig()
                .setDbName("overtime_db")
                .setDbVersion(1)
                .setDbDir(applicationPath)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                });
    }

    private void initApplicationDirectory() {
        applicationPath = new File(Environment.getExternalStorageDirectory(), getPackageName());
        if (!applicationPath.isDirectory()) {
            applicationPath.mkdir();
        }
    }

    private void initImageLoader() {
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
                .diskCache(new UnlimitedDiskCache(new File(applicationPath, "cache")))
                .defaultDisplayImageOptions(displayImageOptions)
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5000, 30000))
                .build();

        ImageLoader.getInstance().init(configuration);
    }
}
