package com.datacloudsec.source.ftp.factory;

import com.datacloudsec.config.Context;
import com.datacloudsec.source.ftp.AbstractFtpFileSource;
import com.datacloudsec.source.ftp.sources.FTPSSource;
import com.datacloudsec.source.ftp.sources.FTPSource;
import com.datacloudsec.source.ftp.sources.SFTPSource;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datacloudsec.source.ftp.FtpFileSourceConstants.*;

/**
 * @Author gumizy
 * @Date 2019/1/5 15:14
 */
public class FtpSourceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpSourceFactory.class);
    private AbstractFtpFileSource ftpsource;

    /**
     * Create ftpsource
     *
     * @param context
     * @return ftpsource
     */
    public AbstractFtpFileSource createFtpSource(Context context) {
        ftpsource = null;
        initSource(context);
        return ftpsource;
    }

    /**
     * Initiate attributes of FtpsSource according to context
     *
     * @param context of the source
     * @return ftpsource
     */
    public AbstractFtpFileSource initSource(Context context) {
        switch (context.getString(CLIENTTYPE)) {
            case "ftp":
                ftpsource = new FTPSource();
                initCommonParam(context);
                break;
            case "sftp":
                ftpsource = new SFTPSource();
                SFTPSource sftpSource = new SFTPSource(context.getString("knownHosts"), context.getString("strictHostKeyChecking", "yes"));
                ftpsource = sftpSource;
                initCommonParam(context);
                break;
            case "ftps":
                ftpsource = new FTPSSource();
                FTPSSource ftpsSource = new FTPSSource(context.getBoolean("security.enabled"), context.getString("security.cipher"), context.getBoolean("security.certificate.enabled"), context.getString("path.keystore", FOLDER_DEFAULT), context.getString("store.pass"));
                ftpsource = ftpsSource;
                initCommonParam(context);
                break;
            default:
                LOGGER.error("Source not found in context");
        }
        return ftpsource;
    }

    /**
     * initialize common parameters for all sources.
     *
     * @param context of source
     */
    public void initCommonParam(Context context) {
        String server = context.getString(SERVER);
        String username = context.getString(USERNAME);
        String password = context.getString(PASSWORD);
        Preconditions.checkNotNull(server,"Ftp server is null");
        Preconditions.checkNotNull(username,"Ftp username is null");
        Preconditions.checkNotNull(password,"Ftp password is null");
        ftpsource.setBufferSize(context.getInteger(BATCH_SIZE));
        ftpsource.setServer(server);
        ftpsource.setUser(username);
        ftpsource.setPassword(password);
        ftpsource.setRunDiscoverDelay(context.getInteger("delay", DISCOVER_DELAY));
        ftpsource.setWorkingDirectory(context.getString("directory"));
        ftpsource.setPort(context.getInteger("port"));
        ftpsource.setFolder(context.getString("folder", FOLDER_DEFAULT));
        ftpsource.setFileName(context.getString("filename", FILENAME_DEFAULT));
        ftpsource.setFlushLines(context.getBoolean("flushlines", FLUSHLINE_DEFAULT));
        ftpsource.setChunkSize(context.getInteger("chunksize", CHUNKSIZE_DEFAULT));
        ftpsource.setFtpFilterRegex(context.getString("pattern", ""));
        ftpsource.setRecursive(context.getBoolean("recursive", RECURSIVE_DEFAULT));
        ftpsource.setFileCompressed(context.getString("compressed", FILE_COMPRESSION_FORMAT));
    }

}
