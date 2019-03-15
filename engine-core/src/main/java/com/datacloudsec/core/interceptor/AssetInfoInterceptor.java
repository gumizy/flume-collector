package com.datacloudsec.core.interceptor;


import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.config.conf.parser.asset.Asset;
import com.datacloudsec.core.conf.DcEnrichConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AssetInfoInterceptor
 */
public class AssetInfoInterceptor extends AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(AssetInfoInterceptor.class);

    @Override
    public void initialize() {
    }

    private AssetInfoInterceptor(Context context) {
        String enrichFields = context.getString(BasicConfigurationConstants.ASSET_ENRICH_FIELDS);
        if (enrichFields != null && enrichFields.trim().length() > 0) {
            this.enrichFields = enrichFields.split(",");
        }

        String enrichConfigField = context.getString(BasicConfigurationConstants.ASSET_ENRICH_CONFIG_FIELD);
        if (enrichConfigField != null && enrichConfigField.trim().length() > 0) {
            this.enrichConfigField = enrichConfigField;
        }

        if (this.enrichFields == null || this.enrichFields.length == 0 || this.enrichConfigField.isEmpty() || this.enrichConfigField.trim().length() == 0) {
            logger.error("Enrich fields of Asset init failed, context :{}" + context);
        }
    }

    @Override
    public Event intercept(Event event) {
        Object object = null;
        try {
            object = event.getValue().get(this.enrichConfigField);
            Asset asset = null;

            if (object != null && object.toString().trim().length() > 0) {
                asset = DcEnrichConfig.getAssetByIp((String) object);
            }

            if (asset == null) {
                asset = new Asset();
            }

            for (String fieldName : enrichFields) {
                inject(asset, fieldName, event);
            }
        } catch (Exception e) {
            logger.warn("Asset information parsing failed, exception : {}", object, e);
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
            return new AssetInfoInterceptor(context);
        }

        @Override
        public void configure(Context context) {
            this.context = context;
        }
    }

}

