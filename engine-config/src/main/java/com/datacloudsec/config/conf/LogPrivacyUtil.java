package com.datacloudsec.config.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPrivacyUtil {
    private static final Logger logger = LoggerFactory.getLogger(LogPrivacyUtil.class);

    public static final String LOG_RAWDATA_PROP = "org.apache.flume.log.rawdata";

    public static final String LOG_PRINTCONFIG_PROP = "org.apache.flume.log.printconfig";

    public static boolean allowLogRawData() {
        return Boolean.getBoolean(LOG_RAWDATA_PROP);
    }

    public static boolean allowLogPrintConfig() {
        return Boolean.getBoolean(LOG_PRINTCONFIG_PROP);
    }
}
