package com.datacloudsec.bootstrap.service.controller;

import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制台application.properties配置
 */
@RequestMapping(name = "/reset")
public class ResetConfigurationController extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CollectorConfigController.class);

    @Override
    public void doGet(Request request, Response response) {
        response.write(UNSUPPORTED);
        this.doPost(request, response);
    }

    @Override
    public void doPost(Request request, Response response) {
        // reset
        try {
            EngineContext.applicationProptiesReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.write(SUCCESS);
        return;
    }
}
