package com.datacloudsec.config.conf;

import com.google.common.base.Preconditions;

public class BasicRuleSql {
    private static final String TABLE_NAME = "dcsec_kv";

    public static final String COLUME_KEY = "key";
    public static final String COLUME_VALUE = "value";

    public static String sqlQueryAll() {
        return "select * from  " + TABLE_NAME + " ";
    }

    public static String sqlQueryByKey(String key) {
        Preconditions.checkNotNull(key, "sql sqlQueryByKey dcsec_kv key is necessarily");
        StringBuilder sql = new StringBuilder("select * from  " + TABLE_NAME);
        sql.append(" where key = " + "'" + key + "'");

        return sql.toString();
    }

    public static String sqlUpdateByKey(String key, String value) {
        Preconditions.checkNotNull(key, "sql updateByKey dcsec_kv key is necessarily");
        Preconditions.checkNotNull(value, "sql updateByKey dcsec_kv value is necessarily");

        StringBuilder sql = new StringBuilder(" update " + TABLE_NAME + "  set value = '" + value + "' where key = '" + key + "'");
        return sql.toString();
    }

    public static String sqlInsert(String key, String value) {
        Preconditions.checkNotNull(key, "sql insert " + TABLE_NAME + " key is necessarily");
        Preconditions.checkNotNull(value, "sql insert " + TABLE_NAME + " value is necessarily");

        StringBuilder sql = new StringBuilder("insert into " + TABLE_NAME + "(key,value) values('" + key + "','" + value + "') ");
        return sql.toString();
    }

    public enum RuleSqlKeyType {

        ENGINE("engine"), COLLECTOR("collector"), INFO_BASE("infobase"), ASSET("asset"), USER("user"), AUTHORIZATION("authorization");

        private String key;

        RuleSqlKeyType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

}
