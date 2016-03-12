package com.ddhigh.overtime;

import android.app.Activity;
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
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {
    private List<Activity> activities = new LinkedList<>();
    private File applicationPath;
    private DbManager.DaoConfig daoConfig;
    private AccessToken accessToken;
    private User user;
    private static MyApplication instance;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public void add(Activity activity) {
        activities.add(activity);
    }

    public void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }
        System.exit(0);
    }

    public File getApplicationPath() {
        return applicationPath;
    }

    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setUser(User user) {
        this.user = user;
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
                if (accessToken == null) {
                    accessToken = new AccessToken();
                }
                long expiresIn = accessToken.getCreated_at() + accessToken.getTtl();
                if (expiresIn * 1000 < System.currentTimeMillis()) {
                    accessToken = new AccessToken();
                    Log.w("user", "accessToken invalidate");
                } else {
                    HttpUtil.setToken(accessToken.getToken());
                }
                user = db.findById(User.class, userId);
                if (user == null) {
                    user = new User();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    private void initXUtils() {
        x.Ext.init(this);
        daoConfig = new DbManager.DaoConfig()
                .setDbName("overtime.db")
                .setDbVersion(5)
                .setDbDir(applicationPath)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        Log.d("db", "upgrade overtime.db from " + oldVersion + " to " + newVersion);
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
