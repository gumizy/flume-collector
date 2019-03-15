package com.datacloudsec.config.tools;


import org.apache.commons.lang.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TimeKit
 */
public class TimeKit {

    private static final List<SimpleDateFormat> FormatList = new ArrayList<>();

    static {
        // 年月日时分秒
        FormatList.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        FormatList.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
        FormatList.add(new SimpleDateFormat("yyyyMMdd HHmmss"));
        FormatList.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        FormatList.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        FormatList.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+0800"));
        // 年月日时分
        FormatList.add(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
        FormatList.add(new SimpleDateFormat("yyyy/MM/dd HH:mm"));
        FormatList.add(new SimpleDateFormat("yyyyMMdd HHmm"));
        // 年月日
        FormatList.add(new SimpleDateFormat("yyyy-MM-dd"));
        FormatList.add(new SimpleDateFormat("yyyy/MM/dd"));
        FormatList.add(new SimpleDateFormat("yyyyMMdd"));
    }

    public static long timeString2Long(String time) {
        if (time == null) {
            return 0;
        } else if (NumberUtils.isNumber(time)) { // 时间戳形式
            if (time.length() == 10) {
                return NumberUtils.toLong(time) * 1000L;
            } else if (time.length() == 13) {
                return NumberUtils.toLong(time);
            } else if (time.length() > 13) {
                return NumberUtils.toLong(time.substring(0, 13));
            } else {
                return NumberUtils.toLong(time);
            }
        } else { // 日期字符串格式
            for (SimpleDateFormat sdf : FormatList) {
                try {
                    Date date = sdf.parse(time);
                    return date.getTime();
                } catch (Exception ignore) {
                }
            }
            return 0;
        }
    }
}
