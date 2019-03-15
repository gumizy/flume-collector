package com.datacloudsec.config.tools;

import org.graylog2.syslog4j.Syslog;
import org.graylog2.syslog4j.SyslogIF;

/**
 * SyslogKit
 */
public class SyslogKit {

    private String host;
    private int port = 514;
    private String charset = "UTF-8";

    public SyslogKit(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SyslogKit(String host, int port, String charset) {
        this.host = host;
        this.port = port;
        this.charset = charset;
    }

    public void sendMessage(int level, String message) {
        if (message == null) {
            return;
        }
        if (checkConfig()) {
            SyslogIF syslog = Syslog.getInstance("UDP");
            syslog.getConfig().setHost(host);
            syslog.getConfig().setPort(port);
            syslog.log(level, message);
        }

    }

    private boolean checkConfig() {
        return host != null && (port > 0 && port < 65536);
    }
    public void send(String message){

    }
}
