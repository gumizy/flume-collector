package com.datacloudsec.bootstrap.server.core;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * HttpResponse封装类
 */
public class HttpResponse implements Response {

    private static Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private HttpExchange httpExchange;

    public HttpResponse(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    /**
     * 输出响应
     */
    @Override
    public void write(String result) {
        write(HttpURLConnection.HTTP_OK, result);
    }

    /**
     * 输出响应
     */
    @Override
    public void write(int status, String result) {
        try {
            // 设置响应头属性及响应信息的长度
            httpExchange.sendResponseHeaders(status, result.getBytes().length);
            OutputStream out = httpExchange.getResponseBody();
            out.write(result.getBytes());
            out.flush();
            httpExchange.close();
        } catch (Exception e) {
            logger.error("status is: " + status + ", result is: " + result);
            logger.error("HttpResponse write error:" + e.getMessage());
        }
    }

}