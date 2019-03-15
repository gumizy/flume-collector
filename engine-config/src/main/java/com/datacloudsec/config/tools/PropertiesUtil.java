package com.datacloudsec.config.tools;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    /**
     * Trans to map
     *
     * @param props props
     * @return Map
     */
    public static Map<String, String> toMap(Properties props) {
        final Map<String, String> map = new HashMap<>();
        if (props != null) {
            for (String name : props.stringPropertyNames()) {
                map.put(name.trim(), props.getProperty(name).trim());
            }
        }
        return map;
    }

    /**
     * Load a Properties object with contents from a File.
     *
     * @param properties the properties object to be loaded
     * @param file       the file to load from
     * @throws IOException IOException
     */
    public static void loadFromFile(final Properties properties, final File file) throws IOException {
        try (final InputStream stream = new FileInputStream(file)) {
            properties.load(stream);
        }
    }

    /**
     * Load the file and return the contents as a Properties object.
     *
     * @param file the file to load
     * @return A Properties object populated
     * @throws IOException IOException
     */
    public static Properties loadFromFile(final File file) throws IOException {
        final Properties properties = new Properties();
        loadFromFile(properties, file);
        return properties;
    }

    /**
     * Write properties to file.
     *
     * @param pair           pair
     * @param propertiesFile propertiesFile
     * @throws Exception Exception
     */
    public static void writeToFile(KeyValuePairs.KeyValuePair pair, File propertiesFile) throws Exception {
        if (propertiesFile == null || !propertiesFile.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(propertiesFile);
            Properties properties = new Properties();
            properties.load(inputStream);
            outputStream = new FileOutputStream(propertiesFile);

            properties.setProperty(pair.getKey(), pair.getValue());
            properties.store(outputStream, "configuration properties file");
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
