package com.datacloudsec.parser.process;

import com.datacloudsec.config.ContextKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthProcess
 */
public class AuthProcess implements ParserMetadataProcess {

    private static final Logger logger = LoggerFactory.getLogger(AuthProcess.class);

    /**
     * 授权修改
     */
    @Override
    public void process(String flag) {
        try {
            boolean auth = "true".equalsIgnoreCase(flag) || "1".equalsIgnoreCase(flag);
            if (ContextKit.isAuthOK() != auth) {
                logger.warn("Current auth flag is {}, will change to {}!", ContextKit.isAuthOK(), flag);
                ContextKit.setAuthFlag(Boolean.valueOf(flag));
            }
        } catch (Exception e) {
            logger.error("Acquisition Engine Authorization Code incorrect");
            System.exit(0);
        }
    }
}
