package com.datacloudsec.bootstrap.service.controller;

import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.bootstrap.service.util.HanderUtils;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.AES_KEY;
import static com.datacloudsec.bootstrap.server.HttpServerConstants.FLAG;

/**
 * 授权handler
 */
@RequestMapping(name = "/authConfig")
public class AuthController extends HttpHandler {

    @Override
    public void doGet(Request request, Response response) {
        response.write(UNSUPPORTED);
    }

    @Override
    public void doPost(Request request, Response response) {
        HanderUtils hander = new HanderUtils();
        String paramKey = FLAG;
        String decodeKey = AES_KEY;
        boolean reload;
        // asset
        reload = hander.save(request, paramKey, decodeKey);
        if (reload) {
            // reload asset
            EngineContext.authorizationInit();
        }
        response.write(SUCCESS);
    }
}
