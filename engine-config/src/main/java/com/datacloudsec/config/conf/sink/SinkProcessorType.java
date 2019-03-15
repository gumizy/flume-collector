package com.datacloudsec.config.conf.sink;

import com.datacloudsec.config.conf.ComponentWithClassName;

public enum SinkProcessorType implements ComponentWithClassName {

    OTHER(null),

    FAILOVER("org.apache.flume.sink.FailoverSinkProcessor"),

    DEFAULT("org.apache.flume.sink.DefaultSinkProcessor"),

    LOAD_BALANCE("org.apache.flume.sink.LoadBalancingSinkProcessor");

    private final String processorClassName;

    private SinkProcessorType(String processorClassName) {
        this.processorClassName = processorClassName;
    }

    @Override
    public String getClassName() {
        return processorClassName;
    }
}
