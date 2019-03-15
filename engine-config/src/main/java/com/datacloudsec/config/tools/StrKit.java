package com.datacloudsec.config.tools;


import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * StrKit
 *
 * @author gumizy 2017/8/31
 */
public class StrKit {

    /**
     * 截取字符串
     *
     * @param string    string
     * @param maxLength maxLength
     * @return String
     */
    public static String str(String string, int maxLength) {
        if (string == null || maxLength <= 0) {
            return "";
        }
        return string.length() > maxLength ? string.substring(0, maxLength - 1) : string;
    }

    /**
     * 按字符串切割字符串
     *
     * @param string string
     * @param c      c
     * @return String[]
     */
    public static String[] split(String string, char c) {
        return StringUtils.split(string, c);
    }


    /**
     * 按字符串切割字符串
     *
     * @param string   string
     * @param splitStr splitStr
     * @return String[]
     */
    public static String[] split(String string, String splitStr) {
        return StringUtils.splitByWholeSeparatorPreserveAllTokens(string, splitStr);
    }

    /**
     * 按字符串合并
     *
     * @param list    list
     * @param joinStr joinStr
     * @return String[]
     */
    public static String join(List<String> list, String joinStr) {
        return StringUtils.join(list, joinStr);
    }

    /**
     * 按字符串合并
     *
     * @param array   list
     * @param joinStr joinStr
     * @return String[]
     */
    public static String join(String[] array, String joinStr) {
        return StringUtils.join(array, joinStr);
    }

    /**
     * 包含某个字符
     *
     * @param string string
     * @param c      c
     * @return boolean
     */
    public static boolean has(String string, char c) {
        return string != null && string.indexOf(c) >= 0;
    }

    /**
     * 包含某个字符串
     *
     * @param string string
     * @param sub    sub
     * @return boolean
     */
    public static boolean contains(String string, String sub) {
        return string != null && string.contains(sub);
    }

    /**
     * 是否匹配正则
     *
     * @param string string
     * @param regex  regex
     * @return boolean
     */
    public static boolean matchRegex(String string, String regex) {
        if (string == null || regex == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(string).find();
    }
}
