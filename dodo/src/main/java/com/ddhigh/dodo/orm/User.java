package com.ddhigh.dodo.orm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ddhigh.dodo.Config;
import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.util.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 用户
 *
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
@SuppressWarnings("ALL")
public class User extends Dao {
    public final static int SEX_FEMALE = 0;//男
    public final static int SEX_MALE = 1;//女
    public final static int SEX_OTHER = 2;//其他


    public final static String PREF_USER_ID = "PREF_USER_ID";
    public final static String PREF_USER_TOKEN = "PREF_USER_TOKEN";
    public final static String PREF_USER = "PREF_USER";

    private String username = "";
    private String password = "";
    private String email = "";
    private boolean emailVerified = false;
    private int sex = -1;
    private String mobile = "";
    private String nickname = "";
    private String avatar = "";

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return TextUtils.isEmpty(nickname) ? "佚名" : nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isGuest() {
        return id == null || TextUtils.isEmpty(id);
    }

    /**
     * 加载用户数据
     */
    public void loadUser(AsyncHttpResponseHandler handler) {
        HttpUtil.get("/user/" + id, null, handler);
    }

    /**
     * 注销
     *
     * @param context
     */
    public void logout(Context context) {
        //清空缓存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREF_USER);
        editor.remove(PREF_USER_ID);
        editor.remove(PREF_USER_TOKEN);
        editor.apply();
    }

    public String getReadableSex() {
        if (sex == SEX_FEMALE) {
            return "男";
        } else if (sex == SEX_MALE) {
            return "女";
        } else if (sex == SEX_OTHER) {
            return "其他";
        } else {
            return "未选择";
        }
    }


    public static class AccessToken extends Dao {
        private int ttl;
        private String userId;

        public int getTtl() {
            return ttl;
        }

        public void setTtl(int ttl) {
            this.ttl = ttl;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }


        @Override
        public void async(Context context, AsyncHttpResponseHandler handler) throws JSONException {

        }
    }


    public User() {
        super();
    }

    /**
     * 登录
     *
     * @param callback 回调方法
     */
    public void login(Context context, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException, JSONException {
        Log.d(MyApplication.TAG, "login ===> " + username + ", " + password);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        HttpUtil.post(context, "/user/login", jsonObject, handler);
    }

    /**
     * 注册
     *
     * @param callback
     */
    public void register(Context context, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("email", email);
        HttpUtil.post(context, "/user", jsonObject, handler);
    }

    @Override
    public void parse(JSONObject result) throws JSONException {
        super.parse(result);
        username = result.getString("username");
        if (result.has("email") && !result.isNull("email")) {
            email = result.getString("email");
        }
        if (result.has("emailVerified") && !result.isNull("emailVerified")) {
            emailVerified = result.getBoolean("emailVerified");
        }
        if (result.has("sex") && !result.isNull("sex")) {
            sex = result.getInt("sex");
        }
        if (result.has("mobile") && !result.isNull("mobile")) {
            mobile = result.getString("mobile");
        }
        if (result.has("nickname") && !result.isNull("nickname")) {
            nickname = result.getString("nickname");
        }
        if (result.has("avatar") && !result.isNull("avatar")) {
            avatar = result.getString("avatar");
        }
    }

    /**
     * 同步至远程
     *
     * @param callback
     */
    public void async(Context context, AsyncHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("emailVerified", String.valueOf(emailVerified));
        jsonObject.put("sex", String.valueOf(sex));
        jsonObject.put("mobile", mobile);
        jsonObject.put("nickname", nickname);
        jsonObject.put("avatar", avatar);
        HttpUtil.put(context, "/user/" + id, jsonObject, handler);
    }

    /**
     * 用户已过期
     *
     * @param context
     */
    public static void broadcastUserAuthorize(Context context) {
        Toast.makeText(context, "登录过期", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction(Config.Constants.BROADCAST_USER_UNAUTHORIZED);
        context.sendBroadcast(intent);
    }

    /**
     * 保存到本地
     *
     * @param applicationContext
     * @param context
     * @param data
     */
    public static void saveToLocal(Context applicationContext, Context context, String data) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = sp.edit();
        //写入本地存储
        editor.putString(User.PREF_USER, data);
        editor.apply();
        Intent intent = new Intent();
        intent.setAction(Config.Constants.BROADCAST_USER_CHANGED);
        context.sendBroadcast(intent);
    }
}
