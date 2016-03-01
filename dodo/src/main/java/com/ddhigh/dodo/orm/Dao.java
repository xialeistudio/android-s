package com.ddhigh.dodo.orm;

import android.util.Log;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.util.DateUtil;

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
abstract public class Dao {
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
                createdAt = DateUtil.parse(result.getString("createdAt"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (result.has("updatedAt") && !result.isNull("updatedAt")) {
            try {
                updatedAt = DateUtil.parse(result.getString("updatedAt"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.d(MyApplication.TAG, "parse " + result.toString() + " ===> " + getClass().getName() + " ===> createdAt: " + simpleDateFormat.format(createdAt) + ", updatedAt: " + simpleDateFormat.format(updatedAt));
    }

    /**
     * 同步数据至远程
     */
    abstract public void async() throws JSONException;
}
