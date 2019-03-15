

package com.datacloudsec.core.source;

import com.datacloudsec.core.ParserRunner;
import com.datacloudsec.core.Source;
import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.datacloudsec.core.parser.AbstractParserProcessor;
import com.google.common.base.Preconditions;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AbstractSource implements Source {

    private ChannelProcessor channelProcessor;

    private String name;
    private String type;

    protected AbstractParserProcessor parserProcessor;
    protected ParserRunner parserRunner;

    private LifecycleState lifecycleState;

    public AbstractSource() {
        lifecycleState = LifecycleState.IDLE;
    }

    @Override
    public synchronized void start() {
        Preconditions.checkState(channelProcessor != null, "No channel processor configured");

        lifecycleState = LifecycleState.START;
    }

    @Override
    public synchronized void stop() {
        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public synchronized void setChannelProcessor(ChannelProcessor cp) {
        channelProcessor = cp;
    }

    @Override
    public synchronized ChannelProcessor getChannelProcessor() {
        return channelProcessor;
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

    public String toString() {
        return this.getClass().getName() + "{name:" + name + ",state:" + lifecycleState + "}";
    }
}
