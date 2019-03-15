package com.datacloudsec.bootstrap;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author gumizy
 * @Date 2019/1/7 16:06
 */
public class ApplicationTest {
    @Test
    public void applicationTest() throws InterruptedException {
        String[] args = new String[]{"F:\\github\\dcsec-collector-engine\\engine-bootstrap\\src\\test\\resources\\application.properties"};

        Application.main(args);
        Thread.sleep(1000000000);
    }

    @Test
    public void loadApplicationProperties() throws IOException {
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        properties.load(resource);
        System.out.println(JSON.toJSONString(properties, true));
        for (Object key : properties.keySet()) {
            String keyStr = String.valueOf(key);
            System.out.println("private String " + keyStr.replace(".", "_") + "=" + '"' + keyStr + '"' + ";");
        }
    }
}
