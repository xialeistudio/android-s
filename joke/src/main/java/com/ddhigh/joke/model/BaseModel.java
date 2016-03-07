package com.ddhigh.joke.model;

import com.ddhigh.mylibrary.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

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

    public void parse(JSONObject object) throws JSONException, ParseException {
        if (object.has("id")) {
            id = object.getString("id");
        }
        if (object.has("createdAt")) {
            createdAt = DateUtil.parse(object.getString("createdAt"));
        }
        if (object.has("updatedAt")) {
            updatedAt = DateUtil.parse(object.getString("updatedAt"));
        }
    }
}
