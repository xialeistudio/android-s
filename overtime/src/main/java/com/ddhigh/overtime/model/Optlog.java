package com.ddhigh.overtime.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 操作日志
 *
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_optlog")
public class Optlog extends Model {
    @Column(name = "log_id", isId = true)
    private int log_id;
    @Column(name = "msg")
    private String msg;
    @Column(name = "created_at")
    private int created_at;
    @Column(name = "created_ip")
    private String created_ip;
    @Column(name = "user_id")
    private int user_id;

    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public String getCreated_ip() {
        return created_ip;
    }

    public void setCreated_ip(String created_ip) {
        this.created_ip = created_ip;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
