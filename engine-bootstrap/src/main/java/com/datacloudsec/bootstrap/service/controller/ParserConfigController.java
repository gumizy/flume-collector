package com.datacloudsec.bootstrap.service.controller;

import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.config.tools.AESKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.*;

/**
 * 解析规则处理
 */
@RequestMapping(name = "/allConfig")
public class ParserConfigController extends HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ParserConfigController.class);

    @Override
    public void doGet(Request request, Response response) {
        response.write(UNSUPPORTED);
    }

    @Override
    public void doPost(Request request, Response response) {
        try {
            // collector
            String collector = AESKit.dec(request.getParameter(COLLECTOR), AES_KEY);
            if (collector != null) {
                EngineContext.saveOrReload(COLLECTOR, collector);
            }

            // asset
            String asset = AESKit.dec(request.getParameter(ASSET), AES_KEY);
            if (asset != null) {
                EngineContext.saveOrReload(ASSET, asset);
            }

            // info base
            String infoBase = AESKit.dec(request.getParameter(INFO_BASE), AES_KEY);
            if (infoBase != null) {
                EngineContext.saveOrReload(INFO_BASE, infoBase);
            }

            // user
            String userInfo = AESKit.dec(request.getParameter(USER), AES_KEY);
            if (userInfo != null) {
                EngineContext.saveOrReload(USER, userInfo);
            }

            // reload all
            EngineContext.componentsInitialization();

        } catch (Exception e) {
            logger.error("process error: " + e.getMessage(), e);
            response.write(ERROR);
            return;
        }
        response.write(SUCCESS);
    }
}
