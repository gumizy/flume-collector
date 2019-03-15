package com.datacloudsec.config.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * FileKit
 *
 * @author gumizy 2017/4/14
 */
public class FileKit {

    private static final Logger logger = LoggerFactory.getLogger(FileKit.class);

    /**
     * 确保目录存在，若不存在则自动创建
     *
     * @param path path
     * @return boolean
     */
    public static boolean ensureDirExist(String path) {
        if (isDirExist(path)) {
            return true;
        } else {
            File file = new File(path);
            return file.mkdirs();
        }
    }

    /**
     * 确保文件存在，若不存在则自动创建
     *
     * @param path path
     * @return boolean
     */
    public static boolean ensureFileExist(String path) {
        if (isFileExist(path)) {
            return true;
        } else {
            try {
                File file = new File(path);
                FileUtils.forceMkdir(file.getParentFile());
                return file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
    }

    /**
     * 是否为文件
     *
     * @param file File
     * @return boolean
     */
    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 是否为目录
     *
     * @param file File
     * @return boolean
     */
    public static boolean isDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 检查文件是否存在
     *
     * @param path path
     * @return boolean
     */
    public static boolean isFileExist(String path) {
        if (StringUtils.isBlank(path)) return false;
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * 检查目录是否存在
     *
     * @param path path
     * @return boolean
     */
    public static boolean isDirExist(String path) {
        if (StringUtils.isBlank(path)) return false;
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    /**
     * 合并路径
     *
     * @param paths paths
     * @return String
     */
    public static String join(String... paths) {
        if (paths == null) return "";
        return StringUtils.join(paths, File.separator);
    }

    /**
     * 复制文件
     *
     * @param srcPath srcPath
     * @param dstPath dstPath
     */
    public static void copy(String srcPath, String dstPath) {
        try {
            FileUtils.copyFile(new File(srcPath), new File(dstPath));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath filePath
     */
    public static void deleteFile(String filePath) {
        if (FileKit.isFileExist(filePath)) {
            FileUtils.deleteQuietly(new File(filePath));
        }
    }

    /**
     * 删除目录
     *
     * @param dirPath dirPath
     */
    public static void deleteDir(String dirPath) {
        if (FileKit.isDirExist(dirPath)) {
            FileUtils.deleteQuietly(new File(dirPath));
        }
    }

    /**
     * 获取绝对路径
     *
     * @param path path
     * @return String
     */
    public static String absolutePath(String path) {
        try {
            return new File(path).getAbsolutePath();
        } catch (Exception e) {
            return path;
        }
    }

    /**
     * 获取文件所在文件夹路径
     *
     * @param path path
     * @return String
     */
    public static String getDirPath(String path) {
        try {
            File file = new File(path);
            int count = 100;
            while (file.isFile() && (--count) > 0) {
                file = file.getParentFile();
            }
            return file.getPath();
        } catch (Exception e) {
            return path;
        }
    }

    /**
     * 文件大小
     *
     * @param path path
     * @return long
     */
    public static long fileSize(String path) {
        if (isFileExist(path)) {
            try {
                return FileUtils.sizeOf(new File(path));
            } catch (Exception e) {
                return 0L;
            }
        }
        return 0;
    }

}
