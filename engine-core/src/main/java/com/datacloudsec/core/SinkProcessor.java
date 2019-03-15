package com.datacloudsec.core;

import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.lifecycle.LifecycleAware;

import java.util.List;

public interface SinkProcessor extends LifecycleAware, Configurable {

    Sink.Status process() throws EventDeliveryException;

    void setSinks(List<Sink> sinks);

    List<Sink> getSinks();
}