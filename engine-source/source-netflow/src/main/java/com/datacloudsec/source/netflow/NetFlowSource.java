package com.datacloudsec.source.netflow;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.source.SourceLogMessage;
import com.datacloudsec.config.tools.BeanKit;
import com.datacloudsec.config.tools.CounterKit;
import com.datacloudsec.config.tools.TimeIdUtil;
import com.datacloudsec.config.tools.UUIDUtil;
import com.datacloudsec.core.ChannelException;
import com.datacloudsec.core.EventDrivenSource;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.conf.DcFlowConfig;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.datacloudsec.core.source.AbstractDatagramSocketSource;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.source.netflow.bean.V5Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.COUNTER_FLOW_KEY;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.FLOW_INDEX_TYPE;

public class NetFlowSource extends AbstractSource implements EventDrivenSource, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(NetFlowSource.class);

    private static int DEFAULT_PORT = 60001;

    private static int DEFAULT_INITIAL_SIZE = 1000000;

    public static final int V5_HEADER_SIZE = 24;

    public static final int V5_FLOW_SIZE = 48;

    private String host = "0.0.0.0";

    protected static LinkedBlockingQueue<SourceLogMessage> queue;

    public static int getQueueSize() {
        return queue != null ? queue.size() : 0;
    }

    public static final int V5FLOW_SIZE = 48;

    private static String defaultEventType = "-1";

    public NetFlowSource() {
        super();
    }

    private NetFlowListener netFlowListener;

    private SourceCounter sourceCounter;

    @Override
    public void configure(Context context) {
        Integer eventSize = context.getInteger(NetflowConfigurationConstants.CONFIG_EVENTSIZE);
        if (eventSize != null && eventSize > 0) {
            DEFAULT_INITIAL_SIZE = eventSize;
        }
        Integer port = context.getInteger(NetflowConfigurationConstants.CONFIG_PORT);
        if (port != null && port > 0) {
            DEFAULT_PORT = port;
        }
        String eventType = context.getString(NetflowConfigurationConstants.DEFAULT_EVENT_TYPE);
        if (eventType != null) {
            defaultEventType = eventType;
        }
        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        netFlowListener = new NetFlowListener();
        netFlowListener.start();
        sourceCounter.start();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        netFlowListener.interrupt();
        sourceCounter.stop();
    }

    class NetFlowListener extends AbstractDatagramSocketSource {
        public void run() {
            try {
                DatagramSocket ds = isLocalhost(host) ? new DatagramSocket(new InetSocketAddress(DEFAULT_PORT)) : new DatagramSocket(new InetSocketAddress(host, DEFAULT_PORT));
                ds.setReceiveBufferSize(DEFAULT_INITIAL_SIZE);
                ds.setSoTimeout(2000);
                setDatagramSocket(ds);
                DatagramPacket p = new DatagramPacket(new byte[8 * 1024], 8 * 1024);
                logger.info("##########################NetFlowListener listen at " + host + ":" + DEFAULT_PORT);
                while (getLifecycleState() == LifecycleState.START) {
                    try {
                        ds.receive(p);
                        process(p);
                    } catch (ChannelException ex) {
                        logger.error("Error NetFlowListener writting to channel", ex);
                        sourceCounter.incrementChannelWriteFail();
                    } catch (SocketTimeoutException ex) {

                    } catch (Exception ex) {
                        logger.error("Error NetFlowListener parsing event from syslog stream, event dropped", ex);
                    }
                }
                logger.warn("##########################Will end NetFlowListener!!!");
            } catch (Throwable e) {
                logger.error("=============================Can't start NetFlow udp collector: ", e);
            } finally {
                try {
                    if (ds != null) ds.close();
                } catch (Exception e) {
                    logger.error("socket close error: ", e);
                }
            }
        }

    }

    private void process(DatagramPacket packet) {
        // 计数
        String hostAddress = packet.getAddress().getHostAddress();
        CounterKit.increaseOne(COUNTER_FLOW_KEY + hostAddress);

        // 判断该IP是否允许采集
        if (!DcFlowConfig.isIpAllowed(hostAddress)) {
            sourceCounter.incrementEventDropCount();
            return;
        }

        List<Event> events = wrapAllVersionEvent(packet);
        if (events != null) {
            sourceCounter.addToEventReceivedCount(events.size());
            sourceCounter.incrementAppendBatchReceivedCount();
            this.getChannelProcessor().processEventBatch(events);
        }
    }

    private List<Event> wrapAllVersionEvent(DatagramPacket packet) {
        //暂时只支持版本5
        long version = NetFlowUtil.toNumber(packet.getData(), 0, 2);
        if (version == 5) {
            return wrapV5Event(packet);
        } else {
            sourceCounter.incrementEventDropCount();
        }
        return null;
    }

    private List<Event> wrapV5Event(DatagramPacket packet) {
        List<Event> events = new ArrayList<>();
        final byte[] buf = packet.getData();
        int len = packet.getLength();
        if (len < V5_HEADER_SIZE) {
            logger.error("incomplete header length or data body length");
        }
        long count = NetFlowUtil.toNumber(buf, 2, 2);
        if (count <= 0 || len != V5_HEADER_SIZE + count * V5FLOW_SIZE) {
            logger.error("NetFlow V5 packet length exception");
        }
        //从头信息中读取当前时间、应用启动时间
        long unixSecs = NetFlowUtil.toNumber(buf, 8, 4); // 秒数
        long unixNsecs = NetFlowUtil.toNumber(buf, 12, 4);// 余的纳秒数
        Date occurTime = new Date(unixSecs * 1000 + unixNsecs / 1000000);// 精确到毫秒级别
        long sysUptime = NetFlowUtil.toNumber(buf, 4, 4);
        Date uptime = new Date(occurTime.getTime() - sysUptime);
        for (int i = 0, off = V5_HEADER_SIZE; i < count; i++, off += V5FLOW_SIZE) {
            try {
                V5Flow v5Flow = new V5Flow(packet.getAddress().getHostAddress().trim(), buf, off);
                Event e = new Event();
                e.setEventUUID(UUIDUtil.generateShortUUID());
                e.setOccurTime(occurTime.getTime());
                e.setSysUpTime(uptime.getTime());
                e.setSrcAddress(v5Flow.getSrcaddr());
                e.setDstAddress(v5Flow.getDstaddr());
                e.setNexthop(v5Flow.getNexthop());
                e.setInput((int) v5Flow.getInput());
                e.setOutput((int) v5Flow.getOutput());
                e.setSendPackage(v5Flow.getDpkts());
                e.setSendByte(v5Flow.getDoctets());
                e.setFirstTime(v5Flow.getFirst());
                e.setEndTime(v5Flow.getLast());
                e.setSrcPort((int) v5Flow.getSrcport());
                e.setDstPort((int) v5Flow.getDstport());
                e.setTcpFlags(v5Flow.getTcp_flags());
                e.setTranProtocol((int) v5Flow.getProt());
                e.setTos(String.valueOf(v5Flow.getTos()));
                e.setSrcMask(String.valueOf(v5Flow.getSrc_mask()));
                e.setDstMask(String.valueOf(v5Flow.getDst_mask()));
                e.setSrcAs(String.valueOf(v5Flow.getSrc_as()));
                e.setDstAs(String.valueOf(v5Flow.getDst_as()));
                e.setId(TimeIdUtil.generator(e.getOccurTime()));
                e.setReceiveTime(System.currentTimeMillis());
                e.setIndexType(FLOW_INDEX_TYPE);
                String address = packet.getAddress().getHostAddress();
                e.setCollectorAddress(address);
                e.setDevAddress(address);
                e.setEventType(defaultEventType);
                transTime(e);
                e.setOriginalLog(BeanKit.toString(v5Flow));
                events.add(e);
            } catch (Exception e) {
                if (sourceCounter.getEventDropCount() == 0) {
                    logger.error("NetFlow error: " + e.getMessage(), e);
                } else {
                    logger.error("NetFlow error: " + e.getMessage());
                }
            }
        }
        return events;
    }

    private void transTime(Event e) {
        // first
        e.setFirstTime(e.getSysUpTime() + e.getFirstTime());
        e.setEndTime(e.getSysUpTime() + e.getEndTime());
        if (logger.isDebugEnabled()) {
            logger.debug("occur time:{}", new Date(e.getOccurTime()));
            logger.debug("sys up time:{}", new Date(e.getSysUpTime()));
            logger.debug("first time:{}", new Date(e.getFirstTime()));
            logger.debug("end time:{}", new Date(e.getEndTime()));
        }
    }

    private boolean isLocalhost(String ip) {
        return "0.0.0.0".equals(ip) || "127.0.0.1".equals(ip) || "::0".equals(ip) || "::".equals(ip);
    }

    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }
}
