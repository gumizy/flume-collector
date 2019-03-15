package com.datacloudsec.parser.cache;

import com.datacloudsec.config.conf.parser.event.EventField;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DcBasicConfig
 * 用于存储数据采集的基础配置，包括：
 * 1. 系统内置的所有字段列表
 *
 * @author gumizy 2017/7/28
 */
public class DcBasicConfigCache implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(DcBasicConfigCache.class);

    // 事件字段列表
    private static List<EventField> eventFields = Collections.emptyList();
    private static Map<Integer, EventField> eventFieldMap = Collections.emptyMap();

    /**
     * 初始化系统内置的event field
     *
     * @param eventFields eventFields
     */
    public synchronized static void initEventFields(List<EventField> eventFields) {
        if (CollectionUtils.isEmpty(eventFields)) {
            logger.error("Event fields is empty, please check!");
            return;
        }
        logger.info("Event fields size is {}!", eventFields.size());
        DcBasicConfigCache.eventFields = eventFields;
        DcBasicConfigCache.eventFieldMap = DcBasicConfigCache.eventFields.stream().collect(Collectors.toMap(EventField::getId, e -> e));
    }

    /**
     * 根据事件ID查询EventField
     *
     * @param id id
     * @return EventField
     */
    public static EventField getEventFieldById(Integer id) {
        if (id == null) return null;
        return eventFieldMap.get(id);
    }
}
