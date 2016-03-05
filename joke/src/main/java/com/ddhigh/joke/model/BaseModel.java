package com.ddhigh.joke.model;

import java.sql.Date;

/**
 * @project Study
 * @package com.ddhigh.joke.model
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class BaseModel {
    protected String id;
    protected Date createdAt;
    protected Date updatedAt;

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
}
