package com.datacloudsec.core;

import com.datacloudsec.config.CollectorEngineException;

/**
 * @Date 2019/1/18 16:19
 */
public interface ParserFactory {
    Parser.Builder create(String sourceName, String type) throws CollectorEngineException;

    Class<? extends Parser.Builder> getClass(String type) throws CollectorEngineException;
}
