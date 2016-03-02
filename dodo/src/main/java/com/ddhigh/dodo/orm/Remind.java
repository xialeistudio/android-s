package com.ddhigh.dodo.orm;

import android.location.Location;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

/**
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Remind extends Dao {

    public static final int MODE_DAY = 1;

    private String title = "";
    private String thumbnail = "";
    private int memberCount = 0;
    private int hitCount = 0;
    private int mode = MODE_DAY;
    private String userId = "";
    private Location location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void parse(JSONObject result) throws JSONException {
        super.parse(result);
        if (result.has("title") && !TextUtils.isEmpty(result.getString("title"))) {
            title = result.getString("title");
        }
        if (result.has("thumbnail") && !TextUtils.isEmpty(result.getString("thumbnail"))) {
            thumbnail = result.getString("thumbnail");
        }
        if (result.has("memberCount") && result.getInt("memberCount") > 0) {
            memberCount = result.getInt("memberCount");
        }
        if (result.has("hitCount") && result.getInt("hitCount") > 0) {
            hitCount = result.getInt("hitCount");
        }
        if (result.has("mode") && result.getInt("mode") > 0) {
            mode = result.getInt("mode");
        }

        if (result.has("userId") && !TextUtils.isEmpty(result.getString("userId"))) {
            userId = result.getString("userId");
        }
        if(result.has("location")){
            location = new Location("apicloud");
            location.setLatitude(result.getDouble("lat"));
            location.setLongitude(result.getDouble("lng"));
        }
    }

    @Override
    public void async(Callback.CommonCallback<JSONObject> callback) {

    }
}
