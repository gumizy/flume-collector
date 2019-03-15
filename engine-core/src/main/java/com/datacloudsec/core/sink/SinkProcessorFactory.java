package com.datacloudsec.core.sink;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.ComponentConfiguration;
import com.datacloudsec.config.conf.sink.SinkProcessorType;
import com.datacloudsec.core.Sink;
import com.datacloudsec.core.SinkProcessor;
import com.datacloudsec.core.conf.Configurables;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SinkProcessorFactory {
    private static final Logger logger = LoggerFactory.getLogger(SinkProcessorFactory.class);

    private static final String TYPE = "type";

    @SuppressWarnings("unchecked")
    public static SinkProcessor getProcessor(Context context, List<Sink> sinks) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(sinks);
        Preconditions.checkArgument(!sinks.isEmpty());
        Map<String, String> params = context.getParameters();
        SinkProcessor processor;
        String typeStr = params.get(TYPE);
        SinkProcessorType type = SinkProcessorType.OTHER;
        String processorClassName = typeStr;
        try {
            type = SinkProcessorType.valueOf(typeStr.toUpperCase(Locale.ENGLISH));
        } catch (Exception ex) {
            logger.warn("Sink Processor type {} is a custom type", typeStr);
        }

        if (!type.equals(SinkProcessorType.OTHER)) {
            processorClassName = type.getClassName();
        }

        logger.debug("Creating instance of sink processor type {}, class {}", typeStr, processorClassName);
        Class<? extends SinkProcessor> processorClass = null;
        try {
            processorClass = (Class<? extends SinkProcessor>) Class.forName(processorClassName);
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to load sink processor type: " + typeStr + ", class: " + type.getClassName(), ex);
        }
        try {
            processor = processorClass.newInstance();
        } catch (Exception e) {
            throw new CollectorEngineException("Unable to create sink processor, type: " + typeStr + ", class: " + processorClassName, e);
        }

        processor.setSinks(sinks);
        Configurables.configure(processor, context);
        return processor;
    }

    @SuppressWarnings("unchecked")
    public static SinkProcessor getProcessor(ComponentConfiguration conf, List<Sink> sinks) {
        String typeStr = conf.getType();
        SinkProcessor processor;
        SinkProcessorType type = SinkProcessorType.DEFAULT;
        try {
            type = SinkProcessorType.valueOf(typeStr.toUpperCase(Locale.ENGLISH));
        } catch (Exception ex) {
            logger.warn("Sink type {} does not exist, using default", typeStr);
        }

        Class<? extends SinkProcessor> processorClass = null;
        try {
            processorClass = (Class<? extends SinkProcessor>) Class.forName(type.getClassName());
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to load sink processor type: " + typeStr + ", class: " + type.getClassName(), ex);
        }
        try {
            processor = processorClass.newInstance();
        } catch (Exception e) {
            throw new CollectorEngineException("Unable to create processor, type: " + typeStr + ", class: " + type.getClassName(), e);
        }

        processor.setSinks(sinks);
        Configurables.configure(processor, conf);
        return processor;
    }

}
