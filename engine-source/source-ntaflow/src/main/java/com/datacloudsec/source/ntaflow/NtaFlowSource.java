package com.datacloudsec.source.ntaflow;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.COUNTER_NTAFLOW_KEY;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.FLOW_INDEX_TYPE;

public class NtaFlowSource extends AbstractSource implements EventDrivenSource, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(NtaFlowSource.class);
    private static int DEFAULT_PORT = 60002;
    private static int DEFAULT_INITIAL_SIZE = 640000;
    public static final int NTAFLOW_HEADER_SIZE = 24;
    public static final int NTAFLOW_PACKET_SIZE = 48;
    private String host = "0.0.0.0";
    private static String defaultEventType = "-1";
    private NtaFlowListener ntaFlowListener;
    private SourceCounter sourceCounter;

    public NtaFlowSource() {
        super();
    }

    @Override
    public void configure(Context context) {
        Integer eventSize = context.getInteger(NtaflowSourceConfigurationConstants.CONFIG_EVENTSIZE);
        if (eventSize != null && eventSize > 0) {
            DEFAULT_INITIAL_SIZE = eventSize;
        }
        Integer port = context.getInteger(NtaflowSourceConfigurationConstants.CONFIG_PORT);
        if (port != null && port > 0) {
            DEFAULT_PORT = port;
        }
        String eventType = context.getString(NtaflowSourceConfigurationConstants.DEFAULT_EVENT_TYPE);
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
        ntaFlowListener = new NtaFlowListener();
        ntaFlowListener.start();
        sourceCounter.start();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        ntaFlowListener.interrupt();
        sourceCounter.stop();
    }

    class NtaFlowListener extends AbstractDatagramSocketSource {
        public void run() {
            try {
                DatagramSocket ds = isLocalhost(host) ? new DatagramSocket(new InetSocketAddress(DEFAULT_PORT)) : new DatagramSocket(new InetSocketAddress(host, DEFAULT_PORT));
                ds.setReceiveBufferSize(DEFAULT_INITIAL_SIZE);
                ds.setSoTimeout(1000);
                setDatagramSocket(ds);
                DatagramPacket p = new DatagramPacket(new byte[8 * 1024], 8 * 1024);
                logger.info("##########################NtaFlowListener listen at " + host + ":" + DEFAULT_PORT);
                while (getLifecycleState() == LifecycleState.START) {
                    try {
                        ds.receive(p);
                        process(p);
                    } catch (ChannelException ex) {
                        logger.error("NtaFlowSource Error writting to channel", ex);
                        sourceCounter.incrementChannelWriteFail();
                    }catch (SocketTimeoutException ex){

                    }catch (Exception ex) {
                        logger.error("NtaFlowSource Error parsing event from syslog stream, event dropped", ex);
                    }
                }
            } catch (Throwable e) {
                logger.error("Can't start NtaFlow udp collector: ", e);
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
        sourceCounter.incrementAppendBatchReceivedCount();
        CounterKit.increaseOne(COUNTER_NTAFLOW_KEY + hostAddress);

        // 判断该IP是否允许采集
        if (!DcFlowConfig.isIpAllowed(hostAddress)) {
            sourceCounter.incrementEventDropCount();
            return;
        }

        List<Event> events = new ArrayList<>();
        final byte[] buf = packet.getData();
        int len = packet.getLength();

        if (len < NTAFLOW_HEADER_SIZE) {
            logger.error("incomplete header length or data body length");
        }
        long count = NetFlowUtil.toNumber(buf, 2, 2);
        if (count <= 0 || len != NTAFLOW_HEADER_SIZE + count * NTAFLOW_PACKET_SIZE) {
            logger.error("NtaFlow packet length exception");
        }
        for (int i = 0, p = NTAFLOW_HEADER_SIZE; i < count; i++, p += NTAFLOW_PACKET_SIZE) {
            try {
                NtaFlow ntaFlow = new NtaFlow(buf, p);
                Event e = new Event();
                e.setEventUUID(UUIDUtil.generateShortUUID());
                e.setSrcMac(ntaFlow.getSrcMac());
                e.setDstMac(ntaFlow.getDstMac());
                e.setSrcAddress(ntaFlow.getSrcIp());
                e.setDstAddress(ntaFlow.getDstIp());
                e.setSrcPort(ntaFlow.getSrcPort());
                e.setDstPort(ntaFlow.getDstPort());
                e.setNetProtocol(ntaFlow.l3Proto);
                e.setTos(String.valueOf(ntaFlow.getIpv4Tos()));
                e.setTranProtocol(ntaFlow.getTransProto());
                e.setAppProtocol(ntaFlow.getAppProto());
                e.setSendPackage(ntaFlow.getPacketCounts());
                e.setSendByte(ntaFlow.getByteCounts());
                e.setOccurTime(System.currentTimeMillis());

                e.setId(TimeIdUtil.generator(e.getOccurTime()));
                e.setReceiveTime(System.currentTimeMillis());
                e.setIndexType(FLOW_INDEX_TYPE);
                e.setOriginalLog(BeanKit.toString(ntaFlow));
                String address = packet.getAddress().getHostAddress();
                e.setCollectorAddress(address);
                e.setDevAddress(address);
                e.setEventType(defaultEventType);
                events.add(e);
            } catch (RuntimeException ex) {
                logger.error("Error parsing event from syslog stream, event dropped", ex);
                sourceCounter.incrementEventReadFail();
            }
        }
        sourceCounter.addToEventReceivedCount(events.size());

        this.getChannelProcessor().processEventBatch(events);

    }

    public static class NtaFlow {
        private String srcMac;
        private String dstMac;
        private String srcIp;
        private String dstIp;
        private int srcPort;
        private int dstPort;
        private String l3Proto;
        private int ipv4Tos;
        private int transProto;
        private String appProto;
        private long packetCounts;
        private long byteCounts;
        private String castType;
        private String streamType;

        public NtaFlow() {
            super();
        }

        public NtaFlow(byte[] buf, int off) {
            super();
            this.srcMac = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 0, 6));
            this.dstMac = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 6, 6));
            this.srcIp = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 12, 4));
            this.dstIp = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 16, 4));
            this.srcPort = (int) NetFlowUtil.toNumber(buf, off + 20, 2);
            this.dstPort = (int) NetFlowUtil.toNumber(buf, off + 22, 2);
            this.l3Proto = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 24, 2));
            this.ipv4Tos = (int) NetFlowUtil.toNumber(buf, off + 26, 1);
            this.transProto = (int) NetFlowUtil.toNumber(buf, off + 27, 1);
            this.appProto = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 28, 2));
            this.packetCounts = NetFlowUtil.toNumber(buf, off + 30, 8);
            this.byteCounts = NetFlowUtil.toNumber(buf, off + 38, 8);
            this.castType = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 46, 1));
            this.streamType = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 47, 1));
        }

        /**
         * @return the srcMac
         */
        public String getSrcMac() {
            return srcMac;
        }

        /**
         * @return the dstMac
         */
        public String getDstMac() {
            return dstMac;
        }

        /**
         * @return the srcIp
         */
        public String getSrcIp() {
            return srcIp;
        }

        /**
         * @return the dstIp
         */
        public String getDstIp() {
            return dstIp;
        }

        /**
         * @return the srcPort
         */
        public int getSrcPort() {
            return srcPort;
        }

        /**
         * @return the dstPort
         */
        public int getDstPort() {
            return dstPort;
        }

        /**
         * @return the l3Proto
         */
        public String getL3Proto() {
            return l3Proto;
        }

        /**
         * @return the ipv4Tos
         */
        public Integer getIpv4Tos() {
            return ipv4Tos;
        }

        /**
         * @return the transProto
         */
        public int getTransProto() {
            return transProto;
        }

        /**
         * @return the appProto
         */
        public String getAppProto() {
            return appProto;
        }

        /**
         * @return the packetCounts
         */
        public long getPacketCounts() {
            return packetCounts;
        }

        /**
         * @return the byteCounts
         */
        public long getByteCounts() {
            return byteCounts;
        }

        /**
         * @return the castType
         */
        public String getCastType() {
            return castType;
        }

        /**
         * @return the streamType
         */
        public String getStreamType() {
            return streamType;
        }

    }

    private boolean isLocalhost(String ip) {
        return "0.0.0.0".equals(ip) || "127.0.0.1".equals(ip) || "::0".equals(ip) || "::".equals(ip);
    }

    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }
}
