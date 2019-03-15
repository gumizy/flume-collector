package com.datacloudsec.source.netflow.bean;

import com.datacloudsec.source.netflow.NetFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V5Packet extends AbstractPacket {

    private static final Logger logger = LoggerFactory.getLogger(V5Packet.class);
    private V5Flow v5Flow;
    public static final int V5_HEADER_SIZE = 24;
    public static final int V5FLOW_SIZE = 48;

    public V5Packet() {
        super();
    }

    /**
     * @param routerIP routerIP
     * @param buf buf
     * @param len len
     * @throws Exception Exception
     */
    public V5Packet(String routerIP, byte[] buf, int len) throws Exception {
        if (len < V5_HEADER_SIZE) {
            logger.error("incomplete header length or data body length");
            throw new Exception("incomplete header");
        }
        long count = NetFlowUtil.toNumber(buf, 2, 2);
        if (count <= 0 || len != V5_HEADER_SIZE + count * V5FLOW_SIZE) {
            logger.error("NetFlow V5 packet length exception");
        }

        for (int i = 0, p = V5_HEADER_SIZE; i < count; i++, p += V5FLOW_SIZE) {
            try {
                v5Flow = new V5Flow(routerIP, buf, p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the v5Flow
     */
    public V5Flow getFlow() {
        return v5Flow;
    }
}
