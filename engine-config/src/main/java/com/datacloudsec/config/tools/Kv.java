package com.datacloudsec.config.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Kv: 用于处理JSON字符串的小工具
 *
 * @author gumizy 2017/5/11
 */
public class Kv implements Serializable {

    private Map<String, Object> data;

    private Kv(Map<String, Object> data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data = new JSONObject();
        }
    }

    public Kv set(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getStr(String key) {
        if (data.containsKey(key)) {
            if (data.get(key) == null) {
                return null;
            } else {
                return String.valueOf(data.get(key));
            }
        } else {
            return null;
        }
    }

    public String getStr(String key, String defaultValue) {
        String str = getStr(key);
        return str == null ? defaultValue : str;
    }

    public Integer getInt(String key) {
        String str = getStr(key);
        return str != null ? NumberUtils.toInt(str, 0) : null;
    }

    public Long getLong(String key) {
        String str = getStr(key);
        return str != null ? NumberUtils.toLong(str, 0) : null;
    }

    public Double getDouble(String key) {
        String str = getStr(key);
        return str != null ? NumberUtils.toDouble(str, 0) : null;
    }

    public Boolean getBoolean(String key) {
        String str = getStr(key);
        return "true".equalsIgnoreCase(str);
    }

    public int getInt(String key, int defaultValue) {
        String str = getStr(key);
        return str != null ? NumberUtils.toInt(str, defaultValue) : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        String str = getStr(key);
        return str != null ? NumberUtils.toLong(str, defaultValue) : defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        String str = getStr(key);
        return str != null ? NumberUtils.toDouble(str, defaultValue) : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (data.containsKey(key)) {
            String str = getStr(key);
            return "true".equalsIgnoreCase(str);
        } else {
            return defaultValue;
        }
    }

    public Kv remove(String key) {
        data.remove(key);
        return this;
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public Kv clear() {
        data.clear();
        return this;
    }

    public Map<String, Object> toMap() {
        return data;
    }

    public String toJSON() {
        return JSON.toJSONString(data);
    }

    ///////////////////////////////////////////////////
    public static Kv create() {
        return new Kv(null);
    }

    public static Kv create(String json) {
        JSONObject jo = JSON.parseObject(json);
        return new Kv(jo);
    }
}
