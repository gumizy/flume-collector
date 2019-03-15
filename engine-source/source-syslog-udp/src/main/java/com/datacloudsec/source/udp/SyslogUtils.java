

package com.datacloudsec.source.udp;

import com.datacloudsec.config.conf.source.SourceLogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

public class SyslogUtils {
    private static final Logger logger = LoggerFactory.getLogger(SyslogUtils.class);
    private int port;

    public SyslogUtils(int port) {
        this.port = port;
    }

    public static String getIP(SocketAddress socketAddress) {
        try {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            String ip = inetSocketAddress.getAddress().getHostAddress();
            if (ip != null) {
                return ip;
            } else {
                throw new NullPointerException("The returned IP is null");
            }
        } catch (Exception e) {
            logger.warn("Unable to retrieve client IP address", e);
        }
        return "";
    }

    public static String getHostname(SocketAddress socketAddress) {
        try {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            String hostname = inetSocketAddress.getHostName();
            if (hostname != null) {
                return hostname;
            } else {
                throw new NullPointerException("The returned hostname is null");
            }
        } catch (Exception e) {
            logger.warn("Unable to retrieve client hostname", e);
        }
        return "";
    }

    public byte[] conver(ByteBuffer byteBuffer) {
        int len = byteBuffer.limit() - byteBuffer.position();
        byte[] bytes = new byte[len];

        if (byteBuffer.isReadOnly()) {
            return null;
        } else {
            byteBuffer.get(bytes);
        }
        return bytes;
    }

    public SourceLogMessage extractFlipSourceMessage(ByteBuffer byteBuffer, String receiveIp) {
        byteBuffer.flip();
        return extractSourceMessage(byteBuffer, receiveIp);
    }

    public SourceLogMessage extractSourceMessage(ByteBuffer byteBuffer, String receiveIp) {
        SourceLogMessage sourceLogMsg = new SourceLogMessage(receiveIp);
        sourceLogMsg.setHeaders(SYSLOG_OPEN_PORT, String.valueOf(port));
        sourceLogMsg.setHeaders(PARSER_IDENTIFICATION, receiveIp + COLLECT_TYPE_SEPARATOR + COLLECT_TYPE_SYSLOGUDP);

        sourceLogMsg.setBody(conver(byteBuffer));
        return sourceLogMsg;
    }
}


