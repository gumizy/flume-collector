package com.datacloudsec.config.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BeanKit
 */
public class BeanKit {

    /**
     * @param t   t
     * @param <T> <T>
     * @return string
     */
    public static <T> String toString(T t) {
        Class<?> clazz = t.getClass();
        Field[] fs = clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for (Field field : fs) {
            String fieldName = field.getName();
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method;
            try {
                method = clazz.getMethod(methodName);
                sb.append(fieldName).append("=").append(method.invoke(t)).append(" ");
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                    IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 对象转字节数组
     *
     * @param t   t
     * @param <T> <T>
     * @return bytes
     */
    public static <T> byte[] toBytes(T t) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ignore) {
        }
        return bytes;
    }
}
