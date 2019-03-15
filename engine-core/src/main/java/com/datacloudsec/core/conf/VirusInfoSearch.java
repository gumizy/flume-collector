package com.datacloudsec.core.conf;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by liqiang on 2017/8/21.
 */
public class VirusInfoSearch {
    private static final Logger logger = LoggerFactory.getLogger(VirusInfoSearch.class);
    private static List<VirusInfo> allVirusInfo = Collections.emptyList();
    private static Map<String, VirusInfo> virusInfoMap = Collections.emptyMap();

    public static void initVirusInfo(List<VirusInfo> allVirusInfo) {
        if (allVirusInfo == null) {
            logger.error("virus info list is null, please check!");
            allVirusInfo = Collections.emptyList();
            virusInfoMap = Collections.emptyMap();
            return;
        }

        VirusInfoSearch.allVirusInfo = allVirusInfo;
        VirusInfoSearch.updateInnerMapping();
    }

    private synchronized static void updateInnerMapping() {
        if(CollectionUtils.isEmpty(VirusInfoSearch.allVirusInfo)) {
            logger.info("virus info list is empty, will auto clear inner mapping!");
            virusInfoMap = Collections.emptyMap();
        } else {
            virusInfoMap = allVirusInfo.stream()
                    .collect(java.util.stream.Collectors.toMap(VirusInfo::getVirusHash, e -> e));
        }
    }

    public static VirusInfo getVirusInfoByHash(String virusHash) {
        return  virusInfoMap.get(virusHash);
    }
}
