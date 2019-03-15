package com.datacloudsec.source.netflow;

import com.datacloudsec.config.tools.IPv4Kit;

public abstract class NetFlowUtil {

    public static byte intToByte(int i) {
        return (byte) i;
    }

    public static int bytesToInt(byte abyte0[], int offset) {
        return (int) toNumber(abyte0, offset, 2);
    }

    public static byte[] intToBytes(int i) {
        byte abyte0[] = new byte[2];
        abyte0[1] = (byte) (0xff & i);
        abyte0[0] = (byte) ((0xff00 & i) >> 8);
        return abyte0;
    }

    public static byte[] intToBytes4(int i) {
        byte abyte0[] = new byte[4];
        abyte0[3] = (byte) (0xff & i);
        abyte0[2] = (byte) ((0xff00 & i) >> 8);
        abyte0[1] = (byte) ((0xff0000 & i) >> 16);
        abyte0[0] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    public static byte[] longToBytes8(long l) {
        byte abyte0[] = new byte[8];
        abyte0[7] = (byte) (int) (255L & l);
        abyte0[6] = (byte) (int) ((65280L & l) >> 8);
        abyte0[5] = (byte) (int) ((0xff0000L & l) >> 16);
        abyte0[4] = (byte) (int) ((0xff000000L & l) >> 24);
        abyte0[3] = (byte) (int) ((0xff00000000L & l) >> 32);
        abyte0[2] = (byte) (int) ((0xff0000000000L & l) >> 40);
        abyte0[1] = (byte) (int) ((0xff000000000000L & l) >> 48);
        abyte0[0] = (byte) (int) ((0xff00000000000000L & l) >> 56);
        return abyte0;
    }

    public static long bytes8ToLong(byte abyte0[], int offset) {
        return (255L & (long) abyte0[offset]) << 56 | (255L & (long) abyte0[offset + 1]) << 48 | (255L & (long) abyte0[offset + 2]) << 40 | (255L & (long) abyte0[offset + 3]) << 32 | (255L & (long) abyte0[offset + 4]) << 24 | (255L & (long) abyte0[offset + 5]) << 16 | (255L & (long) abyte0[offset + 6]) << 8 | (255L & (long) abyte0[offset + 7]);
    }

    public static String byteToString(byte[] b, int off, int len) {
        int done = off + len;
        StringBuilder sb = new StringBuilder("");
        for (int i = off; i < done; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString();
    }

    public static void longToBytes4(long l, byte abyte0[]) {
        abyte0[3] = (byte) (int) (255L & l);
        abyte0[2] = (byte) (int) ((65280L & l) >> 8);
        abyte0[1] = (byte) (int) ((0xff0000L & l) >> 16);
        abyte0[0] = (byte) (int) ((0xffffffffff000000L & l) >> 24);
    }

    public static void intToBytes(int i, byte abyte0[]) {
        abyte0[1] = (byte) (0xff & i);
        abyte0[0] = (byte) ((0xff00 & i) >> 8);
    }

    public static void intToBytes4(int i, byte abyte0[]) {
        abyte0[3] = (byte) (0xff & i);
        abyte0[2] = (byte) ((0xff00 & i) >> 8);
        abyte0[1] = (byte) ((0xff0000 & i) >> 16);
        abyte0[0] = (byte) (int) ((0xffffffffff000000L & (long) i) >> 24);
    }

    public static int bytes4ToInt(byte abyte0[], int offset) {
        return (0xff & abyte0[offset]) << 24 | (0xff & abyte0[offset + 1]) << 16 | (0xff & abyte0[offset + 2]) << 8 | 0xff & abyte0[offset + 3];
    }

    public static long bytes4ToLong(byte abyte0[], int offset) {
        return (255L & (long) abyte0[offset + 0]) << 24 | (255L & (long) abyte0[offset + 1]) << 16 | (255L & (long) abyte0[offset + 2]) << 8 | 255L & (long) abyte0[offset + 3];
    }

    static public final long toNumber(byte[] p, int off, int len) {
        long ret = 0;
        int done = off + len;
        for (int i = off; i < done; i++)
            ret = ((ret << 8) & 0xffffffff) + (p[i] & 0xff);

        return ret;
    }

    private static final String value(long num, String msg) {
        if (num == 0) return "";

        return (num == 1 ? "1 " + msg : num + " " + msg + "s") + ", ";
    }

    static public final String uptime(long time) {
        if (time == 0) return "0 seconds";

        if (time < 0) return time + "(Negative?!)";

        long sec = time % 60;
        long min = (time / 60) % 60;
        long hour = (time / 60 / 60) % 24;
        long day = time / 60 / 60 / 24;

        String ret = value(day, "day") + value(hour, "hour") + value(min, "minute") + value(sec, "second");
        return ret.substring(0, ret.length() - 2);
    }

    static private final char digits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    static private final String value1(long l) {
        return "" + digits[(int) (l / 10) % 10] + digits[(int) l % 10];
    }

    static public final String uptime_short(long time) {
        if (time == 0) return "00:00";

        if (time < 0) return time + "(Negative?!)";

        long sec = time % 60;
        long min = (time / 60) % 60;
        long hour = (time / 60 / 60) % 24;
        long day = time / 60 / 60 / 24;

        return value1(day) + '-' + value1(hour) + ':' + value1(min) + ':' + value1(sec);
    }

    static public final String strAddr(long addr) {
        if (ParamsManager.ip2ipsConvert) {
            return IPv4Kit.long2ip(addr);
        } else {
            return IPv4Kit.long2ip(addr);
        }
    }

    static public final String toInterval(long i) {
        if (i < 60) return i + "S";

        if (i < 3600) return (i / 60) + "M";

        return (i / 3600) + "H";
    }

    public static final long convertIPS2Long(String ip) {
        char[] c = ip.toCharArray();
        byte[] b = {0, 0, 0, 0};
        for (int i = 0, j = 0; i < c.length; ) {
            int d = (byte) (c[i] - '0');
            switch (c[i++]) {
                case '.':
                    ++j;
                    break;
                default:
                    if ((d < 0) || (d > 9)) return 0;
                    if ((b[j] & 0xff) * 10 + d > 255) return 0;
                    b[j] = (byte) (b[j] * 10 + d);
            }
        }
        return 0x00000000ffffffffl & (b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff));
    }
}
