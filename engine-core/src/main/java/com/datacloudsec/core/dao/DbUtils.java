package com.datacloudsec.core.dao;

import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据库操作封装类
 */
public class DbUtils {

    private static Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private static Lock lock = new ReentrantLock();

    /**
     * 基础SQL查询
     *
     * @param querySql query sql
     * @return Map
     */
    public static Map<String, List<String>> query(String querySql) {
        final Map<String, List<String>> result = new HashMap<>();
        DbUtils.query(querySql, result);
        return result;
    }

    /**
     * sql查询，返回HashMap<String, List<String>>，其中key为查询表每行数据的pk,value为每行数据的项
     *
     * @param querySql querySql
     * @param result   result
     * @return Map
     */
    public static Map<String, List<String>> query(String querySql, Map<String, List<String>> result) {
        DruidPooledConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(querySql);

            int columns = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                List<String> list = new ArrayList<>();
                for (int i = 1; i <= columns; i++) {
                    String value = resultSet.getString(i);
                    list.add(value);
                }
                String id = resultSet.getString("key");
                result.put(id, list);
            }

        } catch (SQLException e) {
            logger.error("database query() error:{} ", e.getMessage());
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
        return result;
    }

    /**
     * 批量插入数据
     *
     * @param insertSql insertSql
     * @param dataList  dataList
     */
    public static boolean insertBatch(String insertSql, List<HashMap<Integer, Object>> dataList) {
        if (dataList == null) return false;
        boolean flag = true;

        DruidPooledConnection connection = null;

        try {
            connection = DbPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            for (Map<Integer, Object> columnCache : dataList) {
                for (int i = 1; i <= columnCache.size(); i++) {
                    setValue(preparedStatement, i, columnCache.get(i));
                }
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            flag = false;
            logger.error("database insert() error: " + e.getMessage());
        } finally {
            closeQuietly(connection);
        }

        return flag;
    }

    /**
     * 根据sql更新数据库,更新成功返回true,否则返回false
     *
     * @param updateSql updateSql
     * @return boolean
     */
    public static boolean update(String updateSql) {
        lock.lock();
        boolean flag = false;

        DruidPooledConnection connection = null;
        Statement statement = null;
        int updateCount;

        try {
            connection = DbPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateSql);
            if (updateCount == 0) {
                logger.debug("No record updated! updateSQL: " + updateSql);
            } else {
                flag = true;
            }
        } catch (SQLException e) {
            logger.error("database update() error: ", e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
            lock.unlock();
        }

        return flag;
    }

    /**
     * 根据sql更新数据库,更新成功返回true,否则返回false,使用prepareStatement
     *
     * @param updateSql updateSql
     * @param dataList  dataList
     * @return
     */
    public static boolean update(String updateSql, List<Object> dataList) {
        lock.lock();
        if (dataList == null) return false;
        boolean flag = false;

        DruidPooledConnection connection = null;
        Statement statement = null;
        int updateCount;

        try {
            connection = DbPool.getConnection();
            statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSql);
            for (int i = 1; i <= dataList.size(); i++) {
                setValue(preparedStatement, i, dataList.get(i - 1));
            }
            updateCount = preparedStatement.executeUpdate();
            if (updateCount == 0) {
                logger.debug("No record updated! updateSQL: " + updateSql);
            } else {
                flag = true;
            }
        } catch (SQLException e) {
            logger.error("database update() error: ", e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
            lock.unlock();
        }

        return flag;
    }

    /**
     * 设置statement值
     *
     * @param preparedStatement preparedStatement
     * @param index             index
     * @param value             value
     * @throws SQLException SQLException
     */
    private static void setValue(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                preparedStatement.setNull(index, Types.VARCHAR);
            } else {
                preparedStatement.setString(index, (String) value);
            }
        } else if (value instanceof Integer) {
            preparedStatement.setInt(index, (Integer) value);
        } else if (value instanceof Long) {
            preparedStatement.setLong(index, (Long) value);
        }
    }

    /**
     * 关闭连接
     *
     * @param closeable closeable
     */
    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static <T> T get(String sql, Object params[], Class<T> clazz) {
        Object select = select(sql, params, new BeanHandler(clazz), false);
        return (T) select;
    }

    public static <T> T get(String sql, Class<T> clazz) {
        Object select = select(sql, new Object[]{}, new BeanHandler(clazz), false);
        return (T) select;
    }

    public static Map<String, Object> get(String sql) {
        Object select = select(sql, new Object[]{}, new BeanHandler(), true);
        return (Map<String, Object>) select;
    }

    public static List<Map<String, Object>> queryList(String sql, Object params[]) {
        if (params == null) {
            params = new Object[]{};
        }
        Object select = select(sql, params, new BeanListHandler(), true);
        return (List<Map<String, Object>>) select;
    }

    public static <T> List<T> queryList(String sql, Object params[], Class<T> clazz) {
        if (params == null) {
            params = new Object[]{};
        }
        Object select = select(sql, params, new BeanListHandler(clazz), false);
        return (List<T>) select;
    }

    public static <T> List<T> queryAll(String sql, Class<T> clazz) {
        Object select = select(sql, new Object[]{}, new BeanListHandler(clazz), false);
        return (List<T>) select;
    }

    /**
     * @param sql    要执行的SQL
     * @param params 执行SQL时使用的参数
     * @param rsh    查询返回的结果集处理器
     * @return
     * @Method: query
     * @Description:万能查询 实体的R操作，除SQL语句不同之外，根据操作的实体不同，对ResultSet的映射也各不相同，
     * 因此可义一个query方法，除以参数形式接收变化的SQL语句外，可以使用策略模式由qurey方法的调用者决定如何把ResultSet中的数据映射到实体对象中。
     */
    private static Object select(String sql, Object[] params, ResultSetHandler rsh, boolean notBeanEntity) {
//        logger.info("class=>{},params=>{}", sql, Arrays.toString(params));

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Object handler = null;
        try {
            conn = DbPool.getConnection();
            st = conn.prepareStatement(sql);
            if(params != null) {
                for (int i = 0; i < params.length; i++) {
                    st.setObject(i + 1, params[i]);
                }
            }
            rs = st.executeQuery();
            /**
             * 对于查询返回的结果集处理使用到了策略模式，
             * 在设计query方法时，query方法事先是无法知道用户对返回的查询结果集如何进行处理的，即不知道结果集的处理策略，
             * 那么这个结果集的处理策略就让用户自己提供，query方法内部就调用用户提交的结果集处理策略进行处理
             * 为了能够让用户提供结果集的处理策略，需要对用户暴露出一个结果集处理接口ResultSetHandler
             * 用户只要实现了ResultSetHandler接口，那么query方法内部就知道用户要如何处理结果集了
             */
            if (notBeanEntity) {
                handler = rsh.handlerMap(rs);
            } else {
                handler = rsh.handler(rs);
            }
        } catch (Exception e) {
            logger.info("select error ", e);
        } finally {
            release(conn, st, rs);
        }
        return handler;
    }

    /**
     * @param sql    要执行的SQL
     * @param params 执行SQL时使用的参数
     * @throws SQLException
     * @Method: update
     * @Description: 万能更新
     * 所有实体的CUD操作代码基本相同，仅仅发送给数据库的SQL语句不同而已，
     * 因此可以把CUD操作的所有相同代码抽取到工具类的一个update方法中，并定义参数接收变化的SQL语句
     */
    public static void update(String sql, Object params[]) {
        lock.lock();
//        logger.info("class=>{},params=>{}", sql, Arrays.toString(params));
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DbPool.getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            int i = st.executeUpdate();
            logger.info("update num is : {}", i);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            release(conn, st, rs);
            lock.unlock();
        }
    }

    /**
     * @param conn
     * @param st
     * @param rs
     * @Method: release
     * @Description: 释放资源，
     * 要释放的资源包括Connection数据库连接对象，负责执行SQL命令的Statement对象，存储查询结果的ResultSet对象
     */
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                //关闭存储查询结果的ResultSet对象
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (st != null) {
            try {
                //关闭负责执行SQL命令的Statement对象
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                //关闭Connection数据库连接对象
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
