package com.datacloudsec.source.kafka;

public class KafkaSourceConstants {


    /* Properties */

    public static final String TOPIC_CONFIG = "topic";
    public static final String GROUP_ID = "groupId";
    public static final String BOOTSTRAP_SERVERS_CONFIG = "servers";
    public static final String INTERVAL = "interval";
    public static final String TIMEOUT = "timeout";

    public static final String BATCH_DURATION_MS = "batchDurationMillis";
    public static final int DEFAULT_BATCH_DURATION = 1000;


    /* sa default setting*/
    public static final String SA_DEFAULT_SA_SERVER = "127.0.0.1:9092";
    public static final String SA_DEFAULT_GROUP_ID = "dcsec_kafka_consumer";
    public static final String SA_AUTO_COMMIT_INTERVAL_MS = "1000";
    public static final String SA_SESSION_TIMEOUT_MS = "30000";
    public static final String DEFAULT_SA_KEY_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String DEFAULT_SA_VALUE_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";

}

