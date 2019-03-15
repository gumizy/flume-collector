package com.datacloudsec.config.conf.parser.collector;

import com.datacloudsec.config.conf.parser.event.EventDecodeRule;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Collector
 *
 * @author gumizy 2017/6/23
 */
public class Collector implements Serializable {

    private Integer id;

    private Integer port;

    private String name;

    private Integer enable;

    private String ip;

    private String logEncoding;

    private Integer collectType;

    private String eventDecodeRuleIds;

    private String eventDecodeRuleNames;

    private Date insertTime;

    private Date updateTime;

    private String adminName;

    private String extraInfo;

    private List<EventDecodeRule> eventDecodeRules;

    private String identification;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLogEncoding() {
        return logEncoding;
    }

    public void setLogEncoding(String logEncoding) {
        this.logEncoding = logEncoding;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public String getEventDecodeRuleIds() {
        return eventDecodeRuleIds;
    }

    public void setEventDecodeRuleIds(String eventDecodeRuleIds) {
        this.eventDecodeRuleIds = eventDecodeRuleIds;
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

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getEventDecodeRuleNames() {
        return eventDecodeRuleNames;
    }

    public void setEventDecodeRuleNames(String eventDecodeRuleNames) {
        this.eventDecodeRuleNames = eventDecodeRuleNames;
    }

    public void setEventDecodeRules(List<EventDecodeRule> list) {
        this.eventDecodeRules = list;
    }

    public List<EventDecodeRule> getEventDecodeRules() {
        return this.eventDecodeRules;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }
}
