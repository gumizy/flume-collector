package com.datacloudsec.config.geo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * GeoLine
 */
public class GeoLine implements Serializable {

    private GeoPoint from;
    private GeoPoint to;

    private Map<String, Object> infoMap = new HashMap<>();

    public void setInfo(String key, Object value) {
        if (key != null) {
            infoMap.put(key, value);
        }
    }

    public Object getInfo(String key) {
        return key != null ? infoMap.get(key) : null;
    }

    public void clearInfo() {
        infoMap.clear();
    }
}
