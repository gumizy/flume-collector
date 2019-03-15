package com.datacloudsec.config.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * EnvConfigKit
 */
public class EnvConfigKit {

    public static final String DEV = "-dev"; // 开发环境，一般为windows环境
    public static final String PROD = "-prod";// 生产环境，一般为linux环境

    private static final Map<String, String> EMPTY_CONFIG = new HashMap<>();

    /**
     * 按操作系统环境读取配置文件
     *
     * @param filePath   filePath
     * @param autoRecEnv autoRecEnv
     * @return Map
     */
    public static Map<String, String> getConfigs(String filePath, boolean autoRecEnv) {
        if (autoRecEnv) {
            return getConfigs(filePath, EnvKit.isWin() ? DEV : PROD);
        } else {
            return getConfigs(filePath);
        }
    }

    /**
     * 读取配置文件
     *
     * @param filePath filePath
     * @return Map
     */
    public static Map<String, String> getConfigs(String filePath) {
        Prop prop = PropKit.use(filePath);
        return prop != null ? prop.toMap() : EMPTY_CONFIG;
    }

    /**
     * 按操作系统环境读取配置文件
     *
     * @param filePath filePath
     * @param profile  profile
     * @return Map
     */
    public static Map<String, String> getConfigs(String filePath, String profile) {
        filePath = getFilePath(filePath, profile);
        Prop prop = PropKit.use(filePath);
        return prop != null ? prop.toMap() : EMPTY_CONFIG;
    }

    /**
     * 获取环境下的具体配置
     *
     * @param filePath filePath
     * @return String
     */
    public static String getFilePath(String filePath, String profile) {
        filePath = filePath != null ? filePath.trim() : "";
        profile = profile != null ? profile.trim() : "";
        int pos = filePath.lastIndexOf('.');
        if (pos > 0) {
            return filePath.substring(0, pos) + profile + filePath.substring(pos);
        } else {
            return filePath + profile;
        }
    }
}
