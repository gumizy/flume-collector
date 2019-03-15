package com.datacloudsec.parser.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.core.Parser;
import com.datacloudsec.parser.AbstractParserBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

/**
 * @Author gumizy
 * @Date 2018/6/27 22:35
 */
public class JsonParser extends AbstractParserBuilder {

    private static Logger logger = LoggerFactory.getLogger(JsonParser.class);

    @Override
    protected void setEventField(String sendHost, Event event, Matcher matcher) {
        super.setEventField(sendHost, event, matcher, false);
        String msg = matcher.group(getSourceField());
        if (StringUtils.isNotBlank(msg)) {
            msg = msg.trim();
            JSONObject jsonObject = JSON.parseObject(msg);
            for (FieldParser parser : getFieldParsers()) {
                try {
                    String mappingIdx = parser.getIndex();
                    String field = parser.getField();
                    String defValue = parser.getDefValue();
                    // 避免遇到mappingIdx为null的情况，不处理其它字段
                    if (mappingIdx != null && mappingIdx.trim().length() > 0) {
                        String[] indexes = mappingIdx.split(",");
                        // 对于key-value的方式，不支持多字段group匹配
                        if (indexes.length == 1) {
                            String index = indexes[0];
                            String groupValue = jsonObject.getString(index);
                            if (groupValue != null) {
                                groupValue = groupValue.trim().replaceAll("^\"|\"$", "").replaceAll("^\"|\"$", "");
                            }
                            fieldFormat(sendHost, event, parser, field, defValue, groupValue);
                        } else {
                            setMultiFieldValue(sendHost, event, jsonObject, parser, field, defValue, indexes, false);
                        }
                    } else {
                        event.putValue(field, defValue);
                    }
                } catch (Exception e) {
                    logger.error("{} send log {} field collect error: {}!", sendHost, parser.getField(), e.getMessage());
                }
            }
        }

    }

    protected void setMultiFieldValue(String sendHost, Event event, JSONObject jsonObject, FieldParser parser, String field, String defValue, String[] indexes, boolean groupInt) {
        StringBuilder sb = new StringBuilder();
        for (String index : indexes) {
            try {
                String groupValue = jsonObject.getString(index);
                if (groupValue != null) {
                    groupValue = groupValue.trim().replaceAll("^\"|\"$", "").replaceAll("^\"|\"$", "");
                }
                String value = getRealValue(event, field, parser, groupValue).getValue();
                // 1.优先Mapping匹配值
                mappingValue(sendHost, event, field, defValue, groupValue, sb, value);
            } catch (Exception e) {
                continue;
            }
        }
        String value = "";
        if (sb.length() > 0) {
            value = sb.substring(0, sb.length() - 1);
        }
        if (event.getValue(field) != null) {
            value = event.getValue(field) + value;
        }
        if (value != null) {
            value = value.trim();
        }
        event.putValue(field, value);
    }

    public static class Builder extends AbstractBuilder {

        @Override
        public Parser build(EventDecodeRule parseRule) {
            return super.build(new JsonParser(), parseRule);
        }
    }
}