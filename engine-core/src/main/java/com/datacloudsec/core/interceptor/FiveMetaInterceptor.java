package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FiveMetaInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(FiveMetaInterceptor.class);

    @Override
    public void initialize() {
    }

    @Override
    public Event intercept(Event event) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(event.getSrcAddress() == null ? "" : event.getSrcAddress()).append("_")
                    .append(event.getSrcPort() == null ? "" : event.getSrcPort()).append("_")
                    .append(event.getAppProtocol() == null ? (event.getTranProtocol() == null ? "" : event.getTranProtocol() + "") : event.getAppProtocol())
                    .append("_").append(event.getDstAddress() == null ? "" : event.getDstAddress()).append("_")
                    .append(event.getDstPort() == null ? "" : event.getDstPort());
            event.setSaSpApDaDp(sb.toString());
        } catch (Exception e) {
            logger.debug("FiveMetaInterceptor for event information complete exception, oraginal log will be persisted!", e);
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for (Event event : events) {
            intercept(event);
        }
        return events;
    }

    @Override
    public void close() {
    }

    public static class Builder implements Interceptor.Builder {
        @Override
        public Interceptor build() {
            return new FiveMetaInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
