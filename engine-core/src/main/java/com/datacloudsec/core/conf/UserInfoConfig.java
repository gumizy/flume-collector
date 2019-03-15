package com.datacloudsec.core.conf;

 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author gumizy
 * @Date 2018/8/31 20:19
 */
public class UserInfoConfig {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoConfig.class);
    private static Map<String, CusEmployeeObject> cusEmployeeObjects = Collections.emptyMap();

    public static Map<String, CusEmployeeObject> getCusEmployeeObjects() {
        return cusEmployeeObjects;
    }

    public static synchronized void onInit(List<CusEmployeeObject> cusCusEmployeeList) {
        cusEmployeeObjects.clear();
        cusEmployeeObjects = cusCusEmployeeList.stream().collect(Collectors.toMap(CusEmployeeObject::getUserName, e -> e));
        logger.info("Init cusDepartment end!");
    }
}
