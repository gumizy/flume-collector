package com.datacloudsec.sink.syslog;

import ch.qos.logback.core.net.SyslogConstants;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.tools.IPv4Kit;
import com.datacloudsec.config.tools.SyslogKit;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SinkCounter;
import com.datacloudsec.core.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SyslogSink
 */
public class SyslogSink extends AbstractSink implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(SyslogSink.class);

    private SyslogKit syslogKit = null;
    private SinkCounter counter;
    public static final String CONFIG_PORT = "port";
    public static final String CONFIG_HOST = "host";

    public SyslogSink() {
        super();
    }

    @Override
    public Status process() {
        Status result = Status.READY;
        Channel channel = super.getChannel();
        if (channel != null) {
            try {
                List<Event> events = channel.takes();
                if (events == null || events.size() == 0) {
                    result = Status.BACKOFF;
                    return result;
                }
                if (syslogKit != null) {
                    for (Event event : events) {
                        if (event.getValue().isEmpty()) {
                            logger.error("SyslogSink event value map is empty, will drop!");
                            result = Status.BACKOFF;
                            return result;
                        }
                        String message = this.toSyslogMessage(event);
                        if (message != null) {
                            syslogKit.sendMessage(SyslogConstants.INFO_SEVERITY, message);
                            counter.incrementEventDrainAttemptCount();
                        }
                    }
                    logger.info("SyslogSink send events {}..., channel is {}...", events.size(), channel.getName());
                } else {
                    logger.error("SyslogSink syslogKit is null!");
                    counter.incrementEventWriteFail();
                }
            } catch (Throwable e) {
                logger.error("SyslogSink send error: !", e);
                counter.incrementEventWriteFail();
            }
        }
        return result;
    }

    @Override
    public SinkCounter getSinkCounter() {
        return counter;
    }

    @Override
    public void configure(Context context) {
        synchronized (this) {
            if (context == null) {
                logger.warn("syslog config is null, will close!");
                this.syslogKit = null;
                return;
            }
            this.syslogKit = null;
            try {
                String host = context.getString(CONFIG_HOST);
                int port = context.getInteger(CONFIG_PORT, 514);
                if (IPv4Kit.isIPv4(host) && (port > 0 && port < 65536)) {
                    syslogKit = new SyslogKit(host, port);
                }
            } catch (Exception e) {
                logger.error("configure SyslogSink error: {}!", e.getMessage());
            }
            if (counter == null) {
                counter = new SinkCounter(getName());
            }
        }
    }

    private String toSyslogMessage(Event event) {
        if (event == null) {
            return null;
        }
        String devIp = event.getCollectorAddress() != null ? event.getCollectorAddress() : event.getDevAddress();
        String message = event.getOriginalLog();
        if (message == null || message.isEmpty()) {
            return null;
        }
        final StringBuilder msg = new StringBuilder(message.length() + 50);
        msg.append("@DCE ");
        msg.append("@IP=\"").append(devIp).append("\" ");
        msg.append("@MSG=\"").append(message).append("\"");
        return msg.toString();
    }
}
