package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.ContextKit;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.intercepor.AnalysisEventMappingBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * FieldMapInterceptor
 */
public class FieldMapInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(FieldMapInterceptor.class);

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        try {
            final List<AnalysisEventMappingBean> list = ContextKit.getAnalysisEventMapping();
            for (AnalysisEventMappingBean bean : list) {
                if (bean.getFrom() == null || bean.getTo() == null) {
                    continue;
                }
                Object value = event.getValue(bean.getFrom());
                if (value == null) {
                    event.putValue(bean.getTo(), null);
                } else {
                    event.putValue(bean.getTo(), bean.getToString() != null && bean.getToString() ? String.valueOf(value) : value);
                }
                event.putValue(bean.getTo(), value != null ? value : bean.getDefaultValue());
            }
        } catch (Exception e) {
            logger.error("FieldMap can not be located, so event field map can not be done!");
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
            return new FieldMapInterceptor();
        }

        @Override
        public void configure(Context context) {
        }
    }
}
