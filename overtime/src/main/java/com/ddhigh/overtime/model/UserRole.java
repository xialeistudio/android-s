package com.ddhigh.overtime.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_user_role")
public class UserRole extends Model {
    @Column(name = "hash", isId = true)
    private String hash;
    @Column(name = "created_at")
    private int created_at;
    @Column(name = "user_id")
    private int user_id;
    @Column(name = "role_id")
    private int role_id;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }
}
