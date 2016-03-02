package com.ddhigh.dodo.orm;

import android.location.Location;

import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.List;

/**
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Sign extends Dao {
    private String message;
    private List<String> images;
    private String userId;
    private String remindId;
    private Location location;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRemindId() {
        return remindId;
    }

    public void setRemindId(String remindId) {
        this.remindId = remindId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void async(Callback.CommonCallback<JSONObject> callback) {

    }
}
