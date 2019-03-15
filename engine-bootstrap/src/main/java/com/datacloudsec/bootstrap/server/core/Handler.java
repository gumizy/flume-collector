package com.datacloudsec.bootstrap.server.core;

import java.io.Serializable;

/**
 * Handler接口类
 */
public interface Handler extends Serializable {

    /**
     * Service
     *
     * @param request  request
     * @param response response
     */
    void doService(Request request, Response response);

    /**
     * Get
     *
     * @param request  request
     * @param response response
     */
    void doGet(Request request, Response response);

    /**
     * Post
     *
     * @param request  request
     * @param response response
     */
    void doPost(Request request, Response response);
}
