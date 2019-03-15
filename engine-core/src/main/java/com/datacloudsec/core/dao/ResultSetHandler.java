package com.datacloudsec.core.dao;

import java.sql.ResultSet;

public interface ResultSetHandler {

    /**
     * @param rs 查询结果集
     * @return
     */
    Object handler(ResultSet rs);

    Object handlerMap(ResultSet rs);
}