

package com.datacloudsec.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private Map<String, String> parameters;

    public Context() {
        parameters = new ConcurrentHashMap();
    }

    public Context(Map<String, String> paramters) {
        this();
        this.putAll(paramters);
    }

    public ImmutableMap<String, String> getParameters() {
        synchronized (parameters) {
            return ImmutableMap.copyOf(parameters);
        }
    }

    public void clear() {
        parameters.clear();
    }

    public ImmutableMap<String, String> getSubProperties(String prefix) {
        Preconditions.checkArgument(prefix.endsWith("."), "The given prefix does not end with a period (" + prefix + ")");
        Map<String, String> result = Maps.newHashMap();
        synchronized (parameters) {
            for (Entry<String, String> entry : parameters.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(prefix)) {
                    String name = key.substring(prefix.length());
                    result.put(name, entry.getValue());
                }
            }
        }
        return ImmutableMap.copyOf(result);
    }

    public void putAll(Map<String, String> map) {
        parameters.putAll(map);
    }

    public void put(String key, String value) {
        parameters.put(key, value);
    }

    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = get(key);
        if (value != null) {
            return Boolean.valueOf(Boolean.parseBoolean(value.trim()));
        }
        return defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        String value = get(key);
        if (value != null) {
            return Integer.valueOf(Integer.parseInt(value.trim()));
        }
        return defaultValue;
    }

    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = get(key);
        if (value != null) {
            return Long.valueOf(Long.parseLong(value.trim()));
        }
        return defaultValue;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public String getString(String key, String defaultValue) {
        return get(key, defaultValue);
    }

    public String getString(String key) {
        return get(key);
    }

    public Float getFloat(String key, Float defaultValue) {
        String value = get(key);
        if (value != null) {
            return Float.parseFloat(value.trim());
        }
        return defaultValue;
    }

    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    public Double getDouble(String key, Double defaultValue) {
        String value = get(key);
        if (value != null) {
            return Double.parseDouble(value.trim());
        }
        return defaultValue;
    }

    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    private String get(String key, String defaultValue) {
        String result = parameters.get(key);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    private String get(String key) {
        return get(key, null);
    }

    @Override
    public String toString() {
        return "{ parameters:" + parameters + " }";
    }
}
