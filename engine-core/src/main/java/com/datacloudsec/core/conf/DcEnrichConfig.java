package com.datacloudsec.core.conf;

import com.datacloudsec.config.conf.parser.asset.Asset;
import com.datacloudsec.config.conf.parser.asset.AssetCategory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DcEnrichConfig
 * 用于存放丰富化相关的配置，包括：
 * 1. 资产列表
 * 2. 情报相关信息
 * 3. IP地址库等
 *
 * @author gumizy 2017/7/28
 */
public class DcEnrichConfig implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(DcEnrichConfig.class);

    private static List<Asset> allAssets = Collections.emptyList();
    private static Map<String, Asset> assetMap = Collections.emptyMap();

    private static List<AssetCategory> allAssetCategories = Collections.emptyList();
    private static Map<Integer, AssetCategory> assetCategoryMap = Collections.emptyMap();


    public synchronized static void initAssets(List<Asset> assets) {
        if(assets == null){
            logger.error("Asset is null, please check!");
            return;
        }

        DcEnrichConfig.allAssets = assets;
        DcEnrichConfig.updateInnerMapping();
    }

    public synchronized static void initAssetCategories(List<AssetCategory> assetCategories) {
        if (assetCategories == null) {
            logger.error("AssetCategory is null, please check!");
            return;
        }

        DcEnrichConfig.allAssetCategories = assetCategories;
        DcEnrichConfig.updateInnerMapping();
    }

    private synchronized static void updateInnerMapping() {
        if(CollectionUtils.isEmpty(DcEnrichConfig.allAssets)) {
            logger.info("Asset is empty, will auto clear inner mapping!");
            assetMap = Collections.emptyMap();
            assetCategoryMap = Collections.emptyMap();
        }
        else {
            assetMap = allAssets.stream()
                    .collect(java.util.stream.Collectors.toMap(Asset::getIp, e -> e));

            if(CollectionUtils.isEmpty(DcEnrichConfig.allAssetCategories)) {
                logger.info("AssetCategory is empty, will auto clear inner mapping!");
                assetMap = Collections.emptyMap();
                assetCategoryMap = Collections.emptyMap();
            }
            else {
                assetCategoryMap = allAssetCategories.stream()
                        .collect(java.util.stream.Collectors.toMap(AssetCategory::getId, e -> e));
            }
        }

    }

    public static Asset getAssetByIp(String ip) {
        return assetMap.get(ip);
    }

    public static AssetCategory getAssetCategoryById(Integer id) {
        return assetCategoryMap.get(id);
    }
}
