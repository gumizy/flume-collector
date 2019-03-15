package com.datacloudsec.bootstrap.server.core;

/**
 * HttpHandler实现类
 */
public abstract class HttpHandler implements Handler {

    protected static final String SUCCESS = "SUCCESS";

    protected static final String ERROR = "ERROR";

    protected static final String UNSUPPORTED = "UNSUPPORTED";

    /**
     * Service
     *
     * @param request  request
     * @param response response
     */
    @Override
    public void doService(Request request, Response response) {
        request.initRequestHeader();
        request.initRequestParam();
        if (request.getMethod().equals(Request.GET)) {
            doGet(request, response);
        } else if (request.getMethod().equals(Request.POST)) {
            request.initRequestBody();
            doPost(request, response);
        }
    }

    /**
     * Get
     *
     * @param request  request
     * @param response response
     */
    @Override
    public abstract void doGet(Request request, Response response);

    /**
     * Post
     *
     * @param request  request
     * @param response response
     */
    @Override
    public abstract void doPost(Request request, Response response);

}
