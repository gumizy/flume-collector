package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.core.conf.TrojansInfo;
import com.datacloudsec.core.conf.TrojansInfoSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TrojansInterceptor
 */
public class TrojansInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(TrojansInterceptor.class);

    private TrojansInterceptor(Context context) {
        String enrichFields = context.getString(BasicConfigurationConstants.TROJANS_ENRICH_FIELDS);
        if (enrichFields != null && enrichFields.trim().length() > 0) {
            this.enrichFields = enrichFields.split(",");
        }

        String enrichConfigField = context.getString(BasicConfigurationConstants.TROJANS_REFLECT_ENRICH_FIELDS);
        if (enrichConfigField != null && enrichConfigField.trim().length() > 0) {
            this.enrichConfigField = enrichConfigField;
        }

        if (this.enrichFields == null || this.enrichFields.length == 0 || this.enrichConfigField.isEmpty() || this.enrichConfigField.trim().length() == 0) {
            logger.error("Enrich fields of trojans init failed, context :{}" + context);
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        Object object = null;
        try {
            object = event.getValue().get(this.enrichConfigField);
            TrojansInfo trojansInfo = null;

            if (object != null && object.toString().trim().length() > 0) {
                trojansInfo = TrojansInfoSearch.getTrojansInfoByHash((String) object);
            }

            if (trojansInfo == null) {
                trojansInfo = new TrojansInfo();
            }

            for (String fieldName : enrichFields) {
                inject(fieldName, trojansInfo, event);
            }
        } catch (Exception e) {
            logger.debug("Trojans information parsing failed, exception : {}", object, e);
        } finally {

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
        private Context context;

        @Override
        public Interceptor build() {
            return new TrojansInterceptor(context);
        }

        @Override
        public void configure(Context context) {
            this.context = context;
        }
    }
}
