package com.ddhigh.joke.model;

import com.ddhigh.joke.util.HttpUtil;
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
        object = HttpUtil.handleMongoId(object);
        if (object.has("_id")) {
            id = object.getString("_id");
        }
        if (object.has("createdAt")) {
            JSONObject c = object.getJSONObject("createdAt");
            createdAt = new Date(c.getLong("sec") * 1000);
        }
        if (object.has("updatedAt")) {
            JSONObject c = object.getJSONObject("updatedAt");
            updatedAt = new Date(c.getLong("sec") * 1000);
        }
    }
}
