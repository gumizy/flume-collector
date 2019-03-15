package com.datacloudsec.parser.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.config.conf.parser.collector.Collector;
import com.datacloudsec.config.conf.parser.event.*;
import com.datacloudsec.parser.ParserChain;
import com.datacloudsec.parser.cache.DcBasicConfigCache;
import com.datacloudsec.parser.cache.DcDecodeConfigCache;
import com.datacloudsec.parser.cache.PraserProcessorCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Date 2019/1/21 19:01
 */
public class CollectorProcess implements ParserMetadataProcess {
    private static final Logger logger = LoggerFactory.getLogger(CollectorProcess.class);

    @Override
    public void process(String value) {
        logger.info("CollectorProcess doCollectorProcess start......");
        try {
            if (StringUtils.isNotBlank(value)) {
                JSONObject jsonObject = JSON.parseObject(value);
                String eventTypeStr = jsonObject.getString(EventType.class.getSimpleName());
                String eventFieldStr = jsonObject.getString(EventField.class.getSimpleName());
                String collectorStr = jsonObject.getString(Collector.class.getSimpleName());
                String eventDecodeRuleStr = jsonObject.getString(EventDecodeRule.class.getSimpleName());
                String eventDecodeRuleFieldStr = jsonObject.getString(EventDecodeRuleField.class.getSimpleName());
                String eventDecodeRuleFieldMappingStr = jsonObject.getString(EventDecodeRuleFieldMapping.class.getSimpleName());

                List<EventField> eventFields = JSON.parseArray(eventFieldStr, EventField.class);
                List<EventType> eventTypes = JSON.parseArray(eventTypeStr, EventType.class);
                List<Collector> collector = JSON.parseArray(collectorStr, Collector.class);
                List<EventDecodeRule> eventDecodeRules = JSON.parseArray(eventDecodeRuleStr, EventDecodeRule.class);
                List<EventDecodeRuleField> eventDecodeRuleFields = JSON.parseArray(eventDecodeRuleFieldStr, EventDecodeRuleField.class);
                List<EventDecodeRuleFieldMapping> eventDecodeRuleFieldMappings = JSON.parseArray(eventDecodeRuleFieldMappingStr, EventDecodeRuleFieldMapping.class);

                List<Collector> allCollectors = null;
                if (collector != null) {
                    allCollectors = collector.stream().filter(c -> c.getEnable() == 1).collect(Collectors.toList());
                }
                if (allCollectors != null) {
                    DcDecodeConfigCache.initCollectors(allCollectors);
                } else {
                    DcDecodeConfigCache.initCollectors(Collections.emptyList());
                }

                if (eventTypes != null) {
                    DcDecodeConfigCache.initEventTypes(eventTypes);
                } else {
                    DcDecodeConfigCache.initEventTypes(Collections.emptyList());
                }

                if (eventDecodeRuleFieldMappings != null) {
                    DcDecodeConfigCache.initDecodeRuleFieldMappings(eventDecodeRuleFieldMappings);
                } else {
                    DcDecodeConfigCache.initDecodeRuleFieldMappings(Collections.emptyList());
                }

                if (eventDecodeRules != null) {
                    DcDecodeConfigCache.initDecodeRules(eventDecodeRules);
                } else {
                    DcDecodeConfigCache.initDecodeRules(Collections.emptyList());
                }
                if (eventDecodeRuleFields != null) {
                    DcDecodeConfigCache.initDecodeRuleFields(eventDecodeRuleFields);
                } else {
                    DcDecodeConfigCache.initDecodeRuleFields(Collections.emptyList());
                }

                // 全部字段
                if (eventFields != null) {
                    initBasicConfig(eventFields);
                }
                // 初始化所有解析器
                initAllParser();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("CollectorProcess doCollectorProcess end......");
    }

    private synchronized static void initBasicConfig(List<EventField> eventFields) {
        // 全部字段
        DcBasicConfigCache.initEventFields(eventFields);
    }

    private synchronized static void initAllParser() {
        logger.info("initAllParser...");

        // clear first
        clearAllParser();

        // init then
        Map<String, Collector> collectors = DcDecodeConfigCache.getCollectorMap();
        if (collectors != null && collectors.size() > 0) {
            for (Collector collector : collectors.values()) {
                reloadParser(collector);
            }
        }
        logger.info("initAllParser end!");
    }

    /**
     * 重新同步规则时调用
     */
    private static synchronized void clearAllParser() {
        PraserProcessorCache.getValidateIpSet().clear();
        PraserProcessorCache.clearAllParserChain();
    }

    private static synchronized void reloadParser(Collector collector) {
        // 增加新的解析规则链Stopping lifecycle supervisor
        ParserChain parserChain = new ParserChain();
        parserChain.addParser(DcDecodeConfigCache.getDecodeRuleByIdentification(collector.getIdentification()), collector.getId());

        String charset = collector.getLogEncoding();
        String identification = collector.getIdentification();
        if (charset != null) {
            parserChain.setCharset(Charset.forName(charset));
        }
        PraserProcessorCache.addParserChain(identification, parserChain);
        PraserProcessorCache.setValidateIpSet(collector.getIp());
    }

}
