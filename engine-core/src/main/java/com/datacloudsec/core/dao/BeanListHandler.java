package com.datacloudsec.core.dao;


import com.datacloudsec.config.tools.CommonUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @ClassName: BeanListHandler
 * @Description: 将结果集转换成bean对象的list集合的处理器
 * @author: gumizy
 * @date: 2014-10-5 下午12:00:06
 */
public class BeanListHandler implements ResultSetHandler {
    private Class<?> clazz;

    public BeanListHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    public BeanListHandler() {

    }

    @Override
    public Object handler(ResultSet rs) {
        try {
            List<Object> list = new ArrayList<>();
            while (rs.next()) {
                Object bean = clazz.newInstance();

                ResultSetMetaData metadata = rs.getMetaData();
                int count = metadata.getColumnCount();
                for (int i = 0; i < count; i++) {
                    String name = metadata.getColumnName(i + 1);
                    Object value = rs.getObject(name);
                    if (value != null) {
                        name = CommonUtils.format(name);
                        Field f = bean.getClass().getDeclaredField(name);
                        f.setAccessible(true);
                        try {
                            f.set(bean, value);
                        } catch (IllegalArgumentException e) {
                            f.set(bean, Long.parseLong(String.valueOf(value)));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                list.add(bean);
            }
            return list.size() > 0 ? list : Collections.emptyList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> handlerMap(ResultSet rs) {
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> bean = new HashMap<>();
                ResultSetMetaData metadata = rs.getMetaData();
                int count = metadata.getColumnCount();
                for (int i = 0; i < count; i++) {
                    String name = metadata.getColumnName(i + 1);
                    Object value = rs.getObject(name);
                    if (value != null) {
                        name = CommonUtils.format(name);
                        bean.put(name, value);
                    }
                }
                list.add(bean);
            }
            return list.size() > 0 ? list : Collections.emptyList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}