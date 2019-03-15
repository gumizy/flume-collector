package com.datacloudsec.config.conf.channel;

import com.datacloudsec.config.conf.ComponentWithClassName;

public enum ChannelSelectorType implements ComponentWithClassName {

    OTHER(null),

    REPLICATING("org.apache.flume.channel.ReplicatingChannelSelector"),

    MULTIPLEXING("org.apache.flume.channel.MultiplexingChannelSelector");

    private final String channelSelectorClassName;

    ChannelSelectorType(String channelSelectorClassName) {
        this.channelSelectorClassName = channelSelectorClassName;
    }

    @Override
    public String getClassName() {
        return channelSelectorClassName;
    }
}
