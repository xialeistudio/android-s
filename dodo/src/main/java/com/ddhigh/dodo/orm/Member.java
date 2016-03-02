package com.ddhigh.dodo.orm;

import org.json.JSONObject;
import org.xutils.common.Callback;

/**
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Member extends Dao {
    private String userId;
    private String remindId;

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

    @Override
    public void async(Callback.CommonCallback<JSONObject> callback) {

    }
}
