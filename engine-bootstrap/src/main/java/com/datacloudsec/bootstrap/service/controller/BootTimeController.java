package com.datacloudsec.bootstrap.service.controller;

import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;

/**
 * BootTimeHandler
 */
@RequestMapping(name = "/bootTimeConfig")
public class BootTimeController extends HttpHandler {

    @Override
    public void doGet(Request request, Response response) {
        long bootTime = EngineContext.getBootTime();
        response.write(bootTime + "");
    }

    @Override
    public void doPost(Request request, Response response) {
        doGet(request, response);
    }
}
