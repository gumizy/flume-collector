package com.datacloudsec.bootstrap.server.core;

import java.io.Serializable;
import java.net.URI;

/**
 * Request接口
 */
public interface Request extends Serializable {

    // 默认编码
    String DEFAULT_CHARSET = "UTF-8";

    // 请求方法
    String GET = "GET";
    String POST = "POST";

    /**
     * 初始化header
     */
    void initRequestHeader();

    /**
     * 初始化参数
     */
    void initRequestParam();

    /**
     * 初始化body
     */
    void initRequestBody();

    /**
     * 获取请求参数值
     *
     * @param param param
     * @return String
     */
    String getParameter(String param);

    /**
     * 获取请求参数值
     *
     * @param param param
     * @return String
     */
    String getParameter(String param, String defaultValue);

    /**
     * 获取请求方法
     *
     * @return String
     */
    String getMethod();

    /**
     * 获取请求URI
     *
     * @return URI
     */
    URI getRequestURI();

    /**
     * 获取请求体
     *
     * @return String
     */
    String getRequestBody();
}
