package com.datacloudsec.bootstrap.server.core;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpRequest封装类
 */
public class HttpRequest implements Request {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private HttpExchange httpExchange;

    private Map<String, String> paramMap = new HashMap<>();

    private Map<String, List<String>> headerMap = new HashMap<>();

    private String requestBody = "";

    private String encode = "utf-8";

    public HttpRequest(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public void initRequestParam() {
        String queryString = "";
        switch (httpExchange.getRequestMethod()) {
            default:
            case Request.GET:
                queryString = getRequestURI().getQuery();
                break;
            case Request.POST:
                try {
                    InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), DEFAULT_CHARSET);
                    BufferedReader br = new BufferedReader(isr);
                    queryString = br.readLine();

                } catch (Exception e) {
                    logger.info(queryString);
                    logger.error("HttpRequest initRequestParam error:" + e.getMessage());
                }
                break;
        }

        if (StringUtils.isNotBlank(queryString)) {
            String[] arrayStr = queryString.split("&");
            for (String str : arrayStr) {
                String[] strCache = str.split("=");
                if (strCache.length == 1) {
                    paramMap.put(convert2Decode(strCache[0], encode), "");
                } else {
                    paramMap.put(convert2Decode(strCache[0], encode), convert2Decode(strCache[1], encode));
                }
            }
        }
    }

    @Override
    public void initRequestHeader() {
        for (String s : httpExchange.getRequestHeaders().keySet()) {
            headerMap.put(s, httpExchange.getRequestHeaders().get(s));
        }
    }

    @Override
    public void initRequestBody() {
        InputStream in = httpExchange.getRequestBody(); // 获得输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String temp;
        try {
            while ((temp = reader.readLine()) != null) {
                requestBody += temp;
            }
        } catch (IOException e) {
            logger.error("HttpRequest initRequestBody error:" + e.getMessage());
        }
    }

    @Override
    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public String getParameter(String param) {
        return paramMap.get(param);
    }

    @Override
    public String getParameter(String param, String defaultValue) {
        String value = getParameter(param);
        return value != null ? value : defaultValue;
    }

    @Override
    public String getMethod() {
        return httpExchange.getRequestMethod().trim().toUpperCase();
    }

    @Override
    public URI getRequestURI() {
        return httpExchange.getRequestURI();
    }

    private static String convert2Decode(String target, String charset) {
        try {
//            System.out.println("---------URLDecoder处理前：--------" + target + "--->处理后===" + URLDecoder.save(target, charset));
            return URLDecoder.decode(target, charset);
        } catch (UnsupportedEncodingException e) {
            return target;
        }
    }
}