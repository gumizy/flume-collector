package com.datacloudsec.config.geo;

import com.datacloudsec.config.geo.datablock.DataBlock;
import com.datacloudsec.config.geo.datablock.DbConfig;
import com.datacloudsec.config.geo.datablock.DbSearcher;
import com.datacloudsec.config.tools.FileKit;
import com.datacloudsec.config.tools.ResourceKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.GEO_DB_NAME;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.GEO_DB_STORE_PATH;

/**
 * GeoSearcher
 */
public class GeoSearcher {

    private static final Logger logger = LoggerFactory.getLogger(GeoSearcher.class);

    private static final String ZH = "中国";

    private static final String BJ = "北京";

    private static DbSearcher searcher;

    private static Map<String, DataBlock> countryCoords = new HashMap<>();

    private static Map<String, DataBlock> provinceCoords = new HashMap<>();

    /**
     * 初始化Db文件
     */
    public synchronized static void init() {
        try {
            String targetDbPath = extractDbFile();
            DbConfig config = new DbConfig();
            searcher = new DbSearcher(config, targetDbPath);
            logger.info("Init geo searcher success!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Init geo searcher failed!");
        } finally {
            logger.info("Init geo searcher end!");
        }
    }

    /**
     * 从jar包出释放ip2region file
     *
     * @return String The db file path
     */
    private static String extractDbFile() {
        final String storePath = GEO_DB_STORE_PATH;
        if (!FileKit.isFileExist(storePath) || FileKit.fileSize(storePath) <= 0) {
            FileKit.deleteFile(storePath);
            ResourceKit.extractFileToDisk(GEO_DB_NAME, storePath,false);
        }
        return storePath;
    }

    /**
     * 初始化坐标信息
     *
     * @param dataBlocks dataBlocks
     */
    public static void initCoords(List<DataBlock> dataBlocks) {
        if (dataBlocks == null) {
            logger.error("DataBlocks is null, please check!");
            return;
        }
        for (DataBlock dataBlock : dataBlocks) {
            if (ZH.equals(dataBlock.getCountry()) && dataBlock.getProvince().contains(BJ)) {
                provinceCoords.put(dataBlock.getProvince(), dataBlock);
                countryCoords.put(dataBlock.getCountry(), dataBlock);
            } else if (ZH.equals(dataBlock.getCountry())) {
                provinceCoords.put(dataBlock.getProvince(), dataBlock);
            } else {
                countryCoords.put(dataBlock.getCountry(), dataBlock);
            }
        }
    }

    /**
     * 按照IP查询DataBlock
     *
     * @param ip ip
     * @return DataBlock
     */
    public static DataBlock search(String ip) {
        try {
            DataBlock dataBlock = searcher.btreeSearch(ip);
            if (dataBlock != null) {
                // 中国|华北|北京市|北京市|联通|41847
                return dataBlock;
            }
        } catch (Exception ignore) {
            logger.debug(ignore.getMessage());
        }

        return null;
    }

    /**
     * 按照DataBlock查询坐标信息
     *
     * @param dataBlock dataBlock
     * @return dataBlock
     */
    public static DataBlock getCoordByDataBlock(DataBlock dataBlock) {
        if (dataBlock == null) {
            return null;
        }
        DataBlock tmpDataBlock;

        if (ZH.equals(dataBlock.getCountry())) {
            tmpDataBlock = getCoordByProvince(dataBlock.getProvince());
        } else {
            tmpDataBlock = getCoordByCountry(dataBlock.getCountry());
        }

        if (tmpDataBlock != null) {
            dataBlock.setCountryCode(tmpDataBlock.getCountryCode());
            dataBlock.setLat(tmpDataBlock.getLat());
            dataBlock.setLng(tmpDataBlock.getLng());
        } else {
            logger.debug("Not found data block!");
        }

        return dataBlock;
    }

    /**
     * 按照国家名称查询坐标信息
     *
     * @param country country
     * @return dataBlock
     */
    public static DataBlock getCoordByCountry(String country) {
        return countryCoords.get(country);
    }


    /**
     * 按照省份名称查询坐标信息
     *
     * @param province province
     * @return DataBlock
     */
    public static DataBlock getCoordByProvince(String province) {
        return provinceCoords.get(province);
    }
}
