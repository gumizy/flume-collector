package com.datacloudsec.config.conf.source;

import com.datacloudsec.config.SourceMessage;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

/**
 * @Date 2019/1/28 15:37
 */
public class SimpleSourceMessage implements SourceMessage {
    private Map<String, String> headers = new HashMap();
    private String message = new String();
    private Charset charset = Charset.defaultCharset();

    public SimpleSourceMessage(String srcHost) {
        headers.put(RECEIVE_TIME, String.valueOf(System.currentTimeMillis()));
        headers.put(SEND_HOST_IP, srcHost);
    }

    @Override
    public String getHeaders(String key) {
        return headers.get(key);
    }

    @Override
    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    @Deprecated
    public byte[] getBody() {
        return this.message.getBytes(charset);
    }

    @Deprecated
    public void setBody(byte[] body) {
        if (body == null) {
            body = new byte[0];
        }
        this.message = new String(body, charset);
    }

    @Override
    public String toString() {

        return "[message headers = " + this.headers + ", body = " + message + " ]";
    }

    @Override
    public String getSendHostIp() {

        return getHeaders(SEND_HOST_IP);
    }

    @Override
    public String getSourceMessage(Charset charset) {
        // NO OPER
        return message;
    }

    @Override
    public String getParserIdentification() {
        return headers.get(PARSER_IDENTIFICATION);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}

