package com.datacloudsec.config.tools;

/**
 * @Author gumizy
 * @Date 2018/8/31 19:58
 */
public class CommonUtils {

    public static String format(String name) {
        if (name.contains("_")) {
            String[] split = name.split("_");
            name = split[0];
            for (int m = 1; m < split.length; m++) {
                name += split[m].substring(0, 1).toUpperCase() + split[m].substring(1).toLowerCase();
            }
        }
        return name;
    }
}
