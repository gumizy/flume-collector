package com.datacloudsec.config.tools;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * IdKit
 *
 * @author gumizy 2017/7/5
 */
public class IdKit {

    /**
     * 是否全部为ID
     *
     * @param ids ids
     * @return boolean
     */
    public static boolean isIds(String ids) {
        if (StringUtils.isBlank(ids)) return false;
        String[] arr = StringUtils.split(ids, ",");
        for (String id : arr) {
            if (!StringUtils.isNumeric(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转化为id list
     *
     * @param ids ids
     * @return List
     */
    public static List<Integer> toIntegerList(String ids) {
        if (StringUtils.isBlank(ids))
            return Collections.emptyList();
        String[] arr = StringUtils.split(ids, ",");
        List<Integer> list = new ArrayList<>();
        for (String s : arr) {
            int num = NumberUtils.toInt(s, 0);
            list.add(num);
        }
        return list;
    }

    /**
     * 转化为id integer array
     *
     * @param ids ids
     * @return Integer
     */
    public static Integer[] toIntegerArray(String ids) {
        if (StringUtils.isBlank(ids))
            return new Integer[0];
        String[] arr = StringUtils.split(ids, ",");
        Integer[] array = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            array[i] = NumberUtils.toInt(arr[i], 0);
        }
        return array;
    }

    /**
     * 转化为id string
     *
     * @param list list
     * @return String
     */
    public static String join(List<Integer> list, char separator) {
        if (CollectionUtils.isEmpty(list)) return "";
        return StringUtils.join(list.toArray(), separator);
    }
}
