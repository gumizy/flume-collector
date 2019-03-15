package com.datacloudsec.source.http;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.config.conf.LogPrivacyUtil;
import com.datacloudsec.config.conf.source.SourceLogMessage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

public class BlobHandler implements HttpSourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BlobHandler.class);

    @Override
    public List<SourceMessage> getMessage(HttpServletRequest request) throws Exception {
        Map<String, String> headers = new HashMap<>();

        InputStream inputStream = request.getInputStream();

        Map<String, String[]> parameters = request.getParameterMap();
        for (String parameter : parameters.keySet()) {
            String value = parameters.get(parameter)[0];
            if (LOG.isDebugEnabled() && LogPrivacyUtil.allowLogRawData()) {
                LOG.debug("Setting Header [Key, Value] as [{},{}] ", parameter, value);
            }
            headers.put(parameter, value);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream, outputStream);
            LOG.debug("Building an Event with stream of size -- {}", outputStream.size());
            SourceLogMessage message = new SourceLogMessage(request.getRemoteHost());
            message.setHeaders(headers);
            message.setHeaders(PARSER_IDENTIFICATION, request.getRemoteHost() + COLLECT_TYPE_SEPARATOR + COLLECT_TYPE_HTTP);

            message.setBody(outputStream.toByteArray());

            List<SourceMessage> messages = new ArrayList<>();
            messages.add(message);
            return messages;
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    @Override
    public void configure(Context context) {
        // no oper
    }

}
