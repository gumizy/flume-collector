package com.datacloudsec.config;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.config.conf.intercepor.AnalysisEventMappingBean;
import com.datacloudsec.config.tools.EnvKit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.CONF_MAPPING_PATH;

/**
 * DcContextKit
 */
public class ContextKit {

    private static final Logger logger = LoggerFactory.getLogger(ContextKit.class);
    private static List<AnalysisEventMappingBean> AnalysisEventMapping = new ArrayList<>();

    /**
     * 启用状态
     */
    private static Boolean enable = Boolean.TRUE;
    // 授权状态
    private static boolean authFlag = true;

    // 授权时间
    private static long lastSyncTime = 0L;

    public static boolean isDevMode() {
        return EnvKit.isWin();
    }

    public static boolean isEnabled() {
        return enable != null && enable;
    }

    public static boolean isAuthOK() {
        return authFlag;
    }

    public static void setEnable(Boolean enable) {
        ContextKit.enable = enable;
    }

    public static void setAuthFlag(boolean authFlag) {
        ContextKit.authFlag = authFlag;
    }

    public static long getLastSyncTime() {
        return lastSyncTime;
    }

    public static void setLastSyncTime(long lastSyncTime) {
        ContextKit.lastSyncTime = lastSyncTime;
    }

    /**
     * 获取Event属性复制列表
     *
     * @return List
     */
    public static List<AnalysisEventMappingBean> getAnalysisEventMapping() {
        if (CollectionUtils.isNotEmpty(AnalysisEventMapping)) {
            return AnalysisEventMapping;
        } else {
            InputStream in = null;
            try {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONF_MAPPING_PATH);
                String content = new String(IOUtils.readFully(in, in.available()), "UTF-8");
                List<AnalysisEventMappingBean> list = JSON.parseArray(content, AnalysisEventMappingBean.class);
                if (CollectionUtils.isNotEmpty(list)) {
                    AnalysisEventMapping = list;
                }
            } catch (Exception e) {
                logger.error("Load conf {} failed! Error is: {}!", CONF_MAPPING_PATH, e.getMessage());
            } finally {
                IOUtils.closeQuietly(in);
            }
            return AnalysisEventMapping;
        }
    }
}
