package com.datacloudsec.parser.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.config.conf.parser.asset.Asset;
import com.datacloudsec.config.conf.parser.asset.AssetCategory;
import com.datacloudsec.core.conf.DcEnrichConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author gumizy
 * @Date 2018/7/12 20:26
 */
public class AssetProcess implements ParserMetadataProcess {
    private static Logger logger = LoggerFactory.getLogger(AssetProcess.class);

    @Override
    public void process(String value) {
        logger.info("AssetProcess doAssetProcess start......");
        try {
            if (StringUtils.isNotBlank(value)) {
                JSONObject jsonObject = JSON.parseObject(value);
                String assetStr = jsonObject.getString(Asset.class.getSimpleName());
                String assetCategoryStr = jsonObject.getString(AssetCategory.class.getSimpleName());
                if (assetStr != null) {
                    List<Asset> assets = JSON.parseArray(assetStr, Asset.class);
                    DcEnrichConfig.initAssets(assets);
                }
                if (assetCategoryStr != null) {
                    List<AssetCategory> assetCategories = JSON.parseArray(assetCategoryStr, AssetCategory.class);
                    DcEnrichConfig.initAssetCategories(assetCategories);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("AssetProcess doAssetProcess end......");
    }
}
