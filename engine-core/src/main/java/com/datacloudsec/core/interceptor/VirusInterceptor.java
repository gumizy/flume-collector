package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.core.conf.VirusInfo;
import com.datacloudsec.core.conf.VirusInfoSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * VirusInterceptor
 */
public class VirusInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(VirusInterceptor.class);

    private VirusInterceptor(Context context) {
        String enrichFields = context.getString(BasicConfigurationConstants.VIRUS_ENRICH_FIELDS);
        if (enrichFields != null && enrichFields.trim().length() > 0) {
            this.enrichFields = enrichFields.split(",");
        }

        String enrichConfigField = context.getString(BasicConfigurationConstants.VIRUS_REFLECT_ENRICH_FIELDS);
        if (enrichConfigField != null && enrichConfigField.trim().length() > 0) {
            this.enrichConfigField = enrichConfigField;
        }

        if (this.enrichFields == null || this.enrichFields.length == 0 || this.enrichConfigField.isEmpty() || this.enrichConfigField.trim().length() == 0) {
            logger.error("Enrich fields of virus init failed, context :{}" + context);
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
            VirusInfo virusInfo = null;

            if (object != null && object.toString().trim().length() > 0) {
                virusInfo = VirusInfoSearch.getVirusInfoByHash((String) object);
            }

            if (virusInfo == null) {
                virusInfo = new VirusInfo();
            }

            for (String fieldName : enrichFields) {
                inject(fieldName, virusInfo, event);
            }
        } catch (Exception e) {
            logger.debug("Virus information parsing failed, exception : {}", object, e);
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
            return new VirusInterceptor(context);
        }

        @Override
        public void configure(Context context) {
            this.context = context;
        }
    }
}
