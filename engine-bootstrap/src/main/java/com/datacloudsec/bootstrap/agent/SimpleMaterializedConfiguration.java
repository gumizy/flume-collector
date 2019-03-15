package com.datacloudsec.bootstrap.agent;

import com.datacloudsec.core.Channel;
import com.datacloudsec.core.SinkRunner;
import com.datacloudsec.core.SourceRunner;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class SimpleMaterializedConfiguration implements MaterializedConfiguration {

    private final Map<String, Channel> channels;
    private final Map<String, SourceRunner> sourceRunners;
    private final Map<String, SinkRunner> sinkRunners;

    public SimpleMaterializedConfiguration() {
        channels = new HashMap<>();
        sourceRunners = new HashMap<>();
        sinkRunners = new HashMap<>();
    }

    @Override
    public String toString() {
        return "{ sourceRunners:" + sourceRunners + " sinkRunners:" + sinkRunners + " channels:" + channels + " }";
    }

    @Override
    public void addSourceRunner(String name, SourceRunner sourceRunner) {
        sourceRunners.put(name, sourceRunner);
    }

    @Override
    public void addSinkRunner(String name, SinkRunner sinkRunner) {
        sinkRunners.put(name, sinkRunner);
    }

    @Override
    public void addChannel(String name, Channel channel) {
        channels.put(name, channel);
    }

    @Override
    public ImmutableMap<String, Channel> getChannels() {
        return ImmutableMap.copyOf(channels);
    }

    @Override
    public ImmutableMap<String, SourceRunner> getSourceRunners() {
        return ImmutableMap.copyOf(sourceRunners);
    }

    @Override
    public ImmutableMap<String, SinkRunner> getSinkRunners() {
        return ImmutableMap.copyOf(sinkRunners);
    }

}
