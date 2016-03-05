package com.ddhigh.joke.model;

import android.text.TextUtils;

/**
 * @project Study
 * @package com.ddhigh.joke.model
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class UserModel extends BaseModel {
    /**
     * 是否登录
     * @return 登录情况
     */
    public boolean isGuest() {
        return id == null || TextUtils.isEmpty(id);
    }
}
