package com.datacloudsec.config.generator;

import com.datacloudsec.config.tools.KeyValuePairs;
import com.datacloudsec.config.tools.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class IdentityIdGenerator implements IdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IdentityIdGenerator.class);

    private static final String idPropertiesFilePath = "collector_resource" + File.separator + "id_record.properties";

    private static long id;

    private static final String idKey = "currentId";

    private static File propertiesFile = null;

    private static long currentId;

    private static final int DEFAULT_MIN_SIZE = 1000000;

    static {
        /*try {
            propertiesFile = new File(idPropertiesFilePath);
            if (!propertiesFile.exists()) {
                FileKit.ensureFileExist((idPropertiesFilePath));
                propertiesFile = new File(idPropertiesFilePath);
                logger.warn("Current id properties file with path:{} is not exists, has bean create", idPropertiesFilePath);
            }
            Properties p = PropertiesUtil.loadFromFile(new File(idPropertiesFilePath));
            Object currentId = p.get(idKey);
            if (currentId != null) {
                id = NumberUtils.toLong(currentId.toString(), 0);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }*/
    }

    public synchronized Long generate() {
        if (++id >= currentId) {
            currentId += DEFAULT_MIN_SIZE;
//            sinkToFile();
        }
        return id;
    }

    public void sinkToFile() {
        KeyValuePairs.KeyValuePair pair = new KeyValuePairs.KeyValuePair(idKey, String.valueOf(currentId));
        try {
            PropertiesUtil.writeToFile(pair, propertiesFile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
