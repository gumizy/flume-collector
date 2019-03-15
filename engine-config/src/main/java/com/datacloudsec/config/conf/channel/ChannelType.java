package com.datacloudsec.config.conf.channel;

import com.datacloudsec.config.conf.ComponentWithClassName;

public enum ChannelType implements ComponentWithClassName {

    OTHER(null),

    MEMORY("com.datacloudsec.core.channel.MemoryChannel");

    private final String channelClassName;

    ChannelType(String channelClassName) {
        this.channelClassName = channelClassName;
    }

    @Override
    public String getClassName() {
        return channelClassName;
    }
}
