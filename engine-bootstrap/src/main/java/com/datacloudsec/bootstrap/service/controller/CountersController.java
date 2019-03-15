package com.datacloudsec.bootstrap.service.controller;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.bootstrap.agent.AgentComponent;
import com.datacloudsec.bootstrap.agent.MaterializedConfiguration;
import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import com.datacloudsec.bootstrap.server.core.Request;
import com.datacloudsec.bootstrap.server.core.Response;
import com.datacloudsec.bootstrap.service.entity.ChannelEntity;
import com.datacloudsec.bootstrap.service.entity.SinkEntity;
import com.datacloudsec.bootstrap.service.entity.SourceEntity;
import com.datacloudsec.core.*;
import com.datacloudsec.core.instrumentation.ChannelCounter;
import com.datacloudsec.core.instrumentation.SinkCounter;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * CounterHandler
 */
@RequestMapping(name = "/counterConfig")
public class CountersController extends HttpHandler {

    private Logger LOGGER = LoggerFactory.getLogger(CountersController.class);

    @Override
    public void doGet(Request request, Response response) {
        MaterializedConfiguration conf = AgentComponent.getMaterializedConfiguration();
        if (conf == null) {
            response.write(500, "Service startup exception");
        }
        List<Object> sourceMonitor = getSourceMonitor(response, conf);
        List<Object> channelMonitor = getChannelMonitor(response, conf);
        List<Object> sinkMonitor = getSinkMonitor(response, conf);
        Map<String, List> result = Maps.newHashMap();
        result.put("source", sourceMonitor);
        result.put("channel", channelMonitor);
        result.put("sink", sinkMonitor);
        String jsonString = JSON.toJSONString(result);
        LOGGER.info(jsonString);
        response.write(jsonString);
    }

    private List<Object> getSourceMonitor(Response response, MaterializedConfiguration conf) {
        ImmutableMap<String, SourceRunner> sourceRunners = conf.getSourceRunners();
        if (sourceRunners.isEmpty()) {
            response.write(200, "Unconfigured data source");
            return Collections.emptyList();
        }
        ImmutableCollection<SourceRunner> values = sourceRunners.values();
        ArrayList<Object> sourceMonitors = Lists.newArrayList();
        for (SourceRunner runner : values) {
            SourceEntity sourceEntity = new SourceEntity();
            sourceMonitors.add(sourceEntity);

            Source source = runner.getSource();
            String sourceName = source.getName();
            String sourceType = source.getType();

            String lifeState = source.getLifecycleState().name();
            SourceCounter counter = source.getSourceCounter();
            long eventDropCount = counter.getEventDropCount();
            long eventReceivedCount = counter.getEventReceivedCount();
            long eventReadFail = counter.getEventReadFail();
            long startTime = counter.getStartTime();
            long stopTime = counter.getStopTime();

            sourceEntity.setDropConut(eventDropCount);
            sourceEntity.setFailCount(eventReadFail);
            sourceEntity.setReceiveCount(eventReceivedCount);
            sourceEntity.setStartTime(startTime);
            sourceEntity.setStopTime(stopTime);
            sourceEntity.setSourceName(sourceName);
            sourceEntity.setLifeStat(lifeState);
            sourceEntity.setSourceType(sourceType);
        }
        return sourceMonitors;

    }

    private List<Object> getSinkMonitor(Response response, MaterializedConfiguration conf) {
        ImmutableMap<String, SinkRunner> sourceRunners = conf.getSinkRunners();
        if (sourceRunners.isEmpty()) {
            response.write(200, "Unconfigured data source");
            return Collections.emptyList();
        }
        ImmutableCollection<SinkRunner> values = sourceRunners.values();
        ArrayList<Object> sinkMonitors = Lists.newArrayList();
        for (SinkRunner runner : values) {
            SinkEntity sinkEntity = new SinkEntity();
            sinkMonitors.add(sinkEntity);

            SinkProcessor policy = runner.getPolicy();
            // default sink just one sink；
            Sink sink = policy.getSinks().get(0);
            String sinkName = sink.getName();
            LifecycleState state = sink.getLifecycleState();
            SinkCounter counter = sink.getSinkCounter();
            String sinkType = sink.getType();

            long eventDrainAttemptCount = counter.getEventDrainAttemptCount();
            long eventWriteFail = counter.getEventWriteFail();

            sinkEntity.setSinkName(sinkName);
            sinkEntity.setFailCount(eventWriteFail);
            sinkEntity.setSendCount(eventDrainAttemptCount);
            sinkEntity.setLifeStat(state.name());
            sinkEntity.setStartTime(counter.getStartTime());
            sinkEntity.setStopTime(counter.getStopTime());
            sinkEntity.setSinkType(sinkType);
        }

        return sinkMonitors;
    }

    private List<Object> getChannelMonitor(Response response, MaterializedConfiguration conf) {
        ImmutableMap<String, Channel> channels = conf.getChannels();
        if (channels.isEmpty()) {
            response.write(200, "Unconfigured data source");
            return Collections.emptyList();
        }
        ImmutableCollection<Channel> values = channels.values();
        ArrayList<Object> channelMonitors = Lists.newArrayList();
        for (Channel channel : values) {
            ChannelEntity channelEntity = new ChannelEntity();
            channelMonitors.add(channelEntity);

            ChannelCounter counter = channel.getChannelCounter();
            long channelCapacity = counter.getChannelCapacity();

            String lifeState = channel.getLifecycleState().name();
            String channelName = channel.getName();
            String type = channel.getType();

            channelEntity.setChannelName(channelName);
            channelEntity.setLifeState(lifeState);
            channelEntity.setChannelSize(channelCapacity);
            channelEntity.setChannelType(type);
        }

        return channelMonitors;
    }

    @Override
    public void doPost(Request request, Response response) {
        doGet(request, response);
    }
}
