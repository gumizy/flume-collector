package com.datacloudsec.config.conf.parser.event;

import java.io.Serializable;
import java.util.Date;

/**
 * EventDecodeRule
 *
 * @author gumizy 2017/7/6
 */
public class EventDecodeRule implements Serializable {

    private Integer id;

    private String ruleName;

    private String description;

    private Integer eventTypeId;

    private String eventTypeName;

    private Integer assetTypeId;

    private String assetTypeName;

    private String originLog; //原始日志样例

    private Integer decodeType;

    private Integer matchCount;

    private String regex;

    private String fieldSeparator;

    private String kvSeparator;

    private Date insertTime;

    private Date updateTime;

    private String adminName;

    private String extraInfo;
    //add by gumizy 20180519 添加正则匹配
    private String kvRegexp;
    private String multilineSeparator;
    private String sourceField;
    private Integer lineNum;
    private String valuePackageRegexp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Integer getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Integer assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public String getOriginLog() {
        return originLog;
    }

    public void setOriginLog(String originLog) {
        this.originLog = originLog;
    }

    public Integer getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(Integer decodeType) {
        this.decodeType = decodeType;
    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
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

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getKvRegexp() {
        return kvRegexp;
    }

    public void setKvRegexp(String kvRegexp) {
        this.kvRegexp = kvRegexp;
    }

    public String getMultilineSeparator() {
        return multilineSeparator;
    }

    public void setMultilineSeparator(String multilineSeparator) {
        this.multilineSeparator = multilineSeparator;
    }

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public String getValuePackageRegexp() {
        return valuePackageRegexp;
    }

    public void setValuePackageRegexp(String valuePackageRegexp) {
        this.valuePackageRegexp = valuePackageRegexp;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }
}
