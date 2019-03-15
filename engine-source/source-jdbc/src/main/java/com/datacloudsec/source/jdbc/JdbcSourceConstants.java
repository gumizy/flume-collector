package com.datacloudsec.source.jdbc;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.BASE_STORE_PATH;

/**
 * @Date 2019/1/18 19:21
 */
public final class JdbcSourceConstants {
    public static final String CONNECTION_DRIVER_DEFAULT = "oracle.jdbc.driver.OracleDriver";// 默认驱动
    public static final String CONNECTION_DRIVER_PARAM = "connectionDriver";//数据库驱动
    public static final String CONNECTION_URL_DEFAULT = "jdbc:oracle:oci:@";// 默认url
    public static final String CONNECTION_URL_PARAM = "connectionUrl";//数据库url
    public static final String USERNAME_PARAM = "username";// 用户名
    public static final String USERNAME_DEFAULT = "sys as sysdba";// 默认用户名
    public static final String PASSWORD_PARAM = "password";// 密码
    public static final String PASSWORD_DEFAULT = "sys";// 默认密码
    public static final String TABLE_NAME_PARAM = "table";// 读取表名称
    public static final String COLUMN_TO_COMMIT_PARAM = "columnToCommit";// 读取记录标识字段
    public static final String TYPE_COLUMN_TO_COMMIT_PARAM = "columnToCommitType";// 读取记录标识类型
    public static final ColumnType TYPE_COLUMN_TO_COMMIT_DEFUALT = ColumnType.TIMESTAMP;// 读取记录标识类型默认时间戳
    public static final String COMMITTED_VALUE_TO_LOAD_PARAM = "committtedValue";// 记录标识值，可选
    public static final String QUERY_PARAM = "query";// 查询条件，字符串查询sql，可选
    public static final String QUERY_PATH_PARAM = "reader.query.path";// 查询条件sql所在目录路径，可选
    public static final String COMMITTING_FILE_PATH_DEFAULT = BASE_STORE_PATH + "/committed_value.backup";//默认记录读取标识文件
    public static final String COMMITTING_FILE_PATH_PARAM = "reader.committingFile";// 记录读取标识文件，可选
    public static final String SCALE_AWARE_NUMERIC_PARAM = "reader.scaleAwareNumeric";// NUMERIC数字类型，四舍五入，可选
    public static final String EXPAND_BIG_FLOATS_PARAM = "reader.expandBigFloats";// 提高数字精度，使用BigDecimal，可选

    public static final String MINIMUM_BATCH_TIME_PARAM = "batch.minimumTime";// 最小休眠时间
    public static final long MINIMUM_BATCH_TIME_DEFAULT = 10000;// 默认休眠时间

    public static final int BATCH_SIZE_DEFAULT = 100;// 批处理大小
    public static final String BATCH_SIZE_PARAM = "batch.size";// 批处理大小参数

    public enum State {INITIALIZED, CONFIGURED}

    public enum ColumnType {STRING, TIMESTAMP, NUMERIC}

}
