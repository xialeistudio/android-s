package com.ddhigh.dodo.orm;

import org.json.JSONObject;

import java.sql.Date;

/**
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Dao {
    protected String id;
    protected Date createdAt;
    protected Date updatedAt;

    public Dao() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Dao(JSONObject jsonObject) throws Exception {
        throw new Exception("子类必须重载此构造方法");
    }
}
