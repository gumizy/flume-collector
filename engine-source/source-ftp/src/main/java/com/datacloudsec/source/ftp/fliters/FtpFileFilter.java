package com.datacloudsec.source.ftp.fliters;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

/**
 * @Author gumizy
 * @Date 2019/1/5 16:06
 */
public class FtpFileFilter implements FTPFileFilter {
    private String strMatch;

    public FtpFileFilter(String strMatch) {
        this.strMatch = strMatch;
    }

    @Override
    public boolean accept(FTPFile ftpFile) {
        if (("").equals(strMatch)) return true;
        if (strMatch == null) return true;
        return (ftpFile.isDirectory() || (ftpFile.isFile() && ftpFile.getName().matches(strMatch)));
    }

    /**
     * @param sftpFile
     * @return
     */
    public boolean accept(ChannelSftp.LsEntry sftpFile) {
        if (("").equals(strMatch)) return true;
        if (strMatch == null) return true;
        return (sftpFile.getAttrs().isDir() || (isFile(sftpFile)) && (sftpFile.getFilename().matches(strMatch)));
    }

    /**
     * There is no attribute to check isfile in SftpATTRS for JSCH.
     * Auxiliar function for checking if a ChannelSftp.LsEntry is a file.
     *
     * @param file
     * @return
     */
    public boolean isFile(ChannelSftp.LsEntry file) {
        boolean isfile;
        if ((!file.getAttrs().isDir()) && (!file.getAttrs().isLink())) {
            isfile = true;
        } else {
            isfile = false;
        }
        return isfile;
    }

}

