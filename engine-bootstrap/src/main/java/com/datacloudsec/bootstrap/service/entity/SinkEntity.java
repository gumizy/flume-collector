package com.datacloudsec.bootstrap.service.entity;

import java.util.Map;

/**
 * @Date 2019/1/26 11:11
 */
public class SinkEntity {

    private String sinkName;

    private String lifeStat;

    private long startTime;

    private long stopTime;

    private long sendCount;

    private long failCount;

    private String sinkType;
    private Map<String, String> context;

    public String getSinkName() {
        return sinkName;
    }

    public void setSinkName(String sinkName) {
        this.sinkName = sinkName;
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

    public long getSendCount() {
        return sendCount;
    }

    public void setSendCount(long sendCount) {
        this.sendCount = sendCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
