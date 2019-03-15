package com.datacloudsec.config;

import java.nio.charset.Charset;

/**
 * @Date 2019/1/17 19:12
 */
public interface SourceMessage {

    String getHeaders(String key);

    void setHeaders(String key, String value);

    byte[] getBody();

    void setBody(byte[] var1);

    String getSendHostIp();

    String getSourceMessage(Charset charset);

    String getParserIdentification();
}
