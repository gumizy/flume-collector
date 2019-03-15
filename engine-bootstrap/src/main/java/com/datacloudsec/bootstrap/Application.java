package com.datacloudsec.bootstrap;

import com.datacloudsec.bootstrap.agent.AgentComponent;
import com.datacloudsec.bootstrap.agent.EngineContext;
import com.datacloudsec.bootstrap.server.HttpServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.PORT;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Integer serverPort = PORT;
        try {
            if (args.length > 0) {
                serverPort = Integer.valueOf(args[0]);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Error in input parameters");
        }
        // 初始化采集器配置文件
        EngineContext.applicationInitialization();
        // 采集器内置解析规则等初始化
        EngineContext.componentsInitialization();
        // 生成source、channel、sink，
        // 持有所有组件生命周期
        AgentComponent.init();
        // 启动Server
        HttpServerManager.start(serverPort);

    }

}