package com.datacloudsec.core;

import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;
import com.datacloudsec.core.instrumentation.SinkCounter;
import com.datacloudsec.core.lifecycle.LifecycleAware;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Sink extends LifecycleAware, OriginalComponent {

    void setChannel(Channel channel);

    Channel getChannel();

    Status process() throws EventDeliveryException;

    enum Status {
        READY, BACKOFF
    }

    SinkCounter getSinkCounter();
}
