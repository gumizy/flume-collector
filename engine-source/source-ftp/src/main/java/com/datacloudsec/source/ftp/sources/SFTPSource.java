/*
 * KEEDIO
 */
package com.datacloudsec.source.ftp.sources;

import com.datacloudsec.source.ftp.AbstractFtpFileSource;
import com.datacloudsec.source.ftp.fliters.FtpFileFilter;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Luis Lázaro lalazaro@keedio.com Keedio
 */
public class SFTPSource extends AbstractFtpFileSource<ChannelSftp.LsEntry> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SFTPSource.class);

    private String knownHosts;
    private JSch jsch;
    private Session sessionSftp;
    private Channel channel;
    private ChannelSftp sftpClient;
    private String strictHostKeyChecking;

    public SFTPSource() {
    }

    /**
     * @param knownHosts
     */
    public SFTPSource(String knownHosts, String strictHostKeyChecking) {
        this.knownHosts = knownHosts;
        this.strictHostKeyChecking = strictHostKeyChecking;
        jsch = new JSch();
    }

    /**
     * @return boolean Opens a Socket connected to a com.datacloudsec.bootstrap.server and login to return
     * True if successfully completed, false if not.
     */
    @Override
    public boolean connect() {
        setConnected(true);
        try {
//            jsch.setKnownHosts(knownHosts);
//            sessionSftp = jsch.getSession(user, com.datacloudsec.bootstrap.server, port);
//            sessionSftp.setConfig("StrictHostKeyChecking", strictHostKeyChecking);
//            sessionSftp.setPassword(password);
//            sessionSftp.connect();

            sessionSftp = jsch.getSession(user, server, port);
            sessionSftp.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sessionSftp.setConfig(sshConfig);
            sessionSftp.connect();
            if (sessionSftp.isConnected()) {
                channel = sessionSftp.openChannel("sftp");
                channel.connect();
                if (channel.isConnected()) {
                    sftpClient = (ChannelSftp) channel;
                }
            }

            String workingDirectory = getWorkingDirectory();
            if (workingDirectory != null) {
                sftpClient.cd(workingDirectory);
            }

        } catch (JSchException e) {
            if (!(sessionSftp.isConnected())) {
                LOGGER.info("JSchException ", e);
                this.setConnected(false);
            }
        } catch (SftpException e) {
            this.setConnected(false);
            LOGGER.error("", e);
        }
        return isConnected();
    }

    /**
     * @return the knownHosts
     */
    public String getKnownHosts() {
        return knownHosts;
    }

    /**
     * @param knownHosts the knownHosts to set
     */
    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }

    /**
     * @return the jsch
     */
    public JSch getJsch() {
        return jsch;
    }

    /**
     * @param jsch the jsch to set
     */
    public void setJsch(JSch jsch) {
        this.jsch = jsch;
    }

    /**
     * @return the sessionSftp
     */
    public Session getSessionSftp() {
        return sessionSftp;
    }

    /**
     * @param sessionSftp the sessionSftp to set
     */
    public void setSessionSftp(Session sessionSftp) {
        this.sessionSftp = sessionSftp;
    }

    /**
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * @return the sftpClient
     */
    public ChannelSftp getSftpClient() {
        return sftpClient;
    }

    /**
     * @param sftpClient the sftpClient to set
     */
    public void setSftpClient(ChannelSftp sftpClient) {
        this.sftpClient = sftpClient;
    }

    /**
     * Disconnect and logout from current connection to com.datacloudsec.bootstrap.server
     */
    @Override
    public void disconnect() {
        channel.disconnect();
        sessionSftp.disconnect();
        setConnected(false);
        if (isConnected()) {
            LOGGER.error("Source " + this.getClass().getName() + " failed disconnect");
        }
    }

    @Override
    /**
     * @return void
     * @param String destination
     */ public void changeToDirectory(String directory) throws IOException {
        try {
            sftpClient.cd(directory);
        } catch (SftpException e) {
            LOGGER.error("Could not change to directory " + directory, e);
            throw new IOException(e.getMessage());
        }
    }

    @Override
    /**
     * @return list with objects in directory
     * @param current directory
     */ public List<ChannelSftp.LsEntry> listElements(String directory) throws IOException {
        List<ChannelSftp.LsEntry> list;
        try {
            list = sftpClient.ls(directory);
        } catch (SftpException e) {
            LOGGER.error("Could not list files from  " + directory, e);
            throw new IOException(e.getMessage());
        }
        return list;
    }

    @Override
    /**
     * @param Object
     * @return InputStream
     */ public InputStream getInputStream(ChannelSftp.LsEntry file) throws IOException {
        InputStream inputStream;
        try {
            inputStream = sftpClient.get(file.getFilename());
        } catch (SftpException e) {
            LOGGER.error("Error trying to retrieve inputstream", e);
            throw new IOException(e.getMessage());
        }
        return inputStream;
    }

    @Override
    /**
     * @return name of the file
     * @param object as file
     */ public String getObjectName(ChannelSftp.LsEntry file) {
        return file.getFilename();
    }

    @Override
    /**
     * @return boolean
     * @param Object to check
     */ public boolean isDirectory(ChannelSftp.LsEntry file) {
        return file.getAttrs().isDir();
    }

    @Override
    /**
     * @return boolean
     * @param Object to check
     */ public long getModifiedTime(ChannelSftp.LsEntry file) {
        return file.getAttrs().getMTime() * 1000L;
    }

    @Override
    /**
     * There is no attribute to check isfile in SftpATTRS
     *
     * @return boolean
     * @param file to check
     */ public boolean isFile(ChannelSftp.LsEntry file) {
        boolean isfile;
        if ((!isDirectory(file)) && (!isLink(file))) {
            isfile = true;
        } else {
            isfile = false;
        }
        return isfile;
    }

    @Override
    /**
     * This method does not do anything. It just returns true
     * where the api FTPClient needs a completePendingCommand 
     * bloking method.
     * @return boolean
     */ public boolean particularCommand() {
        return true;
    }

    @Override
    /**
     * @return long size
     * @param object file
     */ public long getObjectSize(ChannelSftp.LsEntry file) {
        long filesize = 0L;
        try {
            filesize = sftpClient.lstat(file.getFilename()).getSize();
        } catch (SftpException e) {
            LOGGER.error("Could not lstat to get size of the file", e);
        }
        return filesize;
    }

    @Override
    /**
     * @return boolean is a link
     * @param object as file
     */ public boolean isLink(ChannelSftp.LsEntry file) {
        return file.getAttrs().isLink();
    }

    @Override
    /**
     * @return String name of the link
     * @param object as file
     */ public String getLink(ChannelSftp.LsEntry file) {
        String link = "";
        try {
            link = sftpClient.readlink(file.getFilename());
        } catch (SftpException e) {
            LOGGER.error("Could not readLink to get name", e);
        }
        return link;
    }

    @Override
    /**
     *
     * @return String directory retrieved for com.datacloudsec.bootstrap.server on connect
     */ public String getDirectoryserver() throws IOException {
        String printWorkingDirectory;
        try {
            printWorkingDirectory = sftpClient.getHome();
        } catch (SftpException e) {
            LOGGER.error("Error getting printworkingdirectory for com.datacloudsec.bootstrap.server -sftpsource", e);
            throw new IOException(e.getMessage());
        }

        if (getWorkingDirectory() != null) {
            return printWorkingDirectory + "/" + getWorkingDirectory();
        }

        return printWorkingDirectory;
    }

    /**
     * @return object as cliente of ftpsource
     */
    @Override
    public Object getClientSource() {
        return sftpClient;
    }

    @Override
    public void setFileType(int fileType) throws IOException {
        //do nothing        
    }

    @Override
    public List<ChannelSftp.LsEntry> listElements(String dirToList, FtpFileFilter filter) throws IOException {
        List<ChannelSftp.LsEntry> list;
        List<ChannelSftp.LsEntry> listFiltered = new ArrayList<>();
        try {
            list = sftpClient.ls(dirToList);
            for (ChannelSftp.LsEntry element : list) {
                if (filter.accept(element)) {
                    listFiltered.add(element);
                }
            }

        } catch (SftpException e) {
            LOGGER.error("Could not list files from  " + dirToList, e);
            throw new IOException(e.getMessage());
        }
        return listFiltered;
    }

}
