package com.datacloudsec.source.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.config.conf.source.JsonSourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class JsonHandler implements HttpSourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JsonHandler.class);

    @Override
    public List<SourceMessage> getMessage(HttpServletRequest request) throws Exception {
        BufferedReader reader = request.getReader();
        String charset = request.getCharacterEncoding();
        String line = null;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        if (charset == null) {
            LOG.debug("Charset is null, default charset of UTF-8 will be used.");
            charset = "UTF-8";
        } else if (!(charset.equalsIgnoreCase("utf-8") || charset.equalsIgnoreCase("utf-16") || charset.equalsIgnoreCase("utf-32"))) {
            LOG.error("Unsupported character set in request {}. JSON handler supports UTF-8, UTF-16 and UTF-32 only.", charset);
            throw new UnsupportedCharsetException("JSON handler supports UTF-8, UTF-16 and UTF-32 only.");
        }

        JSONArray messages;
        try {
            messages = JSON.parseArray(builder.toString());
        } catch (Exception ex) {
            throw new HttpBadRequestException("Request has invalid JSON Syntax.", ex);
        }

        return getSimpleEvents(messages, request.getRemoteHost());
    }

    @Override
    public void configure(Context context) {
    }

    private List<SourceMessage> getSimpleEvents(JSONArray messages, String remoteHost) {
        int size = messages.size();
        List<SourceMessage> newMessages = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject messagesJSONObject = messages.getJSONObject(i);
            if (messagesJSONObject == null) {
                continue;
            }
            JsonSourceMessage message = new JsonSourceMessage(remoteHost);

            message.setJson(messagesJSONObject);
            newMessages.add(message);
        }
        return newMessages;
    }
}
