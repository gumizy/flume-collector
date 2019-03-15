package com.datacloudsec.bootstrap.service.controller;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.config.tools.CounterKit;


/**
 * @Date 2019/3/6 12:36
 */
@RequestMapping(name = "/sourceCounter")
public class SourceCounterController extends HttpHandler {
    @Override
    public void doGet(Request request, Response response) {
        this.doPost(request, response);
    }

    @Override
    public void doPost(Request request, Response response) {

        response.write(JSON.toJSONString(CounterKit.readAll()));
    }
}
