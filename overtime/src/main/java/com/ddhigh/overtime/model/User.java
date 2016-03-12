package com.ddhigh.overtime.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ddhigh.overtime.MyApplication;
import com.ddhigh.overtime.activity.LoginActivity;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_user")
public class User extends Model {
    public static final String PREF_USER_ID = "PREF_USER_ID";
    public static final String PREF_USER = "PREF_USER";
    public static final String PREF_USERNAME = "PREF_USERNAME";
    public static final String PREF_PASSWORD = "PREF_PASSWORD";
    @Column(name = "user_id", isId = true, autoGen = false)
    private int user_id;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "realname")
    private String realname;
    @Column(name = "phone")
    private String phone;
    @Column(name = "created_at")
    private int created_at;
    @Column(name = "total_time")
    private int total_time;

    public User() {
        super();
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getTotal_time() {
        return total_time;
    }

    public void setTotal_time(int total_time) {
        this.total_time = total_time;
    }

    /**
     * 注册
     *
     * @param username 帐号
     * @param password 密码
     * @param handler  回调
     */
    public void register(String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("realname", realname);
        params.put("phone", phone);
        HttpUtil.post("/user/register", params, handler);
    }

    /**
     * 登录
     *
     * @param username 帐号
     * @param password 密码
     * @param handler  回调
     */
    public void login(String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        HttpUtil.post("/user/login", params, handler);
    }


    /**
     * 从本地读取 userId
     *
     * @param applicationContext 应用上下文
     * @return 用户ID
     */
    public static int getUserIdFromLocal(Context applicationContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return sharedPreferences.getInt(PREF_USER_ID, 0);
    }

    /**
     * 登录
     *
     * @param activity    回调
     * @param hasCallback 是否有回调
     */
    public static void loginRequired(Activity activity, boolean hasCallback) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_USER_ID);
        editor.apply();
        MyApplication application = (MyApplication) activity.getApplication();
        try {
            if (!application.getAccessToken().isGuest())
                x.getDb(application.getDaoConfig()).delete(AccessToken.class, WhereBuilder.b().and("user_id", "=", application.getAccessToken().getUser_id()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        application.setAccessToken(new AccessToken());
        application.setUser(new User());
        Intent intent = new Intent(activity, LoginActivity.class);
        if (hasCallback) {
            intent.putExtra("isCallback",true);
            activity.startActivityForResult(intent, RequestCode.LOGIN);
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * 写入本地UserId
     *
     * @param applicationContext 应用上下文
     */
    public void saveUserId(Context applicationContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, user_id);
        editor.apply();
    }

    /**
     * 保存用户ID到本地
     *
     * @param applicationContext 应用上下文
     * @param userId             用户ID
     */
    public static void saveUserIdToLocal(Context applicationContext, int userId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, userId);
        editor.apply();
    }
}
