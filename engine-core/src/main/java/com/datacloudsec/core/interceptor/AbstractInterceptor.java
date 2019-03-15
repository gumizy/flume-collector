package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractInterceptor {

    protected String enrichConfigField;

    protected String[] enrichFields;

    protected Context context;

    public AbstractInterceptor() {
    }

    protected <T> void inject(T t, String fieldName, Event event) {
        Class<? extends Object> clazz = t.getClass();
        String subfix = fieldName.substring(fieldName.indexOf("_") + 1);

        try {
            while (subfix.contains("_")) {
                int start = subfix.indexOf("_");
                String left = subfix.substring(0, start);
                String right = subfix.substring(start + 1);
                subfix = left + right.substring(0, 1).toUpperCase() + right.substring(1);
            }
            Method m = clazz.getMethod("get" + subfix.substring(0, 1).toUpperCase() + subfix.substring(1));
            Object rst = m.invoke(t);
            event.putValue(fieldName, rst);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected <T> void inject(String fieldName, T t, Event event) {
        Class<? extends Object> clazz = t.getClass();
        String subfix = fieldName;

        if (fieldName.contains("_")) {
            int start = fieldName.indexOf("_");
            String left = fieldName.substring(0, start);
            String right = fieldName.substring(fieldName.indexOf("_") + 1);
            subfix = left + right.substring(0, 1).toUpperCase() + right.substring(1);
        }

        try {
            Method m = clazz.getMethod("get" + subfix.substring(0, 1).toUpperCase() + subfix.substring(1));
            Object rst = m.invoke(t);
            event.putValue(fieldName, rst);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected void inject(String filed, String value, Event event) {
        Class<? extends Event> clazz = event.getClass();
        try {
            Method m = clazz.getMethod("set" + filed.substring(0, 1).toUpperCase() + filed.substring(1));
            m.invoke(event, value);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据配置的丰富化字段返回原始事件中配置丰富化字段的值（现阶段只定义为ip,类型为string，暂不做扩展）
     *
     * @param event
     * @return
     */
    protected String getEnrichValue(Event event) {
        Class<? extends Event> clazz = event.getClass();
        try {
            Method m = clazz.getMethod("get" + this.enrichConfigField.substring(0, 1).toUpperCase() + this.enrichConfigField.substring(1));
            Object object = m.invoke(event);
            return object == null ? null : (String) object;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据事件字段名和事件获取对应字段值
     *
     * @param fieldName fieldName
     * @param event     event
     * @return Object
     */
    protected Object getEventFieldValue(String fieldName, Event event) {
        Object obj = null;
        if (null != event && null != fieldName && !"".equals(fieldName)) {
            obj = event.getValue().get(fieldName);
        }
        return obj;

    }

    /**
     * 根据需要丰富的字典数组和反射字段数组,以及字典库,丰富化字段,校验放在调用方处理
     *
     * @param enrichFields        enrichFields
     * @param reflectEnrichFields reflectEnrichFields
     * @param dictMap             dictMap
     * @param event               event
     */
    protected void enrichFieldValue(String[] enrichFields, String[] reflectEnrichFields, Map<String, String> dictMap, Event event) {
        for (int i = 0, len = enrichFields.length; i < len; i++) {
            String enrichField = enrichFields[i];
            String reflectEnrichField = reflectEnrichFields[i];
            if (reflectEnrichField != null) {
                String fieldName = reflectEnrichField.toLowerCase();
                Object dictFieldValue = dictMap.get(fieldName);
                if (null != dictFieldValue) {
                    event.putValue(enrichField, dictFieldValue);
                }
            }
        }
    }

    ;


    /**
     * 根据字段名称和事件获得字段值
     *
     * @param fieldName fieldName
     * @param event     event
     * @return String
     */
    protected String getFieldValue(String fieldName, Event event) {
        String fieldValue = null;
        Object obj = getEventFieldValue(fieldName, event);
        if (null != obj) {
            fieldValue = obj.toString();
        }
        return fieldValue;
    }
}
