package com.datacloudsec.parser.process;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.core.conf.CusEmployeeObject;
import com.datacloudsec.core.conf.UserInfoConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author gumizy
 * @Date 2018/8/31 20:14
 */
public class UserInfoProcess implements ParserMetadataProcess {
    private static Logger logger = LoggerFactory.getLogger(UserInfoProcess.class);

    @Override
    public void process(String value) {
        logger.info("UserInfoProcess doUserInfoProcess start......");
        try {
            if (StringUtils.isNotBlank(value)) {
                List<CusEmployeeObject> jsonObject = JSON.parseArray(value, CusEmployeeObject.class);
                UserInfoConfig.onInit(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("UserInfoProcess doUserInfoProcess end......");
    }
}
