package com.datacloudsec.core.channel;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractChannelSelector implements ChannelSelector {

    private List<Channel> channels;

    private String name;
    private String type;

    @Override
    public List<Channel> getAllChannels() {
        return channels;
    }

    @Override
    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public synchronized void setName(String name) {
        this.name = name;
    }

    @Override
    public synchronized String getName() {
        return name;
    }
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
    protected Map<String, Channel> getChannelNameMap() {
        Map<String, Channel> channelNameMap = new HashMap<String, Channel>();
        for (Channel ch : getAllChannels()) {
            channelNameMap.put(ch.getName(), ch);
        }
        return channelNameMap;
    }

    protected List<Channel> getChannelListFromNames(String channels, Map<String, Channel> channelNameMap) {
        List<Channel> configuredChannels = new ArrayList<Channel>();
        if (channels == null || channels.isEmpty()) {
            return configuredChannels;
        }
        String[] chNames = channels.split(" ");
        for (String name : chNames) {
            Channel ch = channelNameMap.get(name);
            if (ch != null) {
                configuredChannels.add(ch);
            } else {
                throw new CollectorEngineException("Selector channel not found: " + name);
            }
        }
        return configuredChannels;
    }
}