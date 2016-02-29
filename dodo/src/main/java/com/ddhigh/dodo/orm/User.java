package com.ddhigh.dodo.orm;

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

    private String username;
    private String password;
    private String email;
    private String emailVerified;
    private int sex;
    private String mobile;


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

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
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

    /**
     * 登录
     * @param callback 回调方法
     */
    public void login(Callback.CommonCallback<JSONObject> callback) {
        RequestParams params = HttpUtil.prepare("/mcm/api/user/login");
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        params.setAsJsonContent(true);
        x.http().post(params, callback);
    }

    public User() {
        super();
    }

    public User(JSONObject jsonObject) throws Exception {
        super(jsonObject);
    }
}
