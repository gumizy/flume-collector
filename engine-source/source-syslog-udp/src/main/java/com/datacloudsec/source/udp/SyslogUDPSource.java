package com.datacloudsec.source.udp;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.source.SourceLogMessage;
import com.datacloudsec.config.tools.CounterKit;
import com.datacloudsec.core.EventDrivenSource;
import com.datacloudsec.core.ParserException;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.datacloudsec.core.parser.PollableParserRunner;
import com.datacloudsec.core.source.AbstractDatagramSocketSource;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.parser.EventParserProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.COUNTER_SYSLOG_KEY;

/**
 * SyslogUDPSource
 */
public class SyslogUDPSource extends AbstractSource implements EventDrivenSource, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(SyslogUDPSource.class);

    private static final int DEFAULT_INITIAL_SIZE = 640000;

    private String host = "0.0.0.0";
    private int port = 514;
    private SyslogListener syslogListener;
    private SyslogUtils syslogUtils;
    private SourceCounter sourceCounter;

    public SyslogUDPSource() {
        super();
    }

    @Override
    public void start() {
        parserRunner = new PollableParserRunner();
        parserRunner.setPolicy(parserProcessor);
        parserRunner.start();
        super.start();
        logger.info("lifeCycleState is {}", this.getLifecycleState());
        syslogListener = new SyslogListener();
        syslogListener.start();
    }

    @Override
    public void stop() {
        logger.info("Will stop SyslogUDPSource...");
        try {
            super.stop();
            syslogListener.interrupt();
            parserRunner.stop();
        } catch (Exception e) {
            logger.error("Close SyslogUDPSource error: {}", e.getMessage());
        }
        sourceCounter.stop();
        logger.info("SyslogUDPSource stopped!");
    }

    @Override
    public void configure(Context context) {
        String host = context.getString(SyslogSourceConfigurationConstants.CONFIG_HOST);
        if (host != null && !host.isEmpty()) {
            this.host = host;
        }
        Integer port = context.getInteger(SyslogSourceConfigurationConstants.CONFIG_PORT);
        if (port != null && port > 0 && port < 65536) {
            this.port = port;
        }
        syslogUtils = new SyslogUtils(port);
        parserProcessor = new EventParserProcessor(this);
        parserProcessor.configure(context);
        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
    }

    private boolean isLocalhost(String ip) {
        return "0.0.0.0".equals(ip) || "127.0.0.1".equals(ip) || "::0".equals(ip) || "::".equals(ip);
    }

    /**
     * Syslog数据接收器
     */
    public class SyslogListener extends AbstractDatagramSocketSource {

        @Override
        public void run() {
            try {
                logger.info("##########################Will start SyslogListener for {}:{}...", host, port);
                DatagramSocket ds = isLocalhost(host) ? new DatagramSocket(new InetSocketAddress(port)) : new DatagramSocket(new InetSocketAddress(host, port));
                ds.setReceiveBufferSize(DEFAULT_INITIAL_SIZE);
                ds.setSoTimeout(1000);
                setDatagramSocket(ds);
                DatagramPacket p = new DatagramPacket(new byte[2 * 1024], 2 * 1024);
                while (getLifecycleState() == LifecycleState.START) {
                    try {
                        ds.receive(p);
                        process(p);
                    } catch (SocketTimeoutException e) {

                    } catch (ParserException ex) {
                        logger.error("Error writting to parser for channel", ex);
                        sourceCounter.incrementChannelWriteFail();
                    } catch (Exception ex) {
                        logger.error("Error parsing event from syslog stream, event dropped", ex);
                        sourceCounter.incrementEventReadFail();
                    }
                }
                logger.warn("##########################Will end SyslogListener!!!");
            } catch (Throwable e) {
                logger.error("SyslogListener error: {}", e.getMessage());
            } finally {
                try {
                    if (ds != null) {
                        ds.close();
                    }
                } catch (Exception e) {
                    logger.error("socket close error: ", e);
                }
            }
        }

    }

    private void process(DatagramPacket packet) {
        // 计数
        String hostAddress = packet.getAddress().getHostAddress();
        sourceCounter.incrementEventReceivedCount();
        CounterKit.increaseOne(COUNTER_SYSLOG_KEY + hostAddress);

        // 判断发送IP是有有对应的解析规则
//        if (!PraserProcessorCache.getValidateIpSet().contains(hostAddress)) {
//            sourceCounter.incrementEventDropCount();
//            return;
//        }

        // SourceLogMessage
        int length = packet.getLength();
        ByteBuffer buf = ByteBuffer.allocate(length);
        System.arraycopy(packet.getData(), 0, buf.array(), 0, length);
        buf.limit(length);
        buf.position(0);
        SourceLogMessage msg = syslogUtils.extractSourceMessage(buf, hostAddress);
        parserProcessor.put(msg);
    }

    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }
}
