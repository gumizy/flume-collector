package com.datacloudsec.core.output;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Create PathManager instances.
 */
public class PathManagerFactory {
    private static final Logger logger = LoggerFactory.getLogger(PathManagerFactory.class);

    public static PathManager getInstance(String managerType, Context context) {

        Preconditions.checkNotNull(managerType, "path manager type must not be null");

        // try to find builder class in enum of known output serializers
        PathManagerType type;
        try {
            type = PathManagerType.valueOf(managerType.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            logger.debug("Not in enum, loading builder class: {}", managerType);
            type = PathManagerType.OTHER;
        }
        Class<? extends PathManager.Builder> builderClass = type.getBuilderClass();

        // handle the case where they have specified their own builder in the config
        if (builderClass == null) {
            try {
                Class c = Class.forName(managerType);
                if (c != null && PathManager.Builder.class.isAssignableFrom(c)) {
                    builderClass = (Class<? extends PathManager.Builder>) c;
                } else {
                    String errMessage = "Unable to instantiate Builder from " + managerType + ": does not appear to implement " + PathManager.Builder.class.getName();
                    throw new CollectorEngineException(errMessage);
                }
            } catch (ClassNotFoundException ex) {
                logger.error("Class not found: " + managerType, ex);
                throw new CollectorEngineException(ex);
            }
        }

        // build the builder
        PathManager.Builder builder;
        try {
            builder = builderClass.newInstance();
        } catch (InstantiationException ex) {
            String errMessage = "Cannot instantiate builder: " + managerType;
            logger.error(errMessage, ex);
            throw new CollectorEngineException(errMessage, ex);
        } catch (IllegalAccessException ex) {
            String errMessage = "Cannot instantiate builder: " + managerType;
            logger.error(errMessage, ex);
            throw new CollectorEngineException(errMessage, ex);
        }

        return builder.build(context);
    }
}
