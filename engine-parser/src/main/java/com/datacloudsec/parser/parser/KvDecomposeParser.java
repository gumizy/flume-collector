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
public class KvDecomposeParser extends AbstractKvParser {
    private static Logger logger = LoggerFactory.getLogger(KvDecomposeParser.class);
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
                        logger.debug("this is warn: {} type kv_decompose rule collect is null", getEventType());
                    }
                    return;
                }
                // 字符遍历
                String fieldSeparator = getFieldSeparator();
                String kvSeparator = getKvSeparator();
                final Map<String, String> kvMap = new HashMap<>();
                // 去特殊化
                if (getValuePackagePattern() != null) {
                    Matcher matcher2 = getValuePackagePattern().matcher(msg);
                    while (matcher2.find()) {
                        String kv = matcher2.group();
                        int index = kv.indexOf(kvSeparator);
                        if (index < 0) {
                            continue;
                        }
                        String keyTmp = kv.substring(0, index);
                        String valueTmp = kv.substring(index + 1);
                        // 去掉边界符
                        valueTmp = valueTmp.trim();
                        if (valueTmp.length() > 1) {
                            char firstChar = valueTmp.charAt(0);
                            char lastChar = valueTmp.charAt(valueTmp.length() - 1);
                            if (firstChar == lastChar) {
                                valueTmp = valueTmp.substring(1, valueTmp.length() - 1);
                            }
                        }
                        kvMap.put(keyTmp, valueTmp.trim().replaceAll("^\"|\"$", "").replaceAll("^\'|\'$", ""));
                    }
                    msg = msg.replaceAll(getValuePackagePattern().pattern(), "").trim();
                }
                // 遍历开始

                    /*String key = null;
                    String value = null;
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < msg.length(); i++) {
                        char c = msg.charAt(i);
                        String nextStr = msg.substring(i + 1);
                        int nextKvIndex = nextStr.indexOf(kvSeparator);
                        int nextFieldIndex = nextStr.indexOf(fieldSeparator);
                        boolean addValue = nextKvIndex < nextFieldIndex || StringUtils.isBlank(nextStr);
                        if (String.valueOf(c).equals(kvSeparator) && key == null) {
                            key = builder.toString();
                            builder.setLength(0);
                            continue;
                        }
                        if (String.valueOf(c).equals(fieldSeparator) && value == null && addValue) {
                            value = builder.toString();
                            builder.setLength(0);
                            if (StringUtils.isNotBlank(key)) {
                                kvMap.put(key, value);
                            }
                            key = null;
                            value = null;
                            continue;
                        }
                        builder.append(c);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(JSON.toJSONString(kvMap));
                    }*/

                // split切割，按字符串切割，不使用正则方式
//                    String[] arrays = msg.split(fieldSeparator);
                String[] arrays = StringUtils.splitByWholeSeparatorPreserveAllTokens(msg, fieldSeparator);
                if (arrays != null) {
                    for (String keyValue : arrays) {
                        int kvsIndex = keyValue.indexOf(getKvSeparator());
                        if (kvsIndex < 0) {
                            continue;
                        }
                        String key = keyValue.substring(0, kvsIndex).trim();
                        String value = keyValue.substring(kvsIndex + 1).trim().replaceAll("^\"|\"$", "").replaceAll("^\"|\"$", "");

//                            String[] kv = keyValue.split(getKvSeparator(), 2);
                        kvMap.put(key, value);
                    }
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
            return super.build(new KvDecomposeParser(), parseRule);
        }
    }

}

