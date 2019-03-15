package com.datacloudsec.config.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EnvKit
 *
 * @author gumizy 2017/9/22
 */
public class EnvKit {
    private static Logger logger = LoggerFactory.getLogger(EnvKit.class);

    public static boolean isWin() {
        String os = System.getProperty("os.name");
        boolean win = os.toLowerCase().startsWith("win");
        return win;
    }
}
