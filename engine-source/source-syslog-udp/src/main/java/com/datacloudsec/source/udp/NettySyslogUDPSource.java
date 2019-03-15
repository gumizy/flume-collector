package com.datacloudsec.source.udp;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.source.SourceLogMessage;
import com.datacloudsec.core.EventDrivenSource;
import com.datacloudsec.core.ParserException;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.conf.Configurables;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.parser.PollableParserRunner;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.parser.EventParserProcessor;
import com.google.common.annotations.VisibleForTesting;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.oio.OioDatagramChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NettySyslogUDPSource extends AbstractSource implements EventDrivenSource, Configurable {

    private int port;
    private int maxsize = 1 << 16; // 64k is max allowable in RFC 5426
    private String host = null;
    private Channel nettyChannel;

    private static final Logger logger = LoggerFactory.getLogger(NettySyslogUDPSource.class);

    private SourceCounter sourceCounter;
    // Default Min size
    public static final int DEFAULT_MIN_SIZE = 2048;
    public static final int DEFAULT_INITIAL_SIZE = DEFAULT_MIN_SIZE;

    public NettySyslogUDPSource() {
        super();
    }

    public class syslogHandler extends SimpleChannelHandler {
        private SyslogUtils syslogUtils = new SyslogUtils(port);

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent mEvent) {
            try {
                SourceLogMessage msg;
                sourceCounter.incrementEventReceivedCount();
                ChannelBuffer message = (ChannelBuffer) mEvent.getMessage();
                msg = syslogUtils.extractFlipSourceMessage(message.toByteBuffer(), syslogUtils.getIP(mEvent.getRemoteAddress()));

                if (msg == null) {
                    return;
                }

                parserProcessor.put(msg);
                sourceCounter.incrementEventAcceptedCount();
            } catch (ParserException ex) {
                logger.error("Error writting to parser for channel", ex);
                sourceCounter.incrementChannelWriteFail();
                return;
            } catch (Exception ex) {
                logger.error("Error parsing event from syslog stream, event dropped", ex);
                sourceCounter.incrementEventReadFail();
                return;
            }
        }
    }

    @Override
    public void start() {
        parserRunner = new PollableParserRunner();
        parserRunner.setPolicy(parserProcessor);
        parserRunner.start();
        // setup Netty com.datacloudsec.bootstrap.server
        ConnectionlessBootstrap serverBootstrap = new ConnectionlessBootstrap(new OioDatagramChannelFactory(Executors.newCachedThreadPool()));
        final syslogHandler handler = new syslogHandler();
        serverBootstrap.setOption("receiveBufferSizePredictorFactory", new AdaptiveReceiveBufferSizePredictorFactory(DEFAULT_MIN_SIZE, DEFAULT_INITIAL_SIZE, maxsize));
        serverBootstrap.setPipelineFactory(() -> Channels.pipeline(handler));

        if (host == null) {
            nettyChannel = serverBootstrap.bind(new InetSocketAddress(port));
        } else {
            nettyChannel = serverBootstrap.bind(new InetSocketAddress(host, port));
        }
        logger.info("##########################NettySyslogUDPSource listen at " + host + ":" + port);

        sourceCounter.start();
        super.start();
    }

    @Override
    public void stop() {
        logger.info("Netty Syslog UDP Source stopping...");
        logger.info("Metrics: {}", sourceCounter);
        if (nettyChannel != null) {
            nettyChannel.close();
            try {
                nettyChannel.getCloseFuture().await(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("netty com.datacloudsec.bootstrap.server stop interrupted", e);
            } finally {
                nettyChannel = null;
            }
        }
        parserRunner.stop();
        sourceCounter.stop();
        super.stop();
    }

    @Override
    public void configure(Context context) {
        Configurables.ensureRequiredNonNull(context, SyslogSourceConfigurationConstants.CONFIG_PORT);
        port = context.getInteger(SyslogSourceConfigurationConstants.CONFIG_PORT);
        host = context.getString(SyslogSourceConfigurationConstants.CONFIG_HOST);

        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
        parserProcessor = new EventParserProcessor(this);
        parserProcessor.configure(context);
    }

    @VisibleForTesting
    InetSocketAddress getBoundAddress() {
        SocketAddress localAddress = nettyChannel.getLocalAddress();
        if (!(localAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Not bound to an internet address");
        }
        return (InetSocketAddress) localAddress;
    }

    @VisibleForTesting
    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }
}
