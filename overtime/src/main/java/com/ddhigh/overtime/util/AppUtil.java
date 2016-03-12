package com.ddhigh.overtime.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.ddhigh.overtime.R;

/**
 * @project android-s
 * @package com.ddhigh.overtime.util
 * @user xialeistudio
 * @date 2016/3/12 0012
 */
public class AppUtil extends com.ddhigh.mylibrary.util.AppUtil {
    /**
     * 根据状态获取颜色
     *
     * @param status 状态值
     * @return 颜色值
     */
    public static Drawable getStatusBackground(Context context, int status) {
        int resId = 0;
        switch (status) {
            case 0:
                resId = R.drawable.label_warning;
                break;
            case 1:
                resId = R.drawable.label_success;
                break;
            case 2:
                resId = R.drawable.label_danger;
                break;
            default:
                resId = R.drawable.label_warning;
                break;
        }
        return context.getResources().getDrawable(resId);
    }

    /**
     * 获取状态
     *
     * @param status 状态值
     * @return 状态文本
     */
    public static String getStatusText(int status) {
        return new String[]{
                "审批中",
                "已通过",
                "被拒绝"
        }[status];
    }

    /**
     * 获取系统信息
     * @return 简要信息
     */
    public static String getSystemInfo() {
        return Build.MODEL + "|" + Build.VERSION.SDK_INT + "|" + Build.VERSION.RELEASE;
    }
}
