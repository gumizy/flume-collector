package com.datacloudsec.core.source;

import com.datacloudsec.core.Source;
import com.datacloudsec.core.SourceRunner;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.lifecycle.LifecycleState;

public class EventDrivenSourceRunner extends SourceRunner {

    private LifecycleState lifecycleState;

    public EventDrivenSourceRunner() {
        lifecycleState = LifecycleState.IDLE;
    }

    @Override
    public void start() {
        Source source = getSource();
        ChannelProcessor cp = source.getChannelProcessor();
        cp.initialize();
        source.start();
        lifecycleState = LifecycleState.START;
    }

    @Override
    public void stop() {
        Source source = getSource();
        source.stop();
        ChannelProcessor cp = source.getChannelProcessor();
        cp.close();
        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public String toString() {
        return "EventDrivenSourceRunner: { source:" + getSource() + " }";
    }

    @Override
    public LifecycleState getLifecycleState() {
        return lifecycleState;
    }

}
