package com.datacloudsec.bootstrap.service.controller;

import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.bootstrap.service.util.HanderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.AES_KEY;
import static com.datacloudsec.bootstrap.server.HttpServerConstants.ASSET;

/**
 * 资产配置
 */
@RequestMapping(name = "/assetConfig")
public class AssetConfigController extends HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssetConfigController.class);

    @Override
    public void doGet(Request request, Response response) {
        response.write(UNSUPPORTED);
    }

    @Override
    public void doPost(Request request, Response response) {
        try {
            HanderUtils hander = new HanderUtils();
            String paramKey = ASSET;
            String decodeKey = AES_KEY;
            boolean reload;
            // asset
            reload = hander.save(request, paramKey, decodeKey);
            if (reload) {
                // reload asset
                EngineContext.assetInfoInit();
            }
        } catch (Exception e) {
            logger.error("process error: " + e.getMessage(), e);
            response.write(ERROR);
            return;
        }
        response.write(SUCCESS);
    }
}