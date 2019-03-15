package com.datacloudsec.core.source;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.conf.source.SourceType;
import com.datacloudsec.core.Source;
import com.datacloudsec.core.SourceFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class DefaultSourceFactory implements SourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSourceFactory.class);

    @Override
    public Source create(String name, String type) throws CollectorEngineException {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(type, "type");
        logger.info("Creating instance of source {}, type {}", name, type);
        Class<? extends Source> sourceClass = getClass(type);
        try {
            Source source = sourceClass.newInstance();
            source.setName(name);
            source.setType(type);
            return source;
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to create source: " + name + ", type: " + type + ", class: " + sourceClass.getName(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Source> getClass(String type) throws CollectorEngineException {
        String sourceClassName = type;
        SourceType srcType = SourceType.OTHER;
        try {
            srcType = SourceType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            logger.debug("Source type {} is a custom type", type);
        }
        if (!srcType.equals(SourceType.OTHER)) {
            sourceClassName = srcType.getClassName();
        }
        try {
            return (Class<? extends Source>) Class.forName(sourceClassName);
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to load source type: " + type + ", class: " + sourceClassName, ex);
        }
    }
}
