package com.datacloudsec.core.parser;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.conf.parser.ParserType;
import com.datacloudsec.core.Parser;
import com.datacloudsec.core.ParserFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * @Date 2019/1/18 16:19
 */
public class DefaultParserFactory implements ParserFactory {
    private final static Logger logger = LoggerFactory.getLogger(DefaultParserFactory.class);

    @Override
    public Parser.Builder create(String name, String type) throws CollectorEngineException {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(type, "type");
        logger.info("Creating instance of channel {} type {}", name, type);
        Class<? extends Parser.Builder> parserClass = getClass(type);
        try {
            Parser.Builder build = parserClass.newInstance();

            return build;
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to create parser: " + name + ", type: " + type + ", class: " + parserClass.getName(), ex);

        }
    }

    @Override
    public Class<? extends Parser.Builder> getClass(String type) throws CollectorEngineException {
        String parserClassName = type;
        ParserType parserType = ParserType.OTHER;
        try {
            parserType = ParserType.valueOf(parserClassName.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            logger.debug("parser typt {} is custom type", parserType);
        }
        if (!parserType.equals(ParserType.OTHER)) {
            parserClassName = parserType.getClassName();
        }

        try {
            return (Class<? extends Parser.Builder>) Class.forName(parserClassName);
        } catch (ClassNotFoundException e) {
            throw new CollectorEngineException("Unable to load parser type: " + type + ", class: " + parserClassName, e);
        }
    }
}
