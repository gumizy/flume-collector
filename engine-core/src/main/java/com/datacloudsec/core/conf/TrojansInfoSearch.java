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
public class TrojansInfoSearch {
    private static final Logger logger = LoggerFactory.getLogger(TrojansInfoSearch.class);
    private static List<TrojansInfo> allTrojansInfo = Collections.emptyList();
    private static Map<String, TrojansInfo> trojansInfoMap = Collections.emptyMap();

    public static void initTrojansInfo(List<TrojansInfo> allTrojansInfo) {
        if (allTrojansInfo == null) {
            logger.error("trojans info list is null, please check!");
            allTrojansInfo = Collections.emptyList();
            trojansInfoMap = Collections.emptyMap();
            return;
        }

        TrojansInfoSearch.allTrojansInfo = allTrojansInfo;
        TrojansInfoSearch.updateInnerMapping();
    }

    private synchronized static void updateInnerMapping() {
        if(CollectionUtils.isEmpty(TrojansInfoSearch.allTrojansInfo)) {
            logger.info("trojans info list is empty, will auto clear inner mapping!");
            trojansInfoMap = Collections.emptyMap();
        } else {
            trojansInfoMap = allTrojansInfo.stream()
                    .collect(java.util.stream.Collectors.toMap(TrojansInfo::getTrojansHash, e -> e));
        }
    }

    public static TrojansInfo getTrojansInfoByHash(String trojansHash) {
        return  trojansInfoMap.get(trojansHash);
    }
}
