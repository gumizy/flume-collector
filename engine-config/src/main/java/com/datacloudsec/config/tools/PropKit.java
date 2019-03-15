package com.datacloudsec.config.tools;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropKit {

    private static Prop prop = null;

    private static final Map<String, Prop> map = new ConcurrentHashMap<>();

    private PropKit() {
    }

    public static Prop use(String fileName) {
        return use(fileName, "UTF-8");
    }

    public static Prop use(String fileName, String encoding) {
        Prop result = map.get(fileName);
        if (result == null) {
            result = new Prop(fileName, encoding);
            map.put(fileName, result);
            if (PropKit.prop == null) {
                PropKit.prop = result;
            }
        }
        return result;
    }

    public static Prop use(File file) {
        return use(file, "UTF-8");
    }

    public static Prop use(File file, String encoding) {
        Prop result = map.get(file.getName());
        if (result == null) {
            result = new Prop(file, encoding);
            map.put(file.getName(), result);
            if (PropKit.prop == null) {
                PropKit.prop = result;
            }
        }
        return result;
    }

    public static Prop useless(String fileName) {
        Prop previous = map.remove(fileName);
        if (PropKit.prop == previous) {
            PropKit.prop = null;
        }
        return previous;
    }

    public static void clear() {
        prop = null;
        map.clear();
    }

    public static Prop getProp() {
        if (prop == null) {
            throw new IllegalStateException("Load properties file by invoking PropKit.use(String fileName) method first.");
        }
        return prop;
    }

    public static Prop getProp(String fileName) {
        return map.get(fileName);
    }

    public static String get(String key) {
        return getProp().get(key);
    }

    public static String get(String key, String defaultValue) {
        return getProp().get(key, defaultValue);
    }

    public static Integer getInt(String key) {
        return getProp().getInt(key);
    }

    public static Integer getInt(String key, Integer defaultValue) {
        return getProp().getInt(key, defaultValue);
    }

    public static Long getLong(String key) {
        return getProp().getLong(key);
    }

    public static Long getLong(String key, Long defaultValue) {
        return getProp().getLong(key, defaultValue);
    }

    public static Boolean getBoolean(String key) {
        return getProp().getBoolean(key);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return getProp().getBoolean(key, defaultValue);
    }

    public static boolean containsKey(String key) {
        return getProp().containsKey(key);
    }
}

