package com.datacloudsec.core.sink;

import com.datacloudsec.core.Channel;
import com.datacloudsec.core.Sink;
import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;
import com.datacloudsec.core.lifecycle.LifecycleAware;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.google.common.base.Preconditions;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AbstractSink implements Sink, LifecycleAware {

    private Channel channel;
    private String name;

    private LifecycleState lifecycleState;
    private String type;

    public AbstractSink() {
        lifecycleState = LifecycleState.IDLE;
    }

    @Override
    public synchronized void start() {
        Preconditions.checkState(channel != null, "No channel configured");

        lifecycleState = LifecycleState.START;
    }

    @Override
    public synchronized void stop() {
        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public synchronized Channel getChannel() {
        return channel;
    }

    @Override
    public synchronized void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public synchronized LifecycleState getLifecycleState() {
        return lifecycleState;
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
    @Override
    public String toString() {
        return this.getClass().getName() + "{name:" + name + ", channel:" + channel.getName() + "}";
    }
}
