
package com.datacloudsec.config.conf;

import com.datacloudsec.config.CollectorEngineException;

public class ConfigurationException extends CollectorEngineException {

    private static final long serialVersionUID = 1L;

    public ConfigurationException(String arg0) {
        super(arg0);
    }

    public ConfigurationException(Throwable arg0) {
        super(arg0);
    }

    public ConfigurationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
