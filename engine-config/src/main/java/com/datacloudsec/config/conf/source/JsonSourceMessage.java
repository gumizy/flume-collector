

package com.datacloudsec.config.conf.source;

import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.config.tools.JsonUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

public class JsonSourceMessage implements SourceMessage {

    private JSONObject json;
    private Map<String, String> headers;

    public JsonSourceMessage(String srcHost) {
        json = new JSONObject();

        headers = new HashMap<>();
        headers.put(RECEIVE_TIME, String.valueOf(System.currentTimeMillis()));
        headers.put(SEND_HOST_IP, srcHost);
    }

    public void addProperty(String name, Object value) {
        if (value instanceof Date) {
            json.put(name, JsonUtils.to((Date) value));
        } else if (value instanceof Number) {
            json.put(name, value);
        } else if (value instanceof Boolean) {
            json.put(name, value);
        } else if (value == null) {
            json.put(name, null);
        } else {
            json.put(name, value.toString());
        }
    }

    @Override
    public String getHeaders(String key) {
        return headers.get(key);
    }

    @Override
    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public byte[] getBody() {
        return this.json.toJSONString().getBytes(Charset.defaultCharset());
    }

    @Override
    public void setBody(byte[] var1) {
        this.json = new JSONObject().getJSONObject(new String(var1, Charset.defaultCharset()));
    }

    @Override
    public String getSendHostIp() {

        return getHeaders(SEND_HOST_IP);
    }

    @Override
    public String getSourceMessage(Charset charset) {
        // no oper charset
        if (json == null) {
            return null;
        }
        return json.toJSONString();
    }

    @Override
    public String getParserIdentification() {
        return headers.get(PARSER_IDENTIFICATION);
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "JSONEvent [headers=" + headers + ", body=" + json + "]";
    }

}
