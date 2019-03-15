package com.datacloudsec.config.tools;



import com.datacloudsec.config.generator.TimeIdGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeIdUtil {

    private static TimeIdGenerator generator = new TimeIdGenerator();

    private static DateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * 生成带时间序列的ID
     *
     * @param time time
     * @return Long
     */
    public static Long generator(Long time) {
        return generator.generate(time);
    }

    /**
     * 根据ID还原时间
     *
     * @param id id
     * @return Long
     */
    public static Long getTime(Long id) {
        return generator.revert(id);
    }

    /**
     * 生成索引前缀
     *
     * @param id id
     * @return String
     */
    public static String getIndexSuffix(Long id) {
        if (id == null)
            return null;
        return format.format(getTime(id));
    }
}
