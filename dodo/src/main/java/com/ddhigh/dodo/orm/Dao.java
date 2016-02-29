package com.ddhigh.dodo.orm;

import android.util.Log;

import com.ddhigh.dodo.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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

    public void parse(JSONObject result) throws JSONException {
        if (result.has("id") && !result.isNull("id")) {
            id = result.getString("id");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        if (result.has("createdAt") && !result.isNull("createdAt")) {
            try {
                String createdAtStr = result.getString("createdAt").replace("T", " ").replace("Z", " ");
                createdAt = simpleDateFormat.parse(createdAtStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (result.has("updatedAt") && !result.isNull("updatedAt")) {
            try {
                String updateAtStr = result.getString("updatedAt").replace("T", " ").replace("Z", " ");
                updatedAt = simpleDateFormat.parse(updateAtStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.d(MyApplication.TAG, "parse " + result.toString() + " ===> " + getClass().getName() + " ===> createdAt: " + simpleDateFormat.format(createdAt) + ", updatedAt: " + simpleDateFormat.format(updatedAt));
    }
}
