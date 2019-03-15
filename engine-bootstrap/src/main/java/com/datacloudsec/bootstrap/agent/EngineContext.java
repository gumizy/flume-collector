package com.datacloudsec.bootstrap.agent;

import com.datacloudsec.config.conf.BasicRuleSql;
import com.datacloudsec.config.conf.parser.rule.EngineKvEntity;
import com.datacloudsec.config.geo.GeoSearcher;
import com.datacloudsec.config.tools.FileKit;
import com.datacloudsec.config.tools.ResourceKit;
import com.datacloudsec.core.dao.DbUtils;
import com.datacloudsec.parser.process.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.*;
import static com.datacloudsec.config.conf.BasicRuleSql.RuleSqlKeyType.*;

/**
 * EngineContext
 */
public class EngineContext {

    private static final Logger logger = LoggerFactory.getLogger(EngineContext.class);

    private static long bootTime = System.currentTimeMillis();

    /**
     * 采集应用程序初始化,只被调用一次即可
     */
    public static void applicationInitialization() {
        logger.info("Engine context applicationInitialization...");

        bootTime = System.currentTimeMillis();

        // 配置路径
        FileKit.ensureDirExist(BASE_STORE_PATH);
        logger.info("Engine config base path is: {}...", FileKit.absolutePath(BASE_STORE_PATH));

        // 数据库配置
        collectorDatabaseInit();

        // doc配置说明
        extractDocument();

        // 初始化授权信息
        authorizationInit();

        // agent配置信息
        applicationProptiesInit();

        // GEO IP库
        GeoSearcher.init();

        logger.info("Engine context applicationInitialization success!");
    }

    private static void extractDocument() {
        copyDocument(DOC_EXAMPLE_PATH, DOC_EXAMPLE_STORE_PATH);
        copyDocument(DOC_EXAMPLE_MULTIPLE_PATH, DOC_EXAMPLE_MULTIPLE_STORE_PATH);
        copyDocument(DOC_README_PATH, DOC_README_STORE_PATH);
    }

    private static void copyDocument(String srcPath, String dstPath) {
        if (FileKit.isDirExist(dstPath)) {
            logger.info("Document description file already exists:{}", dstPath);
        } else {
            FileKit.deleteFile(dstPath);
            ResourceKit.extractFileToDisk(srcPath, dstPath);
            logger.info("Document description file path is:{}", dstPath);
        }
    }

    private static void applicationProptiesInit() {
        String srcPath = ENGINE_CONFIG_NAME;
        String dstPath = ENGINE_CONFIG_STORE_PATH;
        if (!FileKit.isFileExist(dstPath) || FileKit.fileSize(dstPath) <= 0) {
            FileKit.deleteFile(dstPath);
            ResourceKit.extractFileToDisk(srcPath, dstPath);
        }
    }

    public static void applicationProptiesReset() {
        String srcPath = ENGINE_CONFIG_NAME;
        String dstPath = ENGINE_CONFIG_STORE_PATH;
        ResourceKit.extractFileToDisk(srcPath, dstPath, true);
    }

    /**
     * 采集应用程序初始化
     */
    public static void componentsInitialization() {
        logger.info("Engine context componentsInitialization...");

        // 采集器配置
        collectorInit();

        // 资产信息配置
        assetInfoInit();

        // 情报库配置
        infoBaseInit();

        // 用户信息
        userInfoInit();

        logger.info("Engine context componentsInitialization success!");
    }

    public static void authorizationInit() {
        ParserMetadataProcess process;
        String key = AUTHORIZATION.getKey();
        try {
            Map<String, Object> map = DbUtils.get(BasicRuleSql.sqlQueryByKey(key));
            if (map == null || map.get(key) == null) {
                logger.error("Acquisition Engine Unauthorized");
            } else {
                process = new AuthProcess();
                process.process(String.valueOf(map.get(key)));
            }
        } catch (Exception e) {
            logger.error("Acquisition Engine Unauthorized");
        }
    }

    public static void userInfoInit() {
        ParserMetadataProcess process = new AssetProcess();
        String key = ASSET.getKey();
        commonProcess(process, key);
    }

    public static void assetInfoInit() {
        ParserMetadataProcess process = new UserInfoProcess();
        String key = USER.getKey();
        commonProcess(process, key);
    }

    public static void infoBaseInit() {
        ParserMetadataProcess process = new InfoBaseProcess();
        String key = INFO_BASE.getKey();
        commonProcess(process, key);
    }

    public static void collectorInit() {
        ParserMetadataProcess process = new CollectorProcess();
        String key = COLLECTOR.getKey();
        commonProcess(process, key);
    }

    private static synchronized void commonProcess(ParserMetadataProcess process, String key) {
        EngineKvEntity kv = DbUtils.get(BasicRuleSql.sqlQueryByKey(key), EngineKvEntity.class);
        if (kv != null && kv.getValue() != null) {
            process.process(kv.getValue());
        }
    }

    public static void saveOrReload(String key, String value) {
        Map<String, Object> query = DbUtils.get(BasicRuleSql.sqlQueryByKey(key));
        if (query != null && query.size() > 0) {
            DbUtils.update(BasicRuleSql.sqlUpdateByKey(key, value));
        } else {
            DbUtils.update(BasicRuleSql.sqlInsert(key, value));
        }
    }

    /**
     * 获取启动时间
     *
     * @return long
     */
    public static long getBootTime() {
        return bootTime;
    }

    private static void collectorDatabaseInit() {
        if (FileKit.isFileExist(ENGINE_DB_STORE_PATH) && FileKit.fileSize(ENGINE_DB_STORE_PATH) > 0) {
            logger.info("{} file exist, do not copy again.", ENGINE_DB_STORE_PATH);
        } else {
            logger.info("{} file not exist, will copy...", ENGINE_DB_STORE_PATH);
            FileKit.deleteFile(ENGINE_DB_STORE_PATH);
            ResourceKit.extractFileToDisk(ENGINE_DB_NAME, ENGINE_DB_STORE_PATH);
        }
    }
}
