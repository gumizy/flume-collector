package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * InterceptorChain
 */
public class InterceptorChain implements Interceptor {

    private List<Interceptor> interceptors;

    public InterceptorChain() {
        this.interceptors = new ArrayList<>();
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors != null ? interceptors : new ArrayList<>();
    }

    @Override
    public Event intercept(Event event) {
        if (event == null) {
            return null;
        }
        for (Interceptor interceptor : interceptors) {
            if (interceptor != null) {
                event = interceptor.intercept(event);
            }
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return events;
        }
        for (Interceptor interceptor : interceptors) {
            if (interceptor != null) {
                events = interceptor.intercept(events);
            }
        }
        return events;
    }

    @Override
    public void initialize() {
        for (Interceptor interceptor : interceptors) {
            if (interceptor != null) {
                interceptor.initialize();
            }
        }
    }

    @Override
    public void close() {
        for (Interceptor interceptor : interceptors) {
            if (interceptor != null) {
                interceptor.close();
            }
        }
    }

}
