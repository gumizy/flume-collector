package com.datacloudsec.bootstrap.service.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.config.tools.AESKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.AES_KEY;
import static com.datacloudsec.bootstrap.server.HttpServerConstants.ENGINE;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.*;

/**
 * 引擎配置处理
 */
@RequestMapping(name = "/engineConfig")
public class EngineConfigController extends HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(EngineConfigController.class);

    @Override
    public void doGet(Request request, Response response) {
        response.write(UNSUPPORTED);
    }

    @Override
    public void doPost(Request request, Response response) {
        try {
            // engine
            String engine = AESKit.dec(request.getParameter(ENGINE), AES_KEY);
            if (engine != null) {
                Properties applicationProperties = new Properties();
                JSONObject applicationObject = JSON.parseObject(engine);
                for (Map.Entry<String, Object> entry : applicationObject.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.startsWith(CONFIG_CHANNEL_PREFIX) || key.startsWith(CONFIG_SINK_PREFIX) || key.startsWith(CONFIG_SOURCE_PREFIX)) {
                        applicationProperties.put(key, value);
                    } else {
                        logger.error("application configuration error ,key {},value {}", key, value);
                    }
                }
                if (applicationProperties.isEmpty()) {
                    response.write(ERROR);
                    return;
                }
                FileOutputStream outputStream = new FileOutputStream(new File(BASE_STORE_PATH));
                applicationProperties.store(outputStream, "configuration properties file");
            }
        } catch (Exception e) {
            logger.error("process error: " + e.getMessage(), e);
            response.write(ERROR);
            return;
        }
        response.write(SUCCESS);
    }
}
