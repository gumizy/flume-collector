package com.datacloudsec.bootstrap.service.entity;

import java.util.Map;

/**
 * @Date 2019/1/26 11:11
 */
public class SourceEntity {

    private String sourceName;

    private String sourceType;

    private String lifeStat;

    private long startTime;

    private long stopTime;

    private long receiveCount;

    private long dropConut;

    private long failCount;
    private Map<String, String> context;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getLifeStat() {
        return lifeStat;
    }

    public void setLifeStat(String lifeStat) {
        this.lifeStat = lifeStat;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public long getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(long receiveCount) {
        this.receiveCount = receiveCount;
    }

    public long getDropConut() {
        return dropConut;
    }

    public void setDropConut(long dropConut) {
        this.dropConut = dropConut;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
