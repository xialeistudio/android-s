package com.ddhigh.mylibrary.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class RegexUtil {
    /**
     * 是否手机号码
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 是否邮箱
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        Pattern p = Pattern.compile("\\w+@(\\w+\\.)+[a-z]{2,3}");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证正则
     * @param str
     * @param pattern
     * @return
     */
    public static boolean match(String str, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
