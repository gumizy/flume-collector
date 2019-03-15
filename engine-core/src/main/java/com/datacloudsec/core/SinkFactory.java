package com.datacloudsec.core;

import com.datacloudsec.config.CollectorEngineException;

public interface SinkFactory {
    Sink create(String name, String type) throws CollectorEngineException;

    Class<? extends Sink> getClass(String type) throws CollectorEngineException;
}
