package com.datacloudsec.parser.parser;

import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.core.Parser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @Date 2019/1/18 17:10
 */
public class KvRegularParser extends AbstractKvParser {
    private static Logger logger = LoggerFactory.getLogger(KvRegularParser.class);
    public static String message = "message";

    @Override
    protected void setEventField(String sendHost, Event event, Matcher matcher) {
        super.setEventField(sendHost, event, matcher, false);
        setKvMessageField(sendHost, event, matcher);
    }

    private void setKvMessageField(String sendHost, Event event, Matcher matcher) {
        try {
            if (StringUtils.isBlank(getSourceField())) {
                logger.debug("this is warn: {} sourceField  is null", getSourceField());
                return;
            }
            String msg = matcher.group(getSourceField());
            if (StringUtils.isNotBlank(msg)) {
                msg = msg.trim();

                if (CollectionUtils.isEmpty(getFieldParsers())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("this is warn: {} type kv_regular rule collect is null", getEventType());
                    }
                    return;
                }
                Matcher matcher1 = getSecPattern().matcher(msg);
                final Map<String, String> kvMap = new HashMap<>();
                while (matcher1.find()) {
                    String key = matcher1.group(1);
                    String value = matcher1.group(2);
                    if (StringUtils.isNotBlank(key)) {
                        kvMap.put(key, value.trim().replaceAll("^\"|\"$", "").replaceAll("^\"|\"$", ""));
                    }
//                        if (ContextKit.isDevMode()) {
//                            logger.info(key + "=" + value);
//                        }
                }
                setKvField(sendHost, event, kvMap);
            }
         } catch (Throwable e) {
            logger.error("setKvMessageField error: {}", e.getMessage());
        }
    }



    public static class Builder extends AbstractBuilder {

        @Override
        public Parser build(EventDecodeRule parseRule) {
            return super.build(new KvRegularParser(), parseRule);
        }
    }
}
