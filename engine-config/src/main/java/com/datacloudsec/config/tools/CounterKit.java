package com.datacloudsec.config.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CounterKit
 */
public class CounterKit {

    private static final Map<String, AtomicLong> CounterMap = new ConcurrentHashMap<>(1024);

    /**
     * 读取全部
     */
    public static Map<String, Long> readAll() {
        return readPrefix(null);
    }

    /**
     * 读取特定前缀的所有数据
     */
    public static Map<String, Long> readPrefix(String prefix) {
        if (CounterMap.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Long> map = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : CounterMap.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue().longValue();
            if (prefix == null || key.startsWith(prefix)) {
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 全部数据求和
     */
    public static long getSum() {
        return getSumPrefix(null);
    }

    /**
     * 特定前缀的所有数据求和
     */
    public static long getSumPrefix(String prefix) {
        Map<String, Long> map = readPrefix(prefix);
        if (map.isEmpty()) return 0L;
        long total = 0L;
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    /**
     * 清除全部
     */
    public static void clearAll() {
        CounterMap.clear();
    }

    /**
     * 清除某个key
     */
    public static void clear(String key) {
        if (key != null && CounterMap.containsKey(key)) {
            CounterMap.remove(key);
        }
    }

    /**
     * 清除特定前缀的key
     */
    public static void clearPrefix(String prefix) {
        if (prefix == null) return;
        Set<String> keys = CounterMap.keySet();
        for (String key : keys) {
            if (key != null && key.startsWith(prefix)) {
                clear(key);
            }
        }
    }

    /**
     * 自动加1
     */
    public static void increaseOne(String key) {
        increase(key, 1L);
    }

    /**
     * 自动加n
     */
    public static void increase(String key, long n) {
        if (key == null) return;
        AtomicLong value = CounterMap.get(key);
        if (value == null) {
            CounterMap.put(key, new AtomicLong(n));
        } else {
            value.getAndAdd(n);
        }
    }

    /**
     * 设置值
     *
     * @param key   key
     * @param value value
     */
    public static void set(String key, int value) {
        if (key == null) return;
        CounterMap.put(key, new AtomicLong(value));
    }

    /**
     * 获取值
     */
    public static long get(String key) {
        if (key == null) return 0;
        AtomicLong value = CounterMap.get(key);
        return value != null ? value.longValue() : 0;
    }
}
