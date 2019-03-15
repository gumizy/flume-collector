package com.datacloudsec.source.ftp;

import com.datacloudsec.config.conf.source.SourceLogMessage;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

/**
 * @Date 2019/1/18 18:59
 */
public class FtpFileSourceUtils {
    public static SourceLogMessage extractSourceMessage(byte[] bytes, String filePath, String fileName, String receiveIp) {
        SourceLogMessage ftpFileSourceMessage = new SourceLogMessage(receiveIp);

        ftpFileSourceMessage.setHeaders(FTPFILEPATH, filePath);
        ftpFileSourceMessage.setHeaders(FTPFILENAME, fileName);
        ftpFileSourceMessage.setHeaders(PARSER_IDENTIFICATION, receiveIp + COLLECT_TYPE_SEPARATOR + COLLECT_TYPE_FTP);

        ftpFileSourceMessage.setBody(bytes);
        return ftpFileSourceMessage;
    }
}
