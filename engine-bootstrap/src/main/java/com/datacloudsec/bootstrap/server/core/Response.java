package com.datacloudsec.bootstrap.server.core;

import java.io.Serializable;

/**
 * Response接口
 */
public interface Response extends Serializable {

    /**
     * 输出响应
     *
     * @param result result
     */
    void write(String result);

    /**
     * 输出响应
     *
     * @param status status
     * @param result result
     */
    void write(int status, String result);
}
