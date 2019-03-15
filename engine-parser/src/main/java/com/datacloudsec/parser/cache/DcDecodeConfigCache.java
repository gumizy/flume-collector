package com.datacloudsec.parser.cache;

import com.datacloudsec.config.conf.parser.collector.Collector;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.config.conf.parser.event.EventDecodeRuleField;
import com.datacloudsec.config.conf.parser.event.EventDecodeRuleFieldMapping;
import com.datacloudsec.config.conf.parser.event.EventType;
import com.datacloudsec.config.tools.IdKit;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.COLLECT_TYPE_SEPARATOR;

/**
 * DcDecodeConfigCache
 * 用于存储数据采集相关的配置，包括：
 * 1. 采集器列表：用于记录每个采集器IP用何种解析规则解析
 * 2. 数据解析规则：用于记录解析规则相关的配置
 * 3. 数据解析规则-事件字段对应列表：用于记录事件字段对应的解析值
 * 4. 数据解析规则-事件字段-值映射列表：用于记录事件字段的特殊值转化
 *
 * @author gumizy 2017/7/28
 */
public class DcDecodeConfigCache implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(DcDecodeConfigCache.class);

    // 内部关系映射，方便根据采集器IP+CollectorType获取对应的规则
    private static Map<String, Collector> collectorMap = Collections.emptyMap(); // 采集器，key为采集器IP+type
    private static Map<String, List<EventDecodeRule>> collectorRulesMap = Collections.emptyMap(); //采集规则， key为采集器IP+type
    private static Map<Integer, List<EventDecodeRuleField>> ruleFieldsMap = Collections.emptyMap(); // 规则字段，key为ruleId
    private static Map<String, List<EventDecodeRuleFieldMapping>> ruleFieldsMappingMap = Collections.emptyMap(); // 规则映射，key为ruleId-Field
    // 事件类型列表（需初始化设定）
    private static List<EventType> allEventTypes = Collections.emptyList();

    /**
     * 初始化采集器列表
     *
     * @param collectors collectors
     */
    public synchronized static void initCollectors(List<Collector> collectors) {
        if (collectors == null) {
            logger.error("Collectors is null, please check!");
            return;
        }
        DcDecodeConfigCache.collectorMap = collectors.stream().map(e -> {
            e.setIdentification(e.getIp() + COLLECT_TYPE_SEPARATOR + e.getCollectType());
            return e;
        }).collect(Collectors.toMap(Collector::getIdentification, e -> e));
    }

    /**
     * 初始化采集器对应解析规则列表
     *
     * @param rules rules
     */
    public synchronized static void initDecodeRules(List<EventDecodeRule> rules) {
        if (rules == null) {
            logger.error("Rules is null, please check!");
            return;
        }
        Map<Integer, EventDecodeRule> tmpMap = rules.stream().collect(Collectors.toMap(EventDecodeRule::getId, e -> e));

        DcDecodeConfigCache.collectorRulesMap = collectorMap.values().stream().collect(Collectors.toMap(Collector::getIdentification, e -> {
            String ids = e.getEventDecodeRuleIds();
            List<Integer> idList = IdKit.toIntegerList(ids);
            return idList.stream().filter(tmpMap::containsKey).map(tmpMap::get).collect(Collectors.toList());
        }));
    }

    /**
     * 初始化解析规则-字段列表
     *
     * @param ruleFields ruleFields
     */
    public synchronized static void initDecodeRuleFields(List<EventDecodeRuleField> ruleFields) {
        if (ruleFields == null) {
            logger.error("ruleFields is null, please check!");
            return;
        }
        Map<Integer, List<EventDecodeRuleField>> map = new HashMap<>();
        for (EventDecodeRuleField rf : ruleFields) {
            Integer key = rf.getRuleId();
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            // 设置值映射
            String valueMapKey = rf.getRuleId() + "-" + rf.getFieldId();
            rf.setValueMappings(ruleFieldsMappingMap.containsKey(valueMapKey) ? ruleFieldsMappingMap.get(valueMapKey) : Collections.emptyList());
            map.get(key).add(rf);
        }
        DcDecodeConfigCache.ruleFieldsMap = map;
    }

    /**
     * 初始化解析规则-字段值映射
     *
     * @param mappings mappings
     */
    public synchronized static void initDecodeRuleFieldMappings(List<EventDecodeRuleFieldMapping> mappings) {
        Map<String, List<EventDecodeRuleFieldMapping>> valueMap = new HashMap<>();
        if (CollectionUtils.isEmpty(mappings)) {
            logger.warn("All rule fields mapping is empty!");
        } else {
            for (EventDecodeRuleFieldMapping rfm : mappings) {
                String key = rfm.getRuleId() + "-" + rfm.getFieldId();
                if (!valueMap.containsKey(key)) {
                    valueMap.put(key, new ArrayList<>());
                }
                valueMap.get(key).add(rfm);
            }
        }
        DcDecodeConfigCache.ruleFieldsMappingMap = valueMap;
    }

    /**
     * 初始化事件类型-字段值映射
     *
     * @param types types
     */
    public synchronized static void initEventTypes(List<EventType> types) {
        if (types == null) {
            logger.error("rule field mappings is null, please check!");
            return;
        }
        DcDecodeConfigCache.allEventTypes = types;
    }

    /**
     * 根据ID获取事件类型
     *
     * @param id event_type_id
     */
    public static EventType getEventTypeById(Integer id) {
        for (EventType type : allEventTypes) {
            if (id.equals(type.getId())) {
                return type;
            }
        }

        return null;
    }

    /**
     * 根据identification查找对应的解析规则。注意此identification为上报设备的identification。
     * 若存在解析规则，返回对应的规则
     * 若不存在，返回null
     *
     * @param identification identification
     * @return EventDecodeRule
     */
    public static List<EventDecodeRule> getDecodeRuleByIdentification(String identification) {
        List<EventDecodeRule> list = DcDecodeConfigCache.collectorRulesMap.get(identification);
        return list != null ? list : Collections.emptyList();
    }

    /**
     * 根据identification查找对应的采集器配置。注意此identification为上报设备的identification。
     * 若存在解析规则，返回对应的规则
     * 若不存在，返回null
     *
     * @param identification identification
     * @return Collector
     */
    public static Collector getCollectorByIdentification(String identification) {
        return collectorMap.get(identification);
    }

    /**
     * 根据Id加载所有应该解析的字段。注意此Id为上报设备的Id。
     *
     * @param id rule_id
     * @return List
     */
    public static List<EventDecodeRuleField> getRuleFieldsByRuleId(Integer id) {
        List<EventDecodeRuleField> list = DcDecodeConfigCache.ruleFieldsMap.get(id);
        return list != null ? list : Collections.emptyList();
    }

    public static Map<String, Collector> getCollectorMap() {
        return collectorMap;
    }

}
