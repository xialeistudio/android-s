package com.ddhigh.overtime.util;

import com.ddhigh.overtime.R;

/**
 * @project android-s
 * @package com.ddhigh.overtime.util
 * @user xialeistudio
 * @date 2016/3/12 0012
 */
public class AppUtil extends com.ddhigh.mylibrary.util.AppUtil{
    /**
     * 根据状态获取颜色
     * @param status 状态值
     * @return 颜色值
     */
    public static int getStatusColor(int status) {
        switch (status) {
            case 0:
                return R.color.colorWarning;
            case 1:
                return R.color.colorSuccess;
            case 2:
                return R.color.colorDanger;
            default:
                return android.R.color.black;
        }
    }

    /**
     * 获取状态
     * @param status 状态值
     * @return 状态文本
     */
    public static String getStatusText(int status) {
        return new String[]{
                "审批中",
                "已审批",
                "己拒绝"
        }[status];
    }
}
