package com.datacloudsec.parser.parser;

import com.datacloudsec.config.Event;
import com.datacloudsec.parser.AbstractParserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Date 2019/1/18 17:14
 */
public abstract class AbstractKvParser extends AbstractParserBuilder {
    private final static Logger logger = LoggerFactory.getLogger(AbstractKvParser.class);

    protected void setKvField(String sendHost, Event event, Map<String, String> kvMap) {
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
                        String groupValue = kvMap.get(index);
                        if (groupValue != null) {
                            groupValue = groupValue.trim();
                        }
                        fieldFormat(sendHost, event, parser, field, defValue, groupValue);
                    } else {
                        setMultiFieldValue(sendHost, event, kvMap, parser, field, defValue, indexes, false);
                    }
                } else {
                    event.putValue(field, defValue);
                }
            } catch (Exception e) {
                logger.error("{} send log {} field collect error: {}!", sendHost, parser.getField(), e.getMessage());
            }
        }
    }

    protected void setMultiFieldValue(String sendHost, Event event, Map<String, String> kvMap, FieldParser parser, String field, String defValue, String[] indexes, boolean groupInt) {
        StringBuilder sb = new StringBuilder();
        for (String string : indexes) {
            try {
                String groupValue = null;
                if (!"0".equals(string)) {
                    groupValue = kvMap.get(string);
                }
                if (groupValue != null) {
                    groupValue = groupValue.trim().replaceAll("^\"|\"$", "");
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

}
