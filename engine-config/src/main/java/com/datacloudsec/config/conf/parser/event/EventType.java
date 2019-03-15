package com.datacloudsec.config.conf.parser.event;

import java.io.Serializable;
import java.util.Date;

public class EventType implements Serializable {

    private Integer id;

    private String eventTypeName;

    private Integer parentId;

    private String parentName;

    private String eventFieldIds;

    private String idPath;

    private String namePath;

    private Integer predefined;

    private Integer deleted;

    private String description;

    private Date insertTime;

    private Date updateTime;

    private String adminName;

    private String extraInfo;

    private String keyFields;        //事件分类对应的关键属性，属性字段值采用“，”分割

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getEventFieldIds() {
        return eventFieldIds;
    }

    public void setEventFieldIds(String eventFieldIds) {
        this.eventFieldIds = eventFieldIds;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    public Integer getPredefined() {
        return predefined;
    }

    public void setPredefined(Integer predefined) {
        this.predefined = predefined;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getKeyFields() {
        return this.keyFields;
    }

    public void setKeyFields(String keyFields) {
        this.keyFields = keyFields;
    }

}
