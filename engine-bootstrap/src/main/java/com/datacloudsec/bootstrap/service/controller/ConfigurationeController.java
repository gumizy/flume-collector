package com.datacloudsec.bootstrap.service.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.bootstrap.service.util.HanderUtils;
import com.datacloudsec.bootstrap.service.util.OrderedProperties;
import com.datacloudsec.config.tools.AESKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.*;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.*;

/**
 * 控制台application.properties配置
 */
@RequestMapping(name = "/configuration")
public class ConfigurationeController extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CollectorConfigController.class);

    @Override
    public void doGet(Request request, Response response) {
        response.write(UNSUPPORTED);
        this.doPost(request, response);
    }

    @Override
    public void doPost(Request request, Response response) {
        // configuration

        String action = request.getParameter(ACTION);

        if (action == null) {
            response.write(ERROR);
            return;
        }
        File configurationFile = new File(ENGINE_CONFIG_STORE_PATH);

        Properties configuration;
        if (action.equalsIgnoreCase(GET)) {
            try {
                HanderUtils handerUtils = new HanderUtils();
                configuration = handerUtils.getCollectorEngineConfiguration(configurationFile);
            } catch (Exception e) {
                logger.error("process error: " + e.getMessage(), e);
                response.write(ERROR);
                return;
            }
            if (configuration == null) {
                response.write(ERROR);
                return;
            }
            Set<Object> keySet = configuration.keySet();
            Iterator<Object> iterator = keySet.iterator();

            JSONObject applicationObject = new JSONObject(true);
            while (iterator.hasNext()) {
                Object next = iterator.next();
                if (next != null) {
                    String key = String.valueOf(next);
                    String value = configuration.getProperty(key);
                    applicationObject.put(key, value);
                }
            }
            response.write(applicationObject.toJSONString());
            return;
        } else if (action.equalsIgnoreCase(SAVE)) {
            String paramStr = AESKit.dec(request.getParameter(CONFIGURATION), AES_KEY);
            if (StringUtils.isBlank(paramStr) && !paramStr.equalsIgnoreCase("null")) {
                response.write(ERROR);
                return;
            }
            LinkedHashMap applicationProperties;
            try {
                applicationProperties = JSON.parseObject(paramStr, LinkedHashMap.class);
            } catch (Exception e) {
                logger.error("Incorrect file format", e);
                response.write(ERROR);
                return;
            }

            if (valid(response, paramStr)) {
                return;
            }
            if (applicationProperties.isEmpty()) {
                response.write(ERROR);
                return;
            }
            try {
                save(applicationProperties);
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.write(SUCCESS);
            return;
        }

    }

    /**
     * 保存修改
     *
     * @param map
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void save(LinkedHashMap map) throws FileNotFoundException, IOException {
        // back
        //日期转换为字符串
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
        String nowStr = now.format(format);

        FileInputStream inputStream = new FileInputStream(ENGINE_CONFIG_STORE_PATH);
        FileOutputStream outputStream = new FileOutputStream(ENGINE_CONFIG_STORE_PATH + "-" + nowStr);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(outputStream);
        IOUtils.closeQuietly(inputStream);

        // update
        OrderedProperties prop = new OrderedProperties();

        FileOutputStream fos = new FileOutputStream(ENGINE_CONFIG_STORE_PATH);
        //遍历HashMap
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            prop.setProperty(entry.getKey(), entry.getValue().toString());
        }
        //写入文件
        prop.store(fos, null);
        fos.close();
    }

    private boolean valid(Response response, String paramStr) {
        JSONObject applicationObject = JSON.parseObject(paramStr);
        for (Map.Entry<String, Object> entry : applicationObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(CONFIG_CHANNEL_PREFIX) || key.startsWith(CONFIG_SINK_PREFIX) || key.startsWith(CONFIG_SOURCE_PREFIX)) {

            } else {
                logger.error("application configuration error ,key {},value {}", key, value);
                response.write(ERROR);
                return true;
            }
        }
        return false;
    }
}
