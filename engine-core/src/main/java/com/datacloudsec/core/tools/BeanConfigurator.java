

package com.datacloudsec.core.tools;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.ConfigurationException;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class BeanConfigurator {

    public static void setConfigurationFields(Object configurable, Map<String, String> properties) throws ConfigurationException {
        Class<?> clazz = configurable.getClass();

        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("set") && method.getParameterTypes().length == 1) {
                String fieldName = methodName.substring(3);

                String value = properties.get(StringUtils.uncapitalize(fieldName));
                if (value != null) {

                    Class<?> fieldType = method.getParameterTypes()[0];
                    ;
                    try {
                        if (fieldType.equals(String.class)) {
                            method.invoke(configurable, value);
                        } else if (fieldType.equals(boolean.class)) {
                            method.invoke(configurable, Boolean.parseBoolean(value));
                        } else if (fieldType.equals(short.class)) {
                            method.invoke(configurable, Short.parseShort(value));
                        } else if (fieldType.equals(long.class)) {
                            method.invoke(configurable, Long.parseLong(value));
                        } else if (fieldType.equals(float.class)) {
                            method.invoke(configurable, Float.parseFloat(value));
                        } else if (fieldType.equals(int.class)) {
                            method.invoke(configurable, Integer.parseInt(value));
                        } else if (fieldType.equals(double.class)) {
                            method.invoke(configurable, Double.parseDouble(value));
                        } else if (fieldType.equals(char.class)) {
                            method.invoke(configurable, value.charAt(0));
                        } else if (fieldType.equals(byte.class)) {
                            method.invoke(configurable, Byte.parseByte(value));
                        } else if (fieldType.equals(String[].class)) {
                            method.invoke(configurable, (Object) value.split("\\s+"));
                        } else {
                            throw new ConfigurationException("Unable to configure component due to an unsupported type on field: " + fieldName);
                        }
                    } catch (Exception ex) {
                        if (ex instanceof ConfigurationException) {
                            throw (ConfigurationException) ex;
                        } else {
                            throw new ConfigurationException("Unable to configure component: ", ex);
                        }
                    }
                }
            }
        }
    }

    public static void setConfigurationFields(Object configurable, Context context) throws ConfigurationException {
        Class<?> clazz = configurable.getClass();
        Map<String, String> properties = context.getSubProperties(clazz.getSimpleName() + ".");
        setConfigurationFields(configurable, properties);
    }

    public static void setConfigurationFields(Object configurable, Context context, String subPropertiesPrefix) throws ConfigurationException {
        Map<String, String> properties = context.getSubProperties(subPropertiesPrefix);
        setConfigurationFields(configurable, properties);
    }
}
