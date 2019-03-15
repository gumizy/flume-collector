package com.datacloudsec.config.geo;

/**
 * GeoUtil
 */
public class GeoUtil {

    /**
     * write specified bytes to a byte array start from offset
     *
     * @param b      b
     * @param offset offset
     * @param v      v
     * @param bytes  bytes
     */
    public static void write(byte[] b, int offset, long v, int bytes) {
        for (int i = 0; i < bytes; i++) {
            b[offset++] = (byte) ((v >>> (8 * i)) & 0xFF);
        }
    }

    /**
     * write a int to a byte array
     *
     * @param b      b
     * @param offset offset
     * @param v      v
     */
    public static void writeIntLong(byte[] b, int offset, long v) {
        b[offset++] = (byte) ((v) & 0xFF);
        b[offset++] = (byte) ((v >> 8) & 0xFF);
        b[offset++] = (byte) ((v >> 16) & 0xFF);
        b[offset] = (byte) ((v >> 24) & 0xFF);
    }

    /**
     * get a int from a byte array start from the specified offset
     *
     * @param b      b
     * @param offset offset
     */
    public static long getIntLong(byte[] b, int offset) {
        return (
                ((b[offset++] & 0x000000FFL)) |
                        ((b[offset++] << 8) & 0x0000FF00L) |
                        ((b[offset++] << 16) & 0x00FF0000L) |
                        ((b[offset] << 24) & 0xFF000000L)
        );
    }

    /**
     * get a int from a byte array start from the specified offset
     *
     * @param b      b
     * @param offset offset
     */
    public static int getInt3(byte[] b, int offset) {
        return (
                (b[offset++] & 0x000000FF) |
                        (b[offset++] & 0x0000FF00) |
                        (b[offset] & 0x00FF0000)
        );
    }

    public static int getInt2(byte[] b, int offset) {
        return (
                (b[offset++] & 0x000000FF) |
                        (b[offset] & 0x0000FF00)
        );
    }

    public static int getInt1(byte[] b, int offset) {
        return (
                (b[offset] & 0x000000FF)
        );
    }

    /**
     * string ip to long ip
     *
     * @param ip ip
     * @return long
     */
    public static long ip2long(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) return 0;

        int p1 = ((Integer.valueOf(p[0]) << 24) & 0xFF000000);
        int p2 = ((Integer.valueOf(p[1]) << 16) & 0x00FF0000);
        int p3 = ((Integer.valueOf(p[2]) << 8) & 0x0000FF00);
        int p4 = ((Integer.valueOf(p[3])) & 0x000000FF);

        return ((p1 | p2 | p3 | p4) & 0xFFFFFFFFL);
    }

    /**
     * int to ip string
     *
     * @param ip ip
     * @return string
     */
    public static String long2ip(long ip) {
        StringBuilder sb = new StringBuilder();

        sb
                .append((ip >> 24) & 0xFF).append('.')
                .append((ip >> 16) & 0xFF).append('.')
                .append((ip >> 8) & 0xFF).append('.')
                .append((ip) & 0xFF);

        return sb.toString();
    }

    /**
     * check the validate of the specified ip address
     *
     * @param ip ip
     * @return boolean
     */
    public static boolean isIp(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) return false;

        for (String pp : p) {
            if (pp.length() > 3) return false;
            int val = Integer.valueOf(pp);
            if (val <0 || val > 255) return false;
        }

        return true;
    }
}
