package com.datacloudsec.config.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Prop {

    private static final Logger logger = LoggerFactory.getLogger(Prop.class);

    private Properties properties = null;

    /**
     * Prop constructor.
     *
     * @see #Prop(String, String)
     */
    public Prop(String fileName) {
        this(fileName, "UTF-8");
    }

    public Prop(String fileName, String encoding) {
        InputStream inputStream = null;
        try {
            inputStream = getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                File file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    inputStream = new FileInputStream(file);
                }
                if (inputStream == null) {
                    throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
                }
            }
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : getClass().getClassLoader();
    }

    /**
     * Prop constructor.
     *
     * @see #Prop(File, String)
     */
    public Prop(File file) {
        this(file, "UTF-8");
    }

    /**
     * Prop constructor
     * <p>
     * <p>
     * <p>
     * Example:<br>
     * <p>
     * Prop prop = new Prop(new File("/var/config/my_config.txt"), "UTF-8");<br>
     * <p>
     * String userName = prop.get("userName");
     *
     * @param file     the properties File object
     * @param encoding the encoding
     */
    public Prop(File file, String encoding) {
        if (file == null) {
            throw new IllegalArgumentException("File can not be null.");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("File not found : " + file.getName());
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Long.parseLong(value.trim());
        }
        return defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            value = value.toLowerCase().trim();
            if ("true".equals(value)) {
                return true;
            } else if ("false".equals(value)) {
                return false;
            }
            throw new RuntimeException("The value can not parse to Boolean : " + value);
        }
        return defaultValue;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Properties getProperties() {
        return properties;
    }
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (properties != null) {
            for (String name : properties.stringPropertyNames()) {
                if (name != null && !name.trim().isEmpty()) {
                    map.put(name.trim(), properties.getProperty(name).trim());
                }
            }
        }
        return map;
    }
}
