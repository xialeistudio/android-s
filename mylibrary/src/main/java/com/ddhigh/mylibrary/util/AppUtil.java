package com.ddhigh.mylibrary.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @project Study
 * @package com.ddhigh.mylibrary.util
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class AppUtil {
    public static PackageInfo getAppInfo(Context context) {
        PackageManager packageManager;
        PackageInfo info = null;
        packageManager = context.getPackageManager();
        try {
            info = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }
}
