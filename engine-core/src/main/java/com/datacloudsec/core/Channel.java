package com.datacloudsec.core;

import com.datacloudsec.config.Event;
import com.datacloudsec.core.instrumentation.ChannelCounter;
import com.datacloudsec.core.lifecycle.LifecycleAware;

import java.util.List;

public interface Channel extends LifecycleAware, OriginalComponent {

    void put(Event event) throws ChannelException;

    void puts(List<Event> events) throws ChannelException;

    Event take() throws ChannelException;

    List<Event> takes() throws ChannelException;

    ChannelCounter getChannelCounter();
}
