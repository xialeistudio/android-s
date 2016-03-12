package com.ddhigh.overtime.model;

import android.text.TextUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * AccessToken
 *
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_access_token",onCreated = "CREATE INDEX userId on ot_access_token(user_id)")
public class AccessToken extends Model {
    @Column(name = "token", isId = true)
    private String token;
    @Column(name = "ttl")
    private int ttl;
    @Column(name = "created_at")
    private int created_at;
    @Column(name = "user_id")
    private int user_id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * 是否为游客
     */
    public boolean isGuest() {
        return TextUtils.isEmpty(token);
    }

}
