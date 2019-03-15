package com.datacloudsec.core.sink;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.conf.sink.SinkType;
import com.datacloudsec.core.Sink;
import com.datacloudsec.core.SinkFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class DefaultSinkFactory implements SinkFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSinkFactory.class);

    @Override
    public Sink create(String name, String type) throws CollectorEngineException {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(type, "type");
        logger.info("Creating instance of sink: {}, type: {}", name, type);
        Class<? extends Sink> sinkClass = getClass(type);
        try {
            Sink sink = sinkClass.newInstance();
            sink.setName(name);
            sink.setType(type);
            return sink;
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to create sink: " + name + ", type: " + type + ", class: " + sinkClass.getName(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Sink> getClass(String type) throws CollectorEngineException {
        String sinkClassName = type;
        SinkType sinkType = SinkType.OTHER;
        try {
            sinkType = SinkType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            logger.debug("Sink type {} is a custom type", type);
        }
        if (!sinkType.equals(SinkType.OTHER)) {
            sinkClassName = sinkType.getClassName();
        }
        try {
            return (Class<? extends Sink>) Class.forName(sinkClassName);
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to load sink type: " + type + ", class: " + sinkClassName, ex);
        }
    }
}
