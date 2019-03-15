package com.datacloudsec.source.ftp;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.BASE_STORE_PATH;

/**
 * @Date 2019/1/23 16:23
 */
public class FtpFileSourceConstants {
    public static final String CLIENTTYPE = "clientType";// ftp sftp ftps
    public static final String BATCH_SIZE = "size";
    public static final String SERVER = "server";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final Integer DISCOVER_DELAY = 10000;
    public static final boolean FLUSHLINE_DEFAULT = true;
    //    private static final String FOLDER_DEFAULT = System.getProperty("java.io.tmpdir");
    public static final String FOLDER_DEFAULT = BASE_STORE_PATH; // 存储读取记录文件目录
    public static final Integer CHUNKSIZE_DEFAULT = 1024;// 按大小读取 （不适合本项目应用逻辑，本项目是按照行读取）
    public static final String FILENAME_DEFAULT = "default_file_track_status.ser";// 读取记录文件跟中转台序列化文件
    public static final boolean RECURSIVE_DEFAULT = true;// 是否遍历子目录文件夹
    public static final String FILE_COMPRESSION_FORMAT = null;// 读取gzip类型的文件，要么是null，要么是gzip

}
