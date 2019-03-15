package com.datacloudsec.bootstrap.service.util;

import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.config.tools.AESKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Date 2019/1/24 16:07
 */
public class HanderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HanderUtils.class);

    public boolean save(Request request, String paramKey, String decodeKey) {
        boolean reload = false;
        String asset = AESKit.dec(request.getParameter(paramKey), decodeKey);
        if (asset != null) {
            EngineContext.saveOrReload(paramKey, asset);
            reload = true;
        }
        return reload;
    }

    public OrderedProperties getCollectorEngineConfiguration(File file) {
        BufferedReader reader = null;
        OrderedProperties properties = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            properties = new OrderedProperties();
            properties.load(reader);
        } catch (IOException ex) {
            LOGGER.error("Unable to load file:" + file + " (I/O failure) - Exception follows.", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.warn("Unable to close file reader for file: " + file, ex);
                }
            }
        }
        return properties;
    }

}
