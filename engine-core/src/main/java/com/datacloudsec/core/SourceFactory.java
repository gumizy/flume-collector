

package com.datacloudsec.core;

import com.datacloudsec.config.CollectorEngineException;

public interface SourceFactory {

  Source create(String sourceName, String type)
      throws CollectorEngineException;

  Class<? extends Source> getClass(String type)
      throws CollectorEngineException;
}
