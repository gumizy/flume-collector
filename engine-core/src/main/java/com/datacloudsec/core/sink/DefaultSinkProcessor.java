package com.datacloudsec.core.sink;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.Sink;
import com.datacloudsec.core.Sink.Status;
import com.datacloudsec.core.SinkProcessor;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DefaultSinkProcessor implements SinkProcessor {
    private Sink sink;
    private LifecycleState lifecycleState;

    @Override
    public void start() {
        Preconditions.checkNotNull(sink, "DefaultSinkProcessor sink not set");
        sink.start();
        lifecycleState = LifecycleState.START;
    }

    @Override
    public void stop() {
        Preconditions.checkNotNull(sink, "DefaultSinkProcessor sink not set");
        sink.stop();
        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    @Override
    public void configure(Context context) {
    }

    @Override
    public Status process() throws EventDeliveryException {
        return sink.process();
    }

    @Override
    public void setSinks(List<Sink> sinks) {
        Preconditions.checkNotNull(sinks);
        Preconditions.checkArgument(sinks.size() == 1, "DefaultSinkPolicy can " + "only handle one sink, " + "try using a policy that supports multiple sinks");
        sink = sinks.get(0);
    }

    @Override
    public List<Sink> getSinks() {
        ArrayList<Sink> list = Lists.newArrayList();
        list.add(sink);
        return list;
    }

}
