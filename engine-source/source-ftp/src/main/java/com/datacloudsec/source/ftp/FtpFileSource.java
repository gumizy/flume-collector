package com.datacloudsec.source.ftp;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.core.ChannelException;
import com.datacloudsec.core.PollableSource;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.parser.PollableParserRunner;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.parser.EventParserProcessor;
import com.datacloudsec.source.ftp.factory.FtpSourceFactory;
import com.datacloudsec.source.ftp.fliters.FtpFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @Author gumizy
 * @Date 2019/1/5 15:09
 */
public class FtpFileSource extends AbstractSource implements Configurable, PollableSource {

    private FtpSourceFactory sourceFactory = new FtpSourceFactory();
    private AbstractFtpFileSource ftpSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpFileSource.class);
    private static final short ATTEMPTS_MAX = 3; //  max limit attempts reconnection
    private static final long EXTRA_DELAY = 10000;
    private int counterConnect = 0;
    private SourceCounter sourceCounter;
    private String workingDirectory;
    private FtpFileFilter ftpFileFilter;

    public FtpFileSource() {
        super();
    }

    /**
     * Request ftpSource to the factory
     *
     * @param context
     * @return ftpSource
     */
    private AbstractFtpFileSource orderFtpSource(Context context) {
        ftpSource = sourceFactory.createFtpSource(context);
        return ftpSource;
    }

    /**
     * @param context
     */
    @Override
    public void configure(Context context) {
        ftpSource = orderFtpSource(context);
        if (ftpSource.existFolder()) {
            ftpSource.makeLocationFile();
        } else {
            LOGGER.error("Folder " + ftpSource.getPathTohasmap().toString() + " does not exist");
        }
        ftpSource.connect();
        sourceCounter = new SourceCounter(getName());
        workingDirectory = ftpSource.getWorkingDirectory();
        ftpFileFilter = new FtpFileFilter(ftpSource.getFtpFilterRegex());
        ftpSource.checkPreviousMap();
        parserProcessor = new EventParserProcessor(this);
        parserProcessor.configure(context);
    }

    @Override
    public PollableSource.Status process() {
        try {
            if (workingDirectory == null) {
                LOGGER.info("property workdir is null, setting to default");
                workingDirectory = ftpSource.getDirectoryserver();
            }
            LOGGER.info("Actual dir:  " + workingDirectory + " files: " + ftpSource.getFileList().size());

            discoverElements(ftpSource, workingDirectory, "", 0, ftpSource.isRecursive());

            ftpSource.cleanList(); //clean list according existing actual files
            ftpSource.getExistFileList().clear();
        } catch (IOException e) {
            LOGGER.error("Exception thrown in process, try to reconnect " + counterConnect, e);

            if (!ftpSource.connect()) {
                counterConnect++;
            } else {
                ftpSource.checkPreviousMap();
            }

//            if (counterConnect < ATTEMPTS_MAX) {
//                process();
//            } else {
            LOGGER.error("Server connection closed without indication, reached limit reconnections " + counterConnect);
            try {
                Thread.sleep(ftpSource.getRunDiscoverDelay() + EXTRA_DELAY);
                counterConnect = 0;
            } catch (InterruptedException ce) {
                LOGGER.error("InterruptedException", ce);
            }
//            }
        }
        ftpSource.saveMap();

        try {
            Thread.sleep(ftpSource.getRunDiscoverDelay());
            return PollableSource.Status.READY;     //source was successfully able to generate events
        } catch (InterruptedException inte) {
            LOGGER.info("Exception thrown in process while putting to sleep", inte);
            return PollableSource.Status.BACKOFF;   //inform the runner thread to back off for a bit
        }
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 1000;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 10000;
    }

    /**
     * @return void
     */
    @Override
    public synchronized void start() {
        LOGGER.info("Starting Ftp source ...", this.getName());
        LOGGER.info("Source {} starting. Metrics: {}", getName(), sourceCounter);
        super.start();
        parserRunner = new PollableParserRunner();
        parserRunner.setPolicy(parserProcessor);
        parserRunner.start();

        sourceCounter.start();
    }

    /**
     * @return void
     */
    @Override
    public synchronized void stop() {
        ftpSource.saveMap();
        if (ftpSource.isConnected()) {
            ftpSource.disconnect();
        }
        sourceCounter.stop();
        super.stop();
        parserRunner.stop();
    }

    /**
     * discoverElements: find files to process them
     *
     * @param <T>
     * @param parentDir,  will be the directory retrieved by the com.datacloudsec.bootstrap.server when
     *                    connected
     * @param currentDir, actual dir in the recursive method
     * @param level,      deep to search
     * @param ftpSource
     * @param recursive   Whether to search sub-directories recursively
     * @throws IOException
     */
    // @SuppressWarnings("UnnecessaryContinue")
    private <T> void discoverElements(AbstractFtpFileSource ftpSource, String parentDir, String currentDir, int level, boolean recursive) throws IOException {

        long position;

        String dirToList = parentDir;
        if (!("").equals(currentDir)) {
            dirToList += "/" + currentDir;
        }
        List<T> list = ftpSource.listElements(dirToList, ftpFileFilter);
        if (!(list.isEmpty())) {

            for (T element : list) {
                String elementName = ftpSource.getObjectName(element);
                if (elementName.equals(".") || elementName.equals("..")) {
                    continue;
                }

                if (ftpSource.isDirectory(element)) {
                    if (recursive) {
                        LOGGER.info("Traversing element recursively: " + "[" + elementName + "]");
                        ftpSource.changeToDirectory(parentDir);
                        discoverElements(ftpSource, dirToList, elementName, level + 1, recursive);
                    }
                } else if (ftpSource.isFile(element)) { //element is a regular file
                    ftpSource.changeToDirectory(dirToList);
                    String dstFileNamePath = dirToList + "/" + elementName;

                    ftpSource.getExistFileList().add(dstFileNamePath);  //control of deleted files in com.datacloudsec.bootstrap.server

                    //test if file is new in collection
                    long nowFileSize = ftpSource.getObjectSize(element);

                    if (!(ftpSource.getFileList().containsKey(dstFileNamePath))) { //new file
                        position = 0L;
                        LOGGER.info("Discovered: " + elementName + " ,size: " + nowFileSize);
                    } else { //known file
                        long prevSize = (long) ftpSource.getFileList().get(dstFileNamePath);
                        position = prevSize;
                        long dif = nowFileSize - prevSize;

                        if (dif > 0) {
                            LOGGER.info("Modified: " + elementName + " ,size: " + dif);
                        } else if (dif < 0) { //known and full modified
                            ftpSource.getExistFileList().remove(dstFileNamePath); //will be rediscovered as new file
                            ftpSource.saveMap();
                            continue;
                        } else {
                            continue;
                        }

                    } //end if known file

                    //common for all regular files
                    InputStream inputStream = null;
                    try {
                        inputStream = ftpSource.getInputStream(element);

                        if (!readStream(inputStream, position, elementName, dirToList, ftpSource.getServer())) {
                            inputStream = null;
                        }

                        boolean success = inputStream != null && ftpSource.particularCommand(); //mandatory if FTPClient
                        if (success) {
                            ftpSource.getFileList().put(dstFileNamePath, nowFileSize);
                            ftpSource.saveMap();

                            LOGGER.info("Processed:  " + elementName + ", total files: " + this.ftpSource.getFileList().size() + "\n");

                        } else {
                            handleProcessError(elementName);
                        }
                    } catch (IOException e) {
                        handleProcessError(elementName);
                        LOGGER.error("Failed retrieving inputStream on discoverElements ", e);
                        continue;
                    }

                    ftpSource.changeToDirectory(dirToList);

                } else if (ftpSource.isLink(element)) {
                    LOGGER.info(elementName + " is a link of " + this.ftpSource.getLink(element) + " could not retrieve size");
                    ftpSource.changeToDirectory(parentDir);
                    continue;
                } else {
                    LOGGER.info(elementName + " unknown type of file");
                    ftpSource.changeToDirectory(parentDir);
                    continue;
                }
                ftpSource.changeToDirectory(parentDir);

            }
        }
    }

    /**
     * Determine whether the attribute 'lastModifiedTime' exceeded argument threshold(timeout).
     * If 'timeout' seconds have passed since the last modification of the file, file can be discovered
     * and processed.
     *
     * @param lastModifiedTime
     * @param timeout,         configurable by user via property processInUseTimeout (seconds)
     * @return
     */
    public boolean lastModifiedTimeExceededTimeout(long lastModifiedTime, int timeout) {

        Date dateModified = new Date(lastModifiedTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, -timeout);
        Date timeoutAgo = cal.getTime();

        return (dateModified.compareTo(timeoutAgo) > 0);
    }

    /**
     * Read retrieved stream from ftpclient into byte[] and process. If
     * flushlines is true the retrieved inputstream will be readed by lines. And
     * the type of file is set to ASCII from ftpSource.
     *
     * @param inputStream
     * @param position
     * @return boolean
     */
    private boolean readStream(InputStream inputStream, long position, String fileName, String filePath, String serverIp) {
        if (inputStream == null) {
            return false;
        }
        boolean successRead = true;
        if (ftpSource.isFlushLines()) {
            try {
                inputStream.skip(position);
                if (StringUtils.isNotBlank(ftpSource.getCompressionFormat())) {
                    switch (ftpSource.getCompressionFormat()) {
                        case "gzip":
                            LOGGER.info("File " + fileName + " is GZIP compressed, and decompression has been requested by user. " + "Will attempt to decompress.");
                            try (BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream), Charset.defaultCharset()))) {
                                String line;

                                while ((line = in.readLine()) != null) {
                                    processMessage(line.getBytes(), fileName, filePath, serverIp);
                                }
                            }
                            break;
                        default:
                            throw new IOException("Unsupported compression format specified: " + ftpSource.getCompressionFormat());
                    }
                } else {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
                        String line;

                        while ((line = in.readLine()) != null) {
                            processMessage(line.getBytes(), fileName, filePath, serverIp);
                        }

                    }
                }
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                successRead = false;
            }
        } else {
            try {
                inputStream.skip(position);
                int chunkSize = ftpSource.getChunkSize();
                byte[] bytesArray = new byte[chunkSize];
                int bytesRead;
                while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                    try (ByteArrayOutputStream baostream = new ByteArrayOutputStream(chunkSize)) {
                        baostream.write(bytesArray, 0, bytesRead);
                        byte[] data = baostream.toByteArray();
                        processMessage(data, fileName, filePath, serverIp);
                    }
                }

                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("on readStream", e);
                successRead = false;

            }
        }
        return successRead;
    }

    private void processMessage(byte[] lastInfo, String fileName, String filePath, String serverIp) {
        byte[] message = lastInfo;

        try {
            SourceMessage msg = FtpFileSourceUtils.extractSourceMessage(message, filePath, fileName, serverIp);
            parserProcessor.put(msg);
        } catch (ChannelException e) {
            LOGGER.error("ChannelException", e);
            sourceCounter.incrementEventReadFail();
        }
        sourceCounter.incrementEventReceivedCount();
    }

    private void handleProcessError(String fileName) {
        LOGGER.info("failed retrieving stream from file, will try in next poll :" + fileName);
        sourceCounter.incrementEventReadFail();
    }

    @Override
    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }
}
