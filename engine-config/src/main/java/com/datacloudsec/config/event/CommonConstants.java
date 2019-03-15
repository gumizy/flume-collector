package com.datacloudsec.config.event;

import java.util.regex.Pattern;

public interface CommonConstants {

    // ID
    String ID = "id";

    // 索引类型
    String INDEX_TYPE = "index_type";

    // IP验证
    Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    // 未知用户
    Object UNKNOWN = "N/A";
}
