package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.config.geo.GeoSearcher;
import com.datacloudsec.config.geo.datablock.DataBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * GeoInterceptor
 */
public class GeoInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(GeoInterceptor.class);


    private GeoInterceptor(Context context) {
        String enrichFields = context.getString(BasicConfigurationConstants.GEO_ENRICH_FIELDS);
        if (enrichFields != null && enrichFields.trim().length() > 0) {
            this.enrichFields = enrichFields.split(",");
        }

        String enrichConfigField = context.getString(BasicConfigurationConstants.GEO_ENRICH_CONFIG_FIELD);
        if (enrichConfigField != null && enrichConfigField.trim().length() > 0) {
            this.enrichConfigField = enrichConfigField;
        }

        if (this.enrichFields == null || this.enrichFields.length == 0 || this.enrichConfigField.isEmpty() || this.enrichConfigField.trim().length() == 0) {
            logger.error("Enrich base filed or enrich fields is has not config property, original log will be persisted, context :{}" + context);
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
            if (object != null && object.toString().trim().length() > 0) {
                DataBlock dataBlock = GeoSearcher.search((String) object);

                if (dataBlock == null) {
                    dataBlock = new DataBlock();
                } else {
                    dataBlock = GeoSearcher.getCoordByDataBlock(dataBlock);
                }

                for (String fieldName : enrichFields) {
                    inject(dataBlock, fieldName, event);
                }
            }
        } catch (Exception e) {
            logger.debug("GEO information for ip {} can not be located, so event geo information complated can not be done!", object);
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
            return new GeoInterceptor(context);
        }

        @Override
        public void configure(Context context) {
            this.context = context;
        }
    }
}
