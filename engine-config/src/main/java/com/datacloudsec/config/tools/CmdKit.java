package com.datacloudsec.config.tools;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * CmdKit
 */
public class CmdKit {

    public static final Logger logger = LoggerFactory.getLogger(CmdKit.class);

    /**
     * Linux执行cmd命令
     *
     * @param cmd cmd
     * @return String
     */
    public static String execute(final String cmd) {
        InputStreamReader ir = null;
        LineNumberReader input = null;
        StringBuilder buffer = new StringBuilder();
        try {
            String[] cmdBatch = new String[]{"/bin/sh", "-c", cmd};
            Process pcs = Runtime.getRuntime().exec(cmdBatch);
            ir = new InputStreamReader(pcs.getInputStream());
            input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }

            int extValue = pcs.waitFor();
            if (extValue != 0) {
                logger.error("processExec failure!");
            }
        } catch (Exception e) {
            logger.error("CmdKit execute error: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(ir);
        }
        return buffer.toString();
    }
}
