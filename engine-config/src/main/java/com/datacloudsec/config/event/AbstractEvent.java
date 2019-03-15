package com.datacloudsec.config.event;

import com.datacloudsec.config.geo.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * AbstractEvent: 事件数据的基类
 */
public abstract class AbstractEvent implements Serializable {

    /**
     * 值集合
     */
    protected final Map<String, Object> valueMap = new HashMap<>(128);

    /**
     * 将值放入Map中
     *
     * @param field field
     * @param value value
     */
    public abstract void putValue(String field, Object value);

    public Long getId() {
        return getLong(CommonConstants.ID);
    }

    public void setId(Long id) {
        putValue(CommonConstants.ID, id);
    }

    public String getIndexType() {
        return getString(CommonConstants.INDEX_TYPE);
    }

    public void setIndexType(String indexType) {
        putValue(CommonConstants.INDEX_TYPE, indexType);
    }

    public Map<String, Object> getValue() {
        return valueMap;
    }

    public Object getValue(String field) {
        return valueMap.get(field);
    }

    protected Long getLong(String field) {
        Object obj = getValue(field);
        return obj != null && obj instanceof Long ? (Long) obj : null;
    }

    protected Integer getInteger(String field) {
        Object obj = getValue(field);
        return obj != null && obj instanceof Integer ? (Integer) obj : null;
    }

    protected Double getDouble(String field) {
        Object obj = getValue(field);
        return obj != null && obj instanceof Double ? (Double) obj : null;
    }

    protected Float getFloat(String field) {
        Object obj = getValue(field);
        return obj != null && obj instanceof Float ? (Float) obj : null;
    }

    public String getString(String field) {
        Object obj = getValue(field);
        return obj != null ? String.valueOf(obj) : null;
    }
    public String getString() {

        return "ddddddddd";
    }
    protected GeoPoint getGeoPoint(String field) {
        Object obj = getValue(field);
        return obj != null && obj instanceof GeoPoint ? (GeoPoint) obj : null;
    }
}
