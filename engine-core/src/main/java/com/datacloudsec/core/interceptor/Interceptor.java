package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Event;
import com.datacloudsec.core.conf.Configurable;

import java.util.List;

/**
 * Interceptor
 */
public interface Interceptor {

    void initialize();

    Event intercept(Event event);

    List<Event> intercept(List<Event> events);

    void close();

    interface Builder extends Configurable {
        Interceptor build();
    }
}
