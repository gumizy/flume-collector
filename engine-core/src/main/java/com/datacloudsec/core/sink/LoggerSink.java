

package com.datacloudsec.core.sink;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SinkCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerSink extends AbstractSink implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(LoggerSink.class);

    @Override
    public void configure(Context context) {
        // no oper
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.READY;
        Channel channel = getChannel();
        Event event = null;
        try {
            event = channel.take();
            if (event != null) {
                logger.info("Event: " + event.valueMapToJSONString());
            } else {
                result = Status.BACKOFF;
            }
        } catch (Exception ex) {
            throw new EventDeliveryException("Failed to log event: " + event, ex);
        } finally {
        }

        return result;
    }

    @Override
    public SinkCounter getSinkCounter() {
        return null;
    }
}
