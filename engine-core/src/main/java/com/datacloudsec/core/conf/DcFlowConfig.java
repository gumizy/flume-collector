package com.datacloudsec.core.conf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * DcFlowConfig
 */
public class DcFlowConfig implements Serializable {

    private static boolean enable = true;

    private static Map<String, String> allowIps = new HashMap<>();

    public static void setEnable(boolean enable) {
        DcFlowConfig.enable = enable;
    }

    public static void setAllowIps(String[] allowIps) {
        DcFlowConfig.allowIps.clear();
        if (allowIps != null) {
            for (String allowIp : allowIps) {
                DcFlowConfig.allowIps.put(allowIp, allowIp);
            }
        }
    }

    /**
     * 判断该IP的数据是否允许采集（allowIps为空时表示接收所有IP）
     *
     * @param hostIp hostIp
     * @return boolean
     */
    public static boolean isIpAllowed(String hostIp) {
        return enable && (allowIps.isEmpty() || allowIps.containsKey(hostIp));
    }

}
