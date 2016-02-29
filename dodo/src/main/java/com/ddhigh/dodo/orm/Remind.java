package com.ddhigh.dodo.orm;

import android.location.Location;

/**
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Remind  extends Dao{
    private String title;
    private String thumbnail;
    private int memberCount;
    private int hitCount;
    private int mode;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
