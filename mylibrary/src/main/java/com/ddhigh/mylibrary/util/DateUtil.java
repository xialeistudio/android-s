package com.ddhigh.mylibrary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
public class DateUtil {
    /**
     * 解析时间字符串
     * @param timeStr
     * @return
     * @throws ParseException
     */
    public static Date parse(String timeStr) throws ParseException {
        timeStr = timeStr.replace("T"," ").replace("Z"," ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return simpleDateFormat.parse(timeStr);
    }

    /**
     * 格式化时间戳
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(date);
    }
}
