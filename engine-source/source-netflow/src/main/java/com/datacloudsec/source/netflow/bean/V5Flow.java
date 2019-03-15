package com.datacloudsec.source.netflow.bean;

import com.datacloudsec.source.netflow.NetFlowUtil;

public class V5Flow extends AbstractFlow {
    private String srcaddr = "";
    private String dstaddr = "";
    private String nexthop = "";

    private Prefix srcprefix;
    private Prefix dstprefix;

    private long input = -1;
    private long output = -1;

    private long dpkts = 0;
    private long doctets = 0;
    private long first = 0;
    private long last = 0;

    private long srcport = -1;
    private long dstport = -1;

    private byte tcp_flags = 0;
    private byte prot = -1;
    private byte tos = 0;

    private long src_as = -1;
    private long dst_as = -1;

    private byte src_mask = 0;
    private byte dst_mask = 0;

    private String routerIP = "";

    public V5Flow() {
        super();
    }

    public V5Flow(String routerIP, byte[] buf, int off) throws Exception {
        this.routerIP = routerIP;
        srcaddr = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 0, 4));
        dstaddr = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 4, 4));
        nexthop = NetFlowUtil.strAddr(NetFlowUtil.toNumber(buf, off + 8, 4));

        input = NetFlowUtil.toNumber(buf, off + 12, 2);
        output = NetFlowUtil.toNumber(buf, off + 14, 2);
        dpkts = NetFlowUtil.toNumber(buf, off + 16, 4);
        doctets = NetFlowUtil.toNumber(buf, off + 20, 4);
        first = NetFlowUtil.toNumber(buf, off + 24, 4);
        last = NetFlowUtil.toNumber(buf, off + 28, 4);
        srcport = NetFlowUtil.toNumber(buf, off + 32, 2);
        dstport = NetFlowUtil.toNumber(buf, off + 34, 2);
        tcp_flags = buf[off + 37];
        prot = buf[off + 38];
        tos = buf[off + 39];
        src_as = NetFlowUtil.toNumber(buf, off + 40, 2);
        dst_as = NetFlowUtil.toNumber(buf, off + 42, 2);
        src_mask = buf[off + 44];
        dst_mask = buf[off + 45];
    }

    /**
     * @return the srcaddr
     */
    public String getSrcaddr() {
        return srcaddr;
    }

    /**
     * @return the dstaddr
     */
    public String getDstaddr() {
        return dstaddr;
    }

    /**
     * @return the nexthop
     */
    public String getNexthop() {
        return nexthop;
    }

    /**
     * @return the srcprefix
     */
    public Prefix getSrcprefix() {
        return srcprefix;
    }

    /**
     * @return the dstprefix
     */
    public Prefix getDstprefix() {
        return dstprefix;
    }

    /**
     * @return the input
     */
    public long getInput() {
        return input;
    }

    /**
     * @return the output
     */
    public long getOutput() {
        return output;
    }

    /**
     * @return the dpkts
     */
    public long getDpkts() {
        return dpkts;
    }

    /**
     * @return the doctets
     */
    public long getDoctets() {
        return doctets;
    }

    /**
     * @return the first
     */
    public long getFirst() {
        return first;
    }

    /**
     * @return the last
     */
    public long getLast() {
        return last;
    }

    /**
     * @return the srcport
     */
    public long getSrcport() {
        return srcport;
    }

    /**
     * @return the dstport
     */
    public long getDstport() {
        return dstport;
    }

    /**
     * @return the tcp_flags
     */
    public byte getTcp_flags() {
        return tcp_flags;
    }

    /**
     * @return the prot
     */
    public byte getProt() {
        return prot;
    }

    /**
     * @return the tos
     */
    public byte getTos() {
        return tos;
    }

    /**
     * @return the src_as
     */
    public long getSrc_as() {
        return src_as;
    }

    /**
     * @return the dst_as
     */
    public long getDst_as() {
        return dst_as;
    }

    /**
     * @return the src_mask
     */
    public byte getSrc_mask() {
        return src_mask;
    }

    /**
     * @return the dst_mask
     */
    public byte getDst_mask() {
        return dst_mask;
    }

    /**
     * @return the routerIP
     */
    public String getRouterIP() {
        return routerIP;
    }

}
