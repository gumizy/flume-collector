package com.datacloudsec.config.tools;

import org.apache.commons.lang.StringUtils;

/**
 * IPv4Kit
 *
 * @author gumizy 2017/6/22
 */
public class IPv4Kit {

    public static long ip2long(String ip) {
        long[] ipArr = ip2array(ip);
        return (ipArr[0] << 24)
                + (ipArr[1] << 16)
                + (ipArr[2] << 8)
                + ipArr[3];
    }

    public static String long2ip(long ip) {
        StringBuilder sb = new StringBuilder(30);
        // 右移24位
        sb.append(String.valueOf(ip >>> 24));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((ip & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((ip & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(ip & 0x000000FF));
        return sb.toString();
    }

    public static long[] ip2array(String ip) {
        if (StringUtils.isBlank(ip))
            throw new IllegalArgumentException("IP Format Error");
        long[] result = new long[4];
        String[] arr = ip.split("\\.");
        if (arr.length != 4) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < arr.length; i++) {
            result[i] = Long.parseLong(arr[i]);
            if (result[i] < 0 || result[i] > 255) {
                throw new IllegalArgumentException("IP Format Error");
            }
        }
        return result;
    }

    public static boolean isIPv4(String ip) {
        try {
            ip2array(ip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMask(int mask) {
        return mask > 0 && mask <= 32;
    }
}
