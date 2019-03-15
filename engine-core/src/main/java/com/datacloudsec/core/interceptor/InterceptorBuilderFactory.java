package com.datacloudsec.core.interceptor;

import java.util.Locale;

/**
 * InterceptorBuilderFactory
 */
public class InterceptorBuilderFactory {

    private static Class<? extends Interceptor.Builder> lookup(String name) {
        try {
            return InterceptorType.valueOf(name.toUpperCase(Locale.ENGLISH)).getBuilderClass();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Interceptor.Builder newInstance(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<? extends Interceptor.Builder> clazz = lookup(name);
        if (clazz == null) {
            clazz = (Class<? extends Interceptor.Builder>) Class.forName(name);
        }
        return clazz.newInstance();
    }
}
