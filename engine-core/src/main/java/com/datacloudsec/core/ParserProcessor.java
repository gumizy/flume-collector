package com.datacloudsec.core;

import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.lifecycle.LifecycleAware;

/**
 * @Date 2019/1/12 14:01
 */
public interface ParserProcessor extends LifecycleAware, Configurable {
    Status process() throws EventDeliveryException;

    enum Status {
        READY, BACKOFF
    }
}
