package com.datacloudsec.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.datacloudsec.config.tools.PropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;

/**
 * DbPool
 */
public class DbPool {

    private static Logger logger = LoggerFactory.getLogger(DbPool.class);

    private static DruidDataSource dataSource = null;

    /*
     * 初始化数据库连接池
     */
    static {
        init();
    }

    private static void init() {
        try {
            Properties properties = PropKit.use(ConfigFileName.sqlite.getValue()).getProperties();
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logger.error("Connect to database error: ", e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 从连接池中获取连接
     *
     * @return DruidPooledConnection
     * @throws SQLException SQLException
     */
    public static DruidPooledConnection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    enum ConfigFileName {

        sqlite("sqlite.properties");

        private String value;

        ConfigFileName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
