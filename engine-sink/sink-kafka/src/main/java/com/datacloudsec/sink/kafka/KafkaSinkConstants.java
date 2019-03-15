package com.datacloudsec.sink.kafka;

public class KafkaSinkConstants {


    /* Properties */

    public static final String TOPIC_CONFIG = "topic";
    public static final String BATCH_SIZE = "batchSize";
    public static final String BOOTSTRAP_SERVERS_CONFIG = "servers";
    public static final String ACK = "ack";

    /* sa default setting*/
    public static final String DEFAULT_SA_TOPIC = "event";
    public static final String DEFAULT_SA_SERVER = "127.0.0.1:9092";
    public static final String DEFAULT_SA_ACKS = "1";// 0-无需确认，1-等待leader，all-等待所有
    public static final Integer DEFAULT_SA_TRETRIES = 0;
    public static final Integer DEFAULT_SA_BATCH_SIZE = 16384;
    public static final Integer DEFAULT_SA_LINGER_MS = 1;
    public static final Integer DEFAULT_SA_MAX_BLOCK_MS = 5000;// 连接超时参数
    public static final Integer DEFAULT_SA_BUFFER_MEMORY = 33554432;
    public static final String DEFAULT_SA_KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    public static final String DEFAULT_SA_VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
}

