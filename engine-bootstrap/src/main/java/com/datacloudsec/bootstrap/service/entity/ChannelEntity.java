package com.datacloudsec.bootstrap.service.entity;

import java.util.Map;

/**
 * @Date 2019/1/26 11:32
 */
public class ChannelEntity {

    private String channelName;

    private String lifeState;

    private Long channelSize;

    private String channelType;

    private Map<String, String> context;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLifeState() {
        return lifeState;
    }

    public void setLifeState(String lifeState) {
        this.lifeState = lifeState;
    }

    public Long getChannelSize() {
        return channelSize;
    }

    public void setChannelSize(Long channelSize) {
        this.channelSize = channelSize;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
