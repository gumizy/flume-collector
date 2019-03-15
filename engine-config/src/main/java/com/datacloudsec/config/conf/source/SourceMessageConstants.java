package com.datacloudsec.config.conf.source;

import java.util.regex.Pattern;

/**
 * @Date 2019/1/17 20:10
 */
public final class SourceMessageConstants {
    public static final String NORMALIZATION = "normalization";// 归一化,是否解析原始日志，默认解析，需要在source配置属性
    public static final String FORWARD = "forward";// 是否转发，默认不转发，需要在source配置属性
    public static final String FORWARD_CHARSET = "forwardCharset";// 转发编码，默认UTF-8
    // 转发模式的syslog格式
    public static final String FORWARD_REGEX = ".*@DCE @IP=\"([0-9a-fA-F.:]+)\" @MSG=\"(.*)\"";

    // 转发模式的syslog正则
    public static final Pattern FORWARD_PATTERN = Pattern.compile(FORWARD_REGEX);

    public static final String RECEIVE_TIME = "receive_time";// 是否被解析过
    public static final String SEND_HOST_IP = "send_host_ip";// 接受的主机ip地址
    public static final String PARSER_IDENTIFICATION = "parser_identification";// 解析器获取标识
    public static final String SYSLOG_OPEN_PORT = "syslog_open_port";// syslog 开放端口
    public static final String NETFLOW_OPEN_PORT = "netflow_open_port";// netflow开放端口
    public static final String NTAFLOW_OPEN_PORT = "ntaflow_open_port";// ntaflow开放端口

    public static final String FTPFILEPATH = "ftp_file_path";// ftp采集路径
    public static final String FTPFILENAME = "ftp_file_name";// ftp采集路径

    /**
     * 1-syslogudp
     * 2-jdbc
     * 3-ftp
     * 4-http
     * 5-kafka
     */
    public static final String COLLECT_TYPE_SEPARATOR = "-";
    public static final Integer COLLECT_TYPE_SYSLOGUDP = 1;
    public static final Integer COLLECT_TYPE_JDBC = 2;
    public static final Integer COLLECT_TYPE_FTP = 3;
    public static final Integer COLLECT_TYPE_HTTP = 4;
    public static final Integer COLLECT_TYPE_KAFKA = 5;
}
