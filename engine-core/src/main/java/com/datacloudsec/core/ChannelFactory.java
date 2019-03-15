
package com.datacloudsec.core;

import com.datacloudsec.config.CollectorEngineException;

public interface ChannelFactory {

  Channel create(String name, String type) throws CollectorEngineException;

  Class<? extends Channel> getClass(String type) throws CollectorEngineException;
}
