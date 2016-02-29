package com.ddhigh.dodo.orm;

/**
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Praise extends Dao {
    private String userId;
    private String signId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }
}
