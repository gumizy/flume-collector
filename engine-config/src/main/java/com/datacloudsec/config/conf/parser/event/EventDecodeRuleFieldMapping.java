package com.datacloudsec.config.conf.parser.event;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class EventDecodeRuleFieldMapping implements Serializable {

    private Integer id;

    private Integer ruleId;

    private Integer fieldId;

    private Integer mappingType; // 1 正则； 2 文本； 3 时间转化；4 IP转化...

    private String originalValue;

    private String mappingValue;

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

    public Integer getMappingType() {
        return mappingType;
    }

    public void setMappingType(Integer mappingType) {
        this.mappingType = mappingType;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getMappingValue() {
        return mappingValue;
    }

    public void setMappingValue(String mappingValue) {
        this.mappingValue = mappingValue;
    }

    /**
     * 比较两个配置是否一致
     *
     * @param obj obj
     * @return boolean
     */
    public boolean isSame(EventDecodeRuleFieldMapping obj) {
        if (obj == null) return false;
        if (this.id == null || obj.getId() == null || this.id.intValue() != obj.getId().intValue()) return false;
        if (this.ruleId == null || obj.getRuleId() == null || this.ruleId.intValue() != obj.getRuleId().intValue())
            return false;
        if (this.fieldId == null || obj.getFieldId() == null || this.fieldId.intValue() != obj.getFieldId().intValue())
            return false;
        if (this.mappingType == null || obj.getMappingType() == null || this.mappingType.intValue() != obj.getMappingType().intValue())
            return false;
        if (!StringUtils.equals(this.originalValue, obj.getOriginalValue())) return false;
        if (!StringUtils.equals(this.mappingValue, obj.getMappingValue())) return false;
        return true;
    }
}
