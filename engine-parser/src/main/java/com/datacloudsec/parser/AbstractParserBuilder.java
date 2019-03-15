package com.datacloudsec.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.config.ContextKit;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.config.conf.parser.event.EventDecodeRuleField;
import com.datacloudsec.config.conf.parser.event.EventDecodeRuleFieldMapping;
import com.datacloudsec.config.conf.parser.event.EventType;
import com.datacloudsec.config.event.EventConstants;
import com.datacloudsec.config.tools.DateUtil;
import com.datacloudsec.config.tools.KeyValuePairs;
import com.datacloudsec.config.tools.TimeKit;
import com.datacloudsec.config.tools.UUIDUtil;
import com.datacloudsec.core.Parser;
import com.datacloudsec.core.ProcessException;
import com.datacloudsec.core.parser.AbstractParser;
import com.datacloudsec.parser.cache.DcBasicConfigCache;
import com.datacloudsec.parser.cache.DcDecodeConfigCache;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.EVENT_INDEX_TYPE;

/**
 * AbstractParserBuilder
 */
public abstract class AbstractParserBuilder extends AbstractParser {

    private static Logger logger = LoggerFactory.getLogger(AbstractParserBuilder.class);

    protected final static String EQUALS_ANY = "*";
    protected final static String MATCHER_ANY = ".*";
    protected final static Integer ONE_LINE = 1;

    private String name;

    private Pattern pattern;

    private String eventType;

    private String keyFields;

    private Integer ruleId;

    private String eventContent;

    private List<FieldParser> fieldParsers;

    private Pattern fieldPattern = Pattern.compile("(\\$\\{(\\w+)})");

    // 正则解析
    private String sourceField;
    private Integer decodeType;
    private String fieldSeparator;
    private String kvSeparator;
    private Pattern secPattern;
    private String multilineSeparator;
    private Integer lineNum;
    private Pattern valuePackagePattern;
    private String type;

    public String getMultilineSeparator() {
        return multilineSeparator;
    }

    public void setMultilineSeparator(String multilineSeparator) {
        this.multilineSeparator = multilineSeparator;
    }

    public Pattern getSecPattern() {
        return secPattern;
    }

    public void setSecPattern(Pattern secPattern) {
        this.secPattern = secPattern;
    }

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public String getKvSeparator() {
        return kvSeparator;
    }

    public void setKvSeparator(String kvSeparator) {
        this.kvSeparator = kvSeparator;
    }

    public Pattern getFieldPattern() {
        return fieldPattern;
    }

    public void setFieldPattern(Pattern fieldPattern) {
        this.fieldPattern = fieldPattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(String keyFields) {
        this.keyFields = keyFields;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    public List<FieldParser> getFieldParsers() {
        return fieldParsers;
    }

    public void setFieldParsers(List<FieldParser> fieldParsers) {
        this.fieldParsers = fieldParsers;
    }

    public Pattern getValuePackagePattern() {
        return valuePackagePattern;
    }

    public void setValuePackagePattern(Pattern valuePackagePattern) {
        this.valuePackagePattern = valuePackagePattern;
    }

    // 是否为多行日志
    public boolean isMultilineLog() {
        return this.lineNum != null && this.lineNum > ONE_LINE;
    }

    @Override
    public List<Event> parse(String sendHost, String logContents) throws ProcessException {
        /* 单行日志 or 多行日志 */
        if (!isMultilineLog()) { // 单行
            Matcher matcher = getPattern().matcher(logContents);
            Event event = setEventFields(sendHost, logContents, matcher);
            if (event == null) {
                return Collections.emptyList();
            } else {
                event.valuesDefault();
                return Collections.singletonList(event);
            }
        } else { // 多行
            final List<Event> events = new ArrayList<>();
            String[] multilineLog = getMultilineSeparator() != null ? logContents.split(getMultilineSeparator()) : new String[]{logContents};
            for (String logContent : multilineLog) {
                Matcher matcher = getPattern().matcher(logContent);
                Event event = setEventFields(sendHost, logContent, matcher);
                if (event != null) {
                    event.valuesDefault();
                    events.add(event);
                }
            }
            return events;
        }
    }

    private Event setEventFields(String sendHost, String logContent, Matcher matcher) {
        Event event = null;
        if (matcher.find()) {
            event = new Event();
            // 设置通用配置信息
            setCommonField(sendHost, logContent, event);
            // 正则解析设置字段
            setEventField(sendHost, event, matcher);
            // 事件分类
            setEventType(event);
            // 事件内容
            setEventContent(event);
            // 流量、包设置
            setBytePackage(event);
//            if (ContextKit.isDevMode()) {
//                logger.info("[SUCCESS] sendHost: {},\nlog: {},\npattern: {}", sendHost, logContent, getPattern().pattern());
//            } else {
//                logger.debug("[SUCCESS] sendHost: {},\nlog: {},\npattern: {}", sendHost, logContent, getPattern().pattern());
//            }
        } else {
            if (ContextKit.isDevMode()) {
                logger.warn("[FAILED] sendHost: {},\nlog: {},\npattern: {}", sendHost, logContent, getPattern().pattern());
            } else {
                logger.debug("[FAILED] sendHost: {},\nlog: {},\npattern: {}", sendHost, logContent, getPattern().pattern());
            }
        }
        return event;
    }

    protected void setBytePackage(Event event) {
        long receiveByte = event.getReceiveByte() == null ? 0 : event.getReceiveByte();
        long sendByte = event.getSendByte() == null ? 0 : event.getSendByte();
        event.setTotalByte(sendByte + receiveByte);
        long receivePackage = event.getReceivePackage() == null ? 0 : event.getReceivePackage();
        long sendPackage = event.getSendPackage() == null ? 0 : event.getSendPackage();
        event.setTotalPackage(sendPackage + receivePackage);
    }

    protected abstract void setEventField(String sendHost, Event event, Matcher matcher);

    protected void setEventField(String sendHost, Event event, Matcher matcher, boolean groupInt) {
        try {
            if (getFieldParsers() != null) {
                for (FieldParser parser : getFieldParsers()) {
                    try {
                        String mappingIdx = parser.getIndex();
                        String field = parser.getField();
                        String defValue = parser.getDefValue();
                        // 避免遇到mappingIdx为null的情况，不处理其它字段
                        if (mappingIdx != null && mappingIdx.trim().length() > 0) {
                            String[] indexes = mappingIdx.split(",");
                            String groupValue = null;
                            // 对于key-value的方式，不支持多字段group匹配
                            if (indexes.length == 1) {
                                String index = indexes[0];
                                if (groupInt) {
                                    int idx = NumberUtils.toInt(index);
                                    if (idx > 0) {
                                        groupValue = matcher.group(idx);
                                    }
                                } else {
                                    if (StringUtils.isNotBlank(index) && !"0".equals(index)) {
                                        groupValue = matcher.group(index);
                                    }
                                }
                                // 对匹配的结果进行两端去空
                                if (groupValue != null) {
                                    groupValue = groupValue.trim().replaceAll("^\"|\"$", "");
                                }
                                fieldFormat(sendHost, event, parser, field, defValue, groupValue);
                            } else {
                                setMultiFieldValue(sendHost, event, matcher, parser, field, defValue, indexes, groupInt);
                            }
                        } else {
                            event.putValue(field, defValue);
                        }
                    } catch (Exception e) {
                        if (logger.isDebugEnabled() && !groupInt)
                            logger.debug(String.format("%s send log %s field collect error: ", sendHost, parser.getField()), e);
                    }
                }

            } else {
                if (logger.isDebugEnabled()) logger.debug("this is warn: {} type rule collect is null", getEventType());
            }
        } catch (Throwable e) {
            if (logger.isDebugEnabled() || ContextKit.isDevMode())
                logger.error(String.format("%s send log %s rule collect error: ", sendHost, getName()), e);
        }
    }

    protected KeyValuePairs.KeyValuePair getRealValue(Event event, String field, FieldParser parser, String value) {
        String result;
        MappingParser emp = null;
        MappingParser rmp = null;
        MappingParser tmp = null;
        MappingParser trmp = null;
        MappingParser kv = null;
        Map<FieldMappingType, MappingParser> mappings = parser.getMappingParsers();
        if (mappings != null) {
            emp = mappings.get(FieldMappingType.EQUAL);
            rmp = mappings.get(FieldMappingType.REGULAR);
            tmp = mappings.get(FieldMappingType.TIME);
            trmp = mappings.get(FieldMappingType.TRANSFORM);
            kv = mappings.get(FieldMappingType.KV);
        }
        KeyValuePairs.KeyValuePair keyValuePair = new KeyValuePairs.KeyValuePair();
        if (value != null && value.length() > 0) {
            if (tmp != null) {
                SimpleDateFormat df = tmp.getDateFormat();
                try {
                    keyValuePair.setValue(String.valueOf(DateUtil.format(df, value).getTime()));
                    return keyValuePair;
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) logger.error("format time error: ", e);
                }
            }
            if (trmp != null) {
                Map<String, String> tMap = trmp.getTransformMap();
                String v = tMap.get(field);
                if (v != null) {
                    keyValuePair.setValue(event.getString(v));
                    return keyValuePair;
                }
            }
            if (emp != null) {
                Map<String, String> eqMap = emp.getEqualMap();
                result = eqMap.get(value);
                if (result != null) {
                    keyValuePair.setValue(result);
                    return keyValuePair;
                }
            }
            if (rmp != null) {
                Map<Pattern, String> pMap = rmp.getPatternMap();
                if (pMap != null) {
                    Iterator<Pattern> pIt = pMap.keySet().iterator();
                    while (pIt.hasNext()) {
                        Pattern pattern = pIt.next();
                        Matcher m = pattern.matcher(value);
                        if (m.find()) {
                            keyValuePair.setValue(pMap.get(pattern));
                            return keyValuePair;
                        }
                    }
                }
            }
            if (kv != null) {
                Map<String, String> kvMap = kv.getKvMap();
                Map<String, String> keyValueMap = parseKeyValue(value, parser.getSpliter());
                if (kvMap != null) {
                    Iterator<Map.Entry<String, String>> iterator = kvMap.entrySet().iterator();
                    Set<String> keySet = kvMap.keySet();
                    while (iterator.hasNext()) {
                        try {
                            Map.Entry<String, String> next = iterator.next();
                            String key = next.getKey();
                            if (keySet.contains(key)) {
                                String nKey = kvMap.get(key);
                                if (!StringUtils.isEmpty(nKey)) {
                                    event.putValue(nKey, keyValueMap.get(key));
                                } else {
                                    event.putValue(key, keyValueMap.get(key));
                                }
                            } else {
                                event.putValue(key, keyValueMap.get(key));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                keyValuePair.setKey(field);
                keyValuePair.setValue(value);
                return keyValuePair;
            }
        }
        if (parser.isEquals()) {
            keyValuePair.setValue(parser.getEqualsValue());
            return keyValuePair;
        }
        if (parser.isMatcher()) {
            keyValuePair.setValue(parser.getMatcherValue());
            return keyValuePair;
        }
        return keyValuePair;
    }

    private Map<String, String> parseKeyValue(String json, String spliter) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] split = json.split("\\\\r\\\\n");
        String currentParent = "";
        for (String string : split) {
            String line = string.trim();
            if (line.length() > 0 && !string.matches("\\\\t.*")) {
                String[] keyValue = line.split(spliter);
                if (keyValue.length > 1) {
                    map.put(keyValue[0].trim().replaceAll("\\\\t", ""), keyValue[1].trim().replaceAll("\\\\t", ""));
                } else {
                    map.put(keyValue[0].trim().replaceAll("\\\\t", ""), "");
                }
                currentParent = keyValue[0] + ":";
            } else if (!StringUtils.isEmpty(line)) {
                String[] keyValue = line.split(spliter);
                if (keyValue.length > 1) {
                    map.put(currentParent + keyValue[0].trim().replaceAll("\\\\t", ""), keyValue[1].trim().replaceAll("\\\\t", ""));
                } else {
                    map.put(currentParent + keyValue[0].trim().replaceAll("\\\\t", ""), "");
                }
            }
        }
        return map;
    }

    protected void setCommonField(String sendHost, String logContent, Event event) {
        event.setEventUUID(UUIDUtil.generateShortUUID());
        event.setReceiveTime(System.currentTimeMillis());
        event.setIndexType(EVENT_INDEX_TYPE);
        event.setOriginalLog(logContent);
        event.setCollectorAddress(sendHost);
        event.putValue(EventConstants.DEVADDRESS, sendHost);
        event.setRuleId(getRuleId());
    }

    protected void setMultiFieldValue(String sendHost, Event event, Matcher matcher, FieldParser parser, String field, String defValue, String[] indexes, boolean groupInt) {
        StringBuilder sb = new StringBuilder();
        for (String string : indexes) {
            String groupValue = null;
            try {
                if (groupInt) {
                    Integer group = Integer.valueOf(string);
                    if (group != 0) {
                        groupValue = matcher.group(group);
                    }
                } else {
                    if (!"0".equals(string)) {
                        groupValue = matcher.group(string);
                    }
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
        if (value != null) {
            value = value.trim();
        }
        event.putValue(field, value);
    }

    protected void mappingValue(String sendHost, Event event, String field, String defValue, String groupValue, StringBuilder sb, String value) {
        if (defValue != null && defValue.equals("1") && field.equals("src_address")) {
            event.putValue(field, sendHost);
        } else if (value != null) {
            sb.append(value);
        } else {
            // 2.匹配析取变量值
            if (groupValue != null && groupValue.length() > 0) {
                sb.append(groupValue);
            } else {
                // 3.最后匹配默认值
                sb.append(defValue != null ? defValue : "");
            }
        }
        sb.append(" ");
    }

    protected void fieldFormat(String sendHost, Event event, FieldParser parser, String field, String defValue, String groupValue) {
        KeyValuePairs.KeyValuePair keyValuePair = getRealValue(event, field, parser, groupValue);

        if (!StringUtils.isEmpty(keyValuePair.getKey())) {
            field = keyValuePair.getKey();
        }
        String value = keyValuePair.getValue();
        //1.优先Mapping匹配值
        if (value != null) {
            event.putValue(field, value);
        } else {
            // 1. time format
            if (EventConstants.TIME2LONG_SET.contains(field)) {
                long timeValue = TimeKit.timeString2Long(groupValue);
                if (timeValue > 0) {
                    event.putValue(field, String.valueOf(timeValue));
                } else {
                    event.putValue(field, groupValue);
                }
            } else {
                // 2.匹配析取变量值
                if (groupValue != null && groupValue.length() > 0) {
                    event.putValue(field, groupValue);
                } else {
                    // 3.最后匹配默认值
                    if (defValue != null && defValue.equals("1") && (field.equals("src_address") || field.equals("dst_address"))) {
                        if (StringUtils.isBlank(event.getSrcAddress())) {
                            event.putValue(field, sendHost);
                        }
                    } else {
                        event.putValue(field, defValue);
                    }
                }
            }
        }
    }

    protected void setEventContent(Event event) {
        String ec = getEventContent();
        if (ec != null) {
            // 先用${field}的方式给事件内容赋值
            Matcher m = getFieldPattern().matcher(ec);
            while (m.find()) {
                String replace = m.group(1);
                String field = m.group(2);
                Object value = event.getValue().get(field);
                if (value == null) value = "";
                ec = ec.replace(replace, value.toString());
            }
            event.setEventContent(ec);
        }
    }

    protected void setEventType(Event event) {
        String et = event.getEventType();
        if (et == null) event.setEventType(getEventType());
    }

    public static abstract class AbstractBuilder implements Builder {

        protected Parser build(AbstractParserBuilder parser, EventDecodeRule parseRule) {
            // 根据这个parseRule来写正则表达式
            try {
                // id & name
                parser.setRuleId(parseRule.getId());
                parser.setName(parseRule.getRuleName());
                //设置解析的字段
                parser.setSourceField(parseRule.getSourceField());
                // 1 普通分隔符  1 正则分隔符
                // 普通分隔符
                parser.setKvSeparator(parseRule.getKvSeparator());
                parser.setFieldSeparator(parseRule.getFieldSeparator());
                // 正则分隔符
                String kv_regexp = parseRule.getKvRegexp();
                // 设置message regexp
                if (StringUtils.isNotBlank(kv_regexp)) {
                    parser.setSecPattern(Pattern.compile(kv_regexp));
                }
                // multilineSeparator
                parser.setMultilineSeparator(parseRule.getMultilineSeparator());
                parser.setLineNum(parseRule.getLineNum());
                // regex
                String regex = parseRule.getRegex();
                Pattern pattern = Pattern.compile(regex);
                parser.setPattern(pattern);
                if (StringUtils.isNotBlank(parseRule.getValuePackageRegexp())) {
                    parser.setValuePackagePattern(Pattern.compile(parseRule.getValuePackageRegexp()));
                }
                // eventType
                Integer eventTypeId = parseRule.getEventTypeId();
                EventType eventType = DcDecodeConfigCache.getEventTypeById(eventTypeId);
                if (eventType != null) {
                    parser.setKeyFields(eventType.getKeyFields());
                    parser.setEventType(eventType.getEventTypeName());
                } else {
                    logger.warn("DefaultParser eventType is null for id {}!", eventTypeId);
                }

                // rule fields
                final List<FieldParser> fieldParsers = new ArrayList<>();
                for (EventDecodeRuleField ruleField : DcDecodeConfigCache.getRuleFieldsByRuleId(parseRule.getId())) {
                    final FieldParser fieldParser = new FieldParser();
                    // index & field key
                    String index = ruleField.getMappingIndex();
                    fieldParser.setIndex(index);
                    String fieldKey = DcBasicConfigCache.getEventFieldById(ruleField.getFieldId()).getEventFieldKey();
                    fieldParser.setField(fieldKey);
                    // default value
                    String defValue = ruleField.getDefaultValue();
                    fieldParser.setDefValue(defValue);
                    // value mapping
                    Map<FieldMappingType, MappingParser> mappingTypeMap = new HashMap<>();
                    fieldParser.setMappingParsers(mappingTypeMap);
                    for (EventDecodeRuleFieldMapping mapping : ruleField.getValueMappings()) {
                        FieldMappingType type = FieldMappingType.getEnumByValue(mapping.getMappingType());
                        MappingParser mappingParser = mappingTypeMap.get(type);
                        if (mappingParser == null) {
                            mappingParser = new MappingParser();
                            mappingTypeMap.put(type, mappingParser);
                        }
                        String orgValue = mapping.getOriginalValue();
                        String mappingValue = "";
                        Object value = mapping.getMappingValue();
                        if (value != null) mappingValue = value.toString();
                        if (type == FieldMappingType.EQUAL) {
                            Map<String, String> equalMap = mappingParser.getEqualMap();
                            if (equalMap == null) {
                                equalMap = new HashMap<>();
                                mappingParser.setEqualMap(equalMap);
                            }
                            if (EQUALS_ANY.equals(orgValue.trim())) {
                                fieldParser.setEquals(true);
                                fieldParser.setEqualsValue(mappingValue);
                            } else {
                                equalMap.put(orgValue, mappingValue);
                            }
                        } else if (type == FieldMappingType.REGULAR) {
                            Map<Pattern, String> patternMap = mappingParser.getPatternMap();
                            if (patternMap == null) {
                                patternMap = new HashMap<>();
                                mappingParser.setPatternMap(patternMap);
                            }
                            if (MATCHER_ANY.equals(orgValue.trim())) {
                                fieldParser.setMatcher(true);
                                fieldParser.setMatcherValue(mappingValue);
                            } else {
                                patternMap.put(Pattern.compile(orgValue), mappingValue);
                            }
                        } else if (type == FieldMappingType.TIME) {
                            SimpleDateFormat dateFormat;
                            if (mappingValue.contains(BasicConfigurationConstants.DATEFOMAT_TIMEZONE_SEPERATOR)) {
                                String[] formatterArray = mappingValue.split(BasicConfigurationConstants.DATEFOMAT_TIMEZONE_SEPERATOR);
                                dateFormat = new SimpleDateFormat(formatterArray[0].trim());
                                dateFormat.setTimeZone(TimeZone.getTimeZone(formatterArray[0].trim()));
                            } else {
                                dateFormat = new SimpleDateFormat(mappingValue);
                            }
                            mappingParser.setDateFormat(dateFormat);
                        } else if (type == FieldMappingType.TRANSFORM) {
                            Map<String, String> trMap = mappingParser.getTransformMap();
                            if (trMap == null) {
                                trMap = new HashMap<>();
                                mappingParser.setTransformMap(trMap);
                            }
                            trMap.put(orgValue, mappingValue);
                        } else if (type == FieldMappingType.KV) {
                            // key-value键值对的方式进行数据的解析处理
                            Map<String, String> kvMap = mappingParser.getKvMap();
                            if (kvMap == null) {
                                kvMap = new HashMap<>();
                                mappingParser.setKvMap(kvMap);
                                fieldParser.setSpliter(orgValue);
                            }
                            try {
                                // 需要check!
                                Object mappingValue2 = mapping.getMappingValue();
                                JSONArray fromObject = JSONArray.parseArray(JSON.toJSONString(mappingValue2));
                                kvMap.putAll(kvParser(fromObject.toString()));
                            } catch (Exception e) {
                                logger.error("original mapping value casted to map exception, with original key value information: {}", mappingValue);
                            }
                        }
                    }
                    // Mapping end
                    fieldParsers.add(fieldParser);
                }
                parser.setFieldParsers(fieldParsers);
            } catch (Exception e) {
                logger.error("transform parser error: " + e.getMessage(), e);
            }
            return parser;
        }

        private static Map<String, String> kvParser(String kvContext) {
            Map<String, String> resultMap = new HashMap<>();
            JSONArray jsonArr = JSONArray.parseArray(kvContext);
            for (Object object : jsonArr) {
                JSONObject jsonObj = (JSONObject) object;
                resultMap.put((String) jsonObj.get("key"), (String) jsonObj.get("value"));
            }
            return resultMap;
        }
    }

    protected static class FieldParser {

        private String index;

        private String field;

        private boolean equals;

        private String equalsValue;

        private boolean matcher;

        private String matcherValue;

        private String defValue;

        private String spliter;

        private Map<FieldMappingType, MappingParser> mappingParsers;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public boolean isEquals() {
            return equals;
        }

        public void setEquals(boolean equals) {
            this.equals = equals;
        }

        public String getEqualsValue() {
            return equalsValue;
        }

        public void setEqualsValue(String equalsValue) {
            this.equalsValue = equalsValue;
        }

        public boolean isMatcher() {
            return matcher;
        }

        public void setMatcher(boolean matcher) {
            this.matcher = matcher;
        }

        public String getMatcherValue() {
            return matcherValue;
        }

        public void setMatcherValue(String matcherValue) {
            this.matcherValue = matcherValue;
        }

        public String getDefValue() {
            return defValue;
        }

        public void setDefValue(String defValue) {
            this.defValue = defValue;
        }

        public Map<FieldMappingType, MappingParser> getMappingParsers() {
            return mappingParsers;
        }

        public void setMappingParsers(Map<FieldMappingType, MappingParser> mappingParsers) {
            this.mappingParsers = mappingParsers;
        }

        /**
         * @return the spliter
         */
        public String getSpliter() {
            return spliter;
        }

        /**
         * @param spliter the spliter to set
         */
        public void setSpliter(String spliter) {
            this.spliter = spliter;
        }
    }

    static class MappingParser {

        private SimpleDateFormat dateFormat;

        private Map<Pattern, String> patternMap;

        private Map<String, String> equalMap;

        private Map<String, String> transformMap;

        private Map<String, String> kvMap;

        public SimpleDateFormat getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(SimpleDateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        public Map<Pattern, String> getPatternMap() {
            return patternMap;
        }

        public void setPatternMap(Map<Pattern, String> patternMap) {
            this.patternMap = patternMap;
        }

        public Map<String, String> getEqualMap() {
            return equalMap;
        }

        public void setEqualMap(Map<String, String> equalMap) {
            this.equalMap = equalMap;
        }

        public Map<String, String> getTransformMap() {
            return transformMap;
        }

        public void setTransformMap(Map<String, String> transformMap) {
            this.transformMap = transformMap;
        }

        /**
         * @return the kvMap
         */
        public Map<String, String> getKvMap() {
            return kvMap;
        }

        /**
         * @param kvMap the kvMap to set
         */
        public void setKvMap(Map<String, String> kvMap) {
            this.kvMap = kvMap;
        }

    }

    public enum FieldMappingType {

        EQUAL(1), REGULAR(2), TIME(3), TRANSFORM(4), KV(5);

        private Integer value;

        FieldMappingType(int value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public static FieldMappingType getEnumByValue(int value) {
            for (FieldMappingType fmt : FieldMappingType.values()) {
                if (fmt.getValue() == value) {
                    return fmt;
                }
            }
            return null;
        }
    }
}