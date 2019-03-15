package com.datacloudsec.core.serialization;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.Locale;

public class EventSerializerFactory {

    private static final Logger logger = LoggerFactory.getLogger(EventSerializerFactory.class);

    public static EventSerializer getInstance(String serializerType, Context context, OutputStream out) {

        Preconditions.checkNotNull(serializerType, "serializer type must not be null");

        EventSerializerType type;
        try {
            type = EventSerializerType.valueOf(serializerType.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            logger.debug("Not in enum, loading builder class: {}", serializerType);
            type = EventSerializerType.OTHER;
        }
        Class<? extends EventSerializer.Builder> builderClass = type.getBuilderClass();

        if (builderClass == null) {
            try {
                Class c = Class.forName(serializerType);
                if (c != null && EventSerializer.Builder.class.isAssignableFrom(c)) {
                    builderClass = (Class<? extends EventSerializer.Builder>) c;
                } else {
                    String errMessage = "Unable to instantiate Builder from " + serializerType + ": does not appear to implement " + EventSerializer.Builder.class.getName();
                    throw new CollectorEngineException(errMessage);
                }
            } catch (ClassNotFoundException ex) {
                logger.error("Class not found: " + serializerType, ex);
                throw new CollectorEngineException(ex);
            }
        }

        EventSerializer.Builder builder;
        try {
            builder = builderClass.newInstance();
        } catch (InstantiationException ex) {
            String errMessage = "Cannot instantiate builder: " + serializerType;
            logger.error(errMessage, ex);
            throw new CollectorEngineException(errMessage, ex);
        } catch (IllegalAccessException ex) {
            String errMessage = "Cannot instantiate builder: " + serializerType;
            logger.error(errMessage, ex);
            throw new CollectorEngineException(errMessage, ex);
        }

        return builder.build(context, out);
    }

}
