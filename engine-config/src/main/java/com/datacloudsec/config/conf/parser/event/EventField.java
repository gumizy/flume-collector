package com.datacloudsec.config.conf.parser.event;

import java.io.Serializable;
import java.util.Date;

public class EventField implements Serializable {

    private Integer id;

    private String eventFieldName;

    private String eventFieldType;

    private String eventFieldKey;

    private Integer defaultField;

    private Integer analysisField;

    private Integer visible;

    private Integer predefined;

    private Integer deleted;

    private String description;

    private Date insertTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventFieldName() {
        return eventFieldName;
    }

    public void setEventFieldName(String eventFieldName) {
        this.eventFieldName = eventFieldName;
    }

    public String getEventFieldType() {
        return eventFieldType;
    }

    public void setEventFieldType(String eventFieldType) {
        this.eventFieldType = eventFieldType;
    }

    public String getEventFieldKey() {
        return eventFieldKey;
    }

    public void setEventFieldKey(String eventFieldKey) {
        this.eventFieldKey = eventFieldKey;
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

    public void setDescription(String eventAttrDesc) {
        this.description = eventAttrDesc;
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

    public Integer getDefaultField() {
        return defaultField;
    }

    public void setDefaultField(Integer defaultField) {
        this.defaultField = defaultField;
    }

    public Integer getAnalysisField() {
        return analysisField;
    }

    public void setAnalysisField(Integer analysisField) {
        this.analysisField = analysisField;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }
}
