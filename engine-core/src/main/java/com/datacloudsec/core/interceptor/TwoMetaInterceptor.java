package com.datacloudsec.core.interceptor;



import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TwoMetaInterceptor
 */
public class TwoMetaInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(FiveMetaInterceptor.class);

    @Override
    public void initialize() {
    }

    @Override
    public Event intercept(Event event) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(event.getSrcAddress() == null ? "" : event.getSrcAddress()).append("_")
                    .append(event.getDstAddress() == null ? "" : event.getDstAddress());
            event.setSaDa(sb.toString());
            event.getValue().put("src_address_str", event.getSrcAddress());
            event.getValue().put("dst_address_str", event.getDstAddress());
        } catch (Exception e) {
            logger.debug("FiveMetaInterceptor for event information complete exception, original log will be persisted!", e);
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
            return new TwoMetaInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }

}
