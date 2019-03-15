package com.datacloudsec.source.ftp;

import com.datacloudsec.config.Context;
import com.datacloudsec.core.ChannelSelector;
import com.datacloudsec.core.SourceRunner;
import com.datacloudsec.core.channel.AbstractChannel;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.channel.MemoryChannel;
import com.datacloudsec.core.channel.ReplicatingChannelSelector;
import com.datacloudsec.core.conf.Configurables;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Properties;

/**
 * @Author gumizy
 * @Date 2019/1/5 17:24
 */
public class FtpSourceTest {

    @Test
    public void testFtp() throws InterruptedException {
        Context context = new Context();
        context.put("com.datacloudsec.bootstrap.server", "192.168.101.61");
        context.put("port", "22");
        context.put("user", "root");
        context.put("password", "dcsec@2018");
        context.put("clientType", "sftp");
        context.put("recursive", "false");// 遍历文件夹
        context.put("directory", "/root/ftptest/");
//        context.put("compressed", "gzip");

        // source
        FtpFileSource ftpFileSource = new FtpFileSource();
        ftpFileSource.setName("ftpFileSource");
        SourceRunner sourceRunner = SourceRunner.forSource(ftpFileSource);
        // channel
        AbstractChannel syslogChannel = new MemoryChannel();
        syslogChannel.setName("ftpFilChannel");
        syslogChannel.setType("ftpFil");
        Configurables.configure(syslogChannel, context);
        syslogChannel.start();

        ChannelSelector channelSelector = new ReplicatingChannelSelector();
        channelSelector.setChannels(Collections.singletonList(syslogChannel));

        ChannelProcessor channelProcessor = new ChannelProcessor(channelSelector);
        Configurables.configure(channelProcessor, context);
        ftpFileSource.setChannelProcessor(channelProcessor);
        channelSelector.configure(context);

        ftpFileSource.configure(context);
        sourceRunner.start();

        Thread.sleep(1000000);
    }

    @Test
    public void connect() throws SftpException {
        String host = "192.168.101.61";
        int port = 22;
        String username = "root";
        String password = "dcsec@2018";
        ChannelSftp sftp;
        try {
            JSch jsch = new JSch();
            Session sshSession = jsch.getSession(username, host, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("Connected to " + host + ".");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println(sftp.getHome());
    }
}
