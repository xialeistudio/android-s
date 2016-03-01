package com.ddhigh.dodo.orm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.sql.Date;

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
    public final static int SEX_FEMALE = 1;//男
    public final static int SEX_MALE = 2;//女
    public final static int SEX_OTHER = 3;//其他


    public final static String PREF_USER_ID = "PREF_USER_ID";
    public final static String PREF_USER_TOKEN = "PREF_USER_TOKEN";
    public final static String PREF_USER = "PREF_USER";

    private String username;
    private String password;
    private String email;
    private boolean emailVerified;
    private int sex;
    private String mobile;
    private String nickname;
    private String avatar;

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
    public void loadUser(String token, Callback.CommonCallback<JSONObject> callback) {
        RequestParams params = HttpUtil.prepare("/mcm/api/user/" + id);
        params.addHeader("authorization", token);
        x.http().get(params, callback);
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
    }


    public User() {
        super();
    }

    /**
     * 登录
     *
     * @param callback 回调方法
     */
    public void login(Callback.CommonCallback<JSONObject> callback) {
        RequestParams params = HttpUtil.prepare("/mcm/api/user/login");
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        params.setAsJsonContent(true);
        x.http().post(params, callback);
    }

    /**
     * 注册
     *
     * @param callback
     */
    public void register(Callback.CommonCallback<JSONObject> callback) {
        RequestParams params = HttpUtil.prepare("/mcm/api/user");
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        params.addBodyParameter("email", email);
        params.setAsJsonContent(true);
        x.http().post(params, callback);
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
}
