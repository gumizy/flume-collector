package com.datacloudsec.config.conf.parser.event;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class EventDecodeRuleField implements Serializable {

    private Integer id;

    private Integer ruleId;

    private Integer fieldId;

    private String mappingIndex;

    private String defaultValue;

    private Integer seq; // 排序字段

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getMappingIndex() {
        return mappingIndex;
    }

    public void setMappingIndex(String mappingIndex) {
        this.mappingIndex = mappingIndex;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    /**
     * 比较两个配置是否一致
     *
     * @param obj obj
     * @return boolean
     */
    public boolean isSame(EventDecodeRuleField obj) {
        if (obj == null) return false;
        if (this.id == null || obj.getId() == null || this.id.intValue() != obj.getId().intValue()) return false;
        if (this.ruleId == null || obj.getRuleId() == null || this.ruleId.intValue() != obj.getRuleId().intValue())
            return false;
        if (this.fieldId == null || obj.getFieldId() == null || this.fieldId.intValue() != obj.getFieldId().intValue())
            return false;
        if (!StringUtils.equals(this.mappingIndex, obj.getMappingIndex())) return false;
        if (!StringUtils.equals(this.defaultValue, obj.getDefaultValue())) return false;
        if (this.seq == null || obj.getSeq() == null || this.seq.intValue() != obj.getSeq().intValue()) return false;
        return true;
    }

    private List<EventDecodeRuleFieldMapping> valueMappings;

    public List<EventDecodeRuleFieldMapping> getValueMappings() {
        return valueMappings != null ? valueMappings : Collections.emptyList();
    }

    public void setValueMappings(List<EventDecodeRuleFieldMapping> valueMappings) {
        this.valueMappings = valueMappings != null ? valueMappings : Collections.emptyList();
    }
}
