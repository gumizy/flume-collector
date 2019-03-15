package com.datacloudsec.config.conf.source;

import com.datacloudsec.config.SourceMessage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

/**
 * SourceLogMessage
 */
public class SourceLogMessage implements SourceMessage {
    private Map<String, String> headers = new HashMap();
    private byte[] body = new byte[0];

    public SourceLogMessage(String srcHost) {
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

    public void setHeaders(Map<String, String> val) {
        headers.putAll(val);
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        if (body == null) {
            body = new byte[0];
        }

        this.body = body;
    }

    @Override
    public String toString() {
        Integer bodyLen = null;
        if (this.body != null) {
            bodyLen = this.body.length;
        }

        return "[message headers = " + this.headers + ", body.length = " + bodyLen + " ]";
    }

    @Override
    public String getSendHostIp() {

        return getHeaders(SEND_HOST_IP);
    }

    @Override
    public String getSourceMessage(Charset charset) {
        if (getBody() == null) {
            return null;
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return new String(body, charset);
    }

    @Override
    public String getParserIdentification() {
        return getHeaders(PARSER_IDENTIFICATION);
    }

}
