

package com.datacloudsec.core;

import com.datacloudsec.config.EventDeliveryException;

public interface PollableSource extends Source {

    Status process() throws EventDeliveryException;

    long getBackOffSleepIncrement();

    long getMaxBackOffSleepInterval();

    enum Status {
        READY, BACKOFF
    }

}
