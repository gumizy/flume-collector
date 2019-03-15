package com.datacloudsec.config.conf;

public final class BasicConfigurationConstants {

    public static final String AGENTN_NAME = "DCSEC_COLLOECTOR_AGENT";

    public static final String EVENT_INDEX_TYPE = "event";
    public static final String FLOW_INDEX_TYPE = "flow";
    public static final String NO_PARSER_TYPE = "/other";
    public static final String EVENT_TOPIC = "event";
    public static final String EVENT_KEY = "dcsec";
    public static final String CONF_MAPPING_PATH = "conf/mapping/analysis-event-mapping.json";
    // counter key
    public static final String COUNTER_SYSLOG_KEY = "Collector@SyslogUDP@";
    public static final String COUNTER_FLOW_KEY = "Collector@Flow@";
    public static final String COUNTER_NTAFLOW_KEY = "Collector@NtaFlow@";
    public static final String COUNTER_HTTP_KEY = "Collector@Http@";
    public static final String COUNTER_FTP_KEY = "Collector@Ftp@";
    public static final String COUNTER_JDBC_KEY = "Collector@Jdbc@";
    public static final String COUNTER_KAFKA_KEY = "Collector@Kafka@";
    public static final String COUNTER_REDIS_KEY = "Collector@Redis@";

    //基础存储目录
    public static final String BASE_STORE_PATH = "./collector_resource";

    // 配置文件
    public static final String ENGINE_CONFIG_NAME = "application.properties";
    public static final String ENGINE_CONFIG_STORE_PATH = "./collector_resource/application.properties";

    // Sqlite
    public static final String ENGINE_DB_NAME = "collector_engine.db";
    public static final String ENGINE_DB_STORE_PATH = "./collector_resource/collector_engine.db";

    // Geo
    public static final String GEO_DB_NAME = "conf/ip2region.db";
    public static final String GEO_DB_STORE_PATH = "./collector_resource/ip2region.db";

    // doc
    public static final String DOC_EXAMPLE_PATH = "doc/example.properties";
    public static final String DOC_EXAMPLE_STORE_PATH = "./collector_resource/" + DOC_EXAMPLE_PATH;

    public static final String DOC_EXAMPLE_MULTIPLE_PATH = "doc/example-multiple.properties";
    public static final String DOC_EXAMPLE_MULTIPLE_STORE_PATH = "./collector_resource/" + DOC_EXAMPLE_MULTIPLE_PATH;

    public static final String DOC_README_PATH = "doc/README.md";
    public static final String DOC_README_STORE_PATH = "./collector_resource/" + DOC_README_PATH;

    public static final String CONFIG_SOURCE = "source";
    public static final String CONFIG_SOURCE_PREFIX = CONFIG_SOURCE + ".";
    public static final String CONFIG_SOURCE_CHANNELSELECTOR_PREFIX = "selector.";

    public static final String CONFIG_SINK = "sink";
    public static final String CONFIG_SINK_PREFIX = CONFIG_SINK + ".";
    public static final String CONFIG_SINK_PROCESSOR_PREFIX = "processor.";

    public static final String CONFIG_CHANNEL = "channel";
    public static final String CONFIG_CHANNEL_PREFIX = CONFIG_CHANNEL + ".";
    public static final String DEFAULT_CHANNEL_NAME = "channel.memory";

    public static final String CONFIG_CONFIG = "config";
    public static final String CONFIG_TYPE = "type";

    public static final String CONFIG_HOST = "host";
    public static final String CONFIG_HOSTS = "hosts";
    public static final String CONFIG_HOSTS_PREFIX = "hosts.";

    public static final String SOURCE_TYPE = "sourceType";

    public static final String GEO_ENRICH_CONFIG_FIELD = "geoEnrichConfigField";
    public static final String GEO_ENRICH_FIELDS = "geoEnrichFields";

    public static final String ASSET_ENRICH_CONFIG_FIELD = "assetEnrichConfigField";
    public static final String ASSET_ENRICH_FIELDS = "assetEnrichFields";

    public static final String VIRUS_REFLECT_ENRICH_FIELDS = "virusReflectFields";
    public static final String VIRUS_ENRICH_FIELDS = "virusEnrichFields";

    public static final String VULNERABILITY_REFLECT_ENRICH_FIELDS = "vulnerabilityReflectFields";
    public static final String VULNERABILITY_ENRICH_FIELDS = "vulnerabilityEnrichFields";

    public static final String TROJANS_REFLECT_ENRICH_FIELDS = "trojansReflectFields";
    public static final String TROJANS_ENRICH_FIELDS = "trojansEnrichFields";

    public static final String MALWAREIP_REFLECT_ENRICH_FIELDS = "MalwareIpReflectFields";
    public static final String MALWAREIP_ENRICH_FIELDS = "MalwareIpEnrichFields";

    public static final String MALWAREURL_REFLECT_ENRICH_FIELDS = "MalwareURLReflectFields";
    public static final String MALWAREURL_ENRICH_FIELDS = "MalwareURLEnrichFields";

    public static final String MALWAREDOMAIN_REFLECT_ENRICH_FIELDS = "MalwareDomainReflectFields";
    public static final String MALWAREDOMAIN_ENRICH_FIELDS = "MalwareDomainEnrichFields";

    public static final String MALWARECODE_REFLECT_ENRICH_FIELDS = "MalwareCodeReflectFields";
    public static final String MALWARECODE_ENRICH_FIELDS = "MalwareCodeEnrichFields";
    //add end

    public static final String DATEFOMAT_TIMEZONE_SEPERATOR = "-tz:";

    public static final String CUS_DEPART_ENRICH_CONFIG_FIELD = "cusEnrichConfigField";
    public static final String CUS_DEPART_ENRICH_FIELDS = "cusEnrichFields";

    private BasicConfigurationConstants() {
        // disable explicit object creation
    }

}
