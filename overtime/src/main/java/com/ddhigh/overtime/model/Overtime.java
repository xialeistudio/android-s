package com.ddhigh.overtime.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_overtime")
public class Overtime extends Model {

    /**
     * 状态
     */
    public static final byte STATUS_CREATE = 0;//审批中
    public static final byte STATUS_ACCEPT = 1;//已审批
    public static final byte STATUS_REJECT = 2;//已拒绝

    @Column(name = "id", isId = true, autoGen = false)
    private int id;
    @Column(name = "content")
    private String content;
    @Column(name = "begin_at")
    private String begin_at;
    @Column(name = "end_at")
    private String end_at;
    @Column(name = "status")
    private byte status;
    @Column(name = "created_at")
    private int created_at;
    @Column(name = "user_id")
    private int user_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBegin_at() {
        return begin_at;
    }

    public void setBegin_at(String begin_at) {
        this.begin_at = begin_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
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
}
