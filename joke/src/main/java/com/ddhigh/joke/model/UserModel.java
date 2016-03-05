package com.ddhigh.joke.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @project Study
 * @package com.ddhigh.joke.model
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class UserModel extends BaseModel {
    private String token;

    /**
     * 是否登录
     *
     * @return 登录情况
     */
    public boolean isGuest() {
        return token == null || TextUtils.isEmpty(token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 保存到本地存储
     *
     * @param applicationContext 应用上下文
     */
    public void save(Context applicationContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("token", token);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.PREF_USER, jsonObject.toString());
            editor.apply();
            Log.d(MyApplication.TAG, "save user ===> " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 还原用户信息
     *
     * @param applicationContext 应用上下文
     */
    public void restore(Context applicationContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if (sharedPreferences.contains(Config.PREF_USER)) {
            String userString = sharedPreferences.getString(Config.PREF_USER, null);
            if (userString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(userString);
                    id = jsonObject.getString("id");
                    token = jsonObject.getString("token");
                    Log.d(MyApplication.TAG, "restore user ===> " + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
