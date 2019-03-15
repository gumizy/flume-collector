package com.datacloudsec.bootstrap.agent;

import com.datacloudsec.core.Channel;
import com.datacloudsec.core.SinkRunner;
import com.datacloudsec.core.SourceRunner;
import com.datacloudsec.core.lifecycle.LifecycleAware;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.datacloudsec.core.lifecycle.LifecycleSupervisor;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.AGENTN_NAME;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.ENGINE_CONFIG_STORE_PATH;

/**
 * @Date 2019/1/21 16:56
 */
public class AgentComponent {
    private static final Logger logger = LoggerFactory.getLogger(AgentComponent.class);

    private final List<LifecycleAware> components;
    private final LifecycleSupervisor supervisor;
    private static MaterializedConfiguration materializedConfiguration;
    private final ReentrantLock lifecycleLock = new ReentrantLock();

    public static MaterializedConfiguration getMaterializedConfiguration() {
        return materializedConfiguration;
    }

    public AgentComponent(List<LifecycleAware> components) {
        this.components = components;
        supervisor = new LifecycleSupervisor();
    }

    public AgentComponent() {
        this(new ArrayList<>(0));
    }

    public void start() {
        lifecycleLock.lock();
        try {
            for (LifecycleAware component : components) {
                supervisor.supervise(component, LifecycleState.START);
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void stop() {
        lifecycleLock.lock();
        stopAllComponents();
        try {
            supervisor.stop();
        } finally {
            lifecycleLock.unlock();
        }
    }

    private void stopAllComponents() {
        if (this.materializedConfiguration != null) {
            logger.info("============================Shutting down configuration: {}", this.materializedConfiguration + "============================");
            for (Map.Entry<String, SourceRunner> entry : this.materializedConfiguration.getSourceRunners().entrySet()) {
                try {
                    logger.info("[**********Stopping Source " + entry.getKey() + "**********]");
                    supervisor.unsupervise(entry.getValue());
                } catch (Exception e) {
                    logger.error("[**********Error while stopping {}", entry.getValue(), e + "**********]");
                }
            }

            for (Map.Entry<String, SinkRunner> entry : this.materializedConfiguration.getSinkRunners().entrySet()) {
                try {
                    logger.info("[**********Stopping Sink " + entry.getKey() + "**********]");
                    supervisor.unsupervise(entry.getValue());
                } catch (Exception e) {
                    logger.error("[**********Error while stopping {}", entry.getValue(), e + "**********]");
                }
            }

            for (Map.Entry<String, Channel> entry : this.materializedConfiguration.getChannels().entrySet()) {
                try {
                    logger.info("[**********Stopping Channel " + entry.getKey() + "**********]");
                    supervisor.unsupervise(entry.getValue());
                } catch (Exception e) {
                    logger.error("[**********Error while stopping {}", entry.getValue(), e + "**********]");
                }
            }
        }
    }

    private void startAllComponents(MaterializedConfiguration materializedConfiguration) {
        logger.info("============================ Starting new configuration:{}", materializedConfiguration + "============================");

        this.materializedConfiguration = materializedConfiguration;

        for (Map.Entry<String, Channel> entry : materializedConfiguration.getChannels().entrySet()) {
            try {
                logger.info("[**********Starting Channel " + entry.getKey() + "**********]");
                supervisor.supervise(entry.getValue(), LifecycleState.START);
            } catch (Exception e) {
                logger.error("[**********Error while starting {}", entry.getValue(), e + "**********]");
            }
        }

        /*
         * Wait for all channels to start.
         */
        for (Channel ch : materializedConfiguration.getChannels().values()) {
            while (ch.getLifecycleState() != LifecycleState.START) {
                try {
                    logger.info("[**********Waiting for channel: " + ch.getName() + " to start. Sleeping for 500 ms" + "**********]");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("[**********Interrupted while waiting for channel to start.", e + "**********]");
                    Throwables.propagate(e);
                }
            }
        }

        for (Map.Entry<String, SinkRunner> entry : materializedConfiguration.getSinkRunners().entrySet()) {
            try {
                logger.info("[**********Starting Sink " + entry.getKey() + "**********]");
                supervisor.supervise(entry.getValue(), LifecycleState.START);
            } catch (Exception e) {
                logger.error("[**********Error while starting {}", entry.getValue(), e + "**********]");
            }
        }

        for (Map.Entry<String, SourceRunner> entry : materializedConfiguration.getSourceRunners().entrySet()) {
            try {
                logger.info("[**********Starting Source " + entry.getKey() + "**********]");
                supervisor.supervise(entry.getValue(), LifecycleState.START);
            } catch (Exception e) {
                logger.error("[**********Error while starting {}", entry.getValue(), e + "**********]");
            }
        }

    }

    /**
     * 监听回调application.properties配置文件
     *
     * @param conf
     */
    @Subscribe
    public void handleConfigurationEvent(MaterializedConfiguration conf) {
        try {
            lifecycleLock.lockInterruptibly();
            stopAllComponents();
            startAllComponents(conf);
        } catch (InterruptedException e) {
            logger.info("Interrupted while trying to handle configuration event");
            return;
        } finally {
            if (lifecycleLock.isHeldByCurrentThread()) {
                lifecycleLock.unlock();
            }
        }
    }

    public static void init() {
        try {
            String agentName = AGENTN_NAME;
            // 以下操作是核心操作，解析application.properties内容
            // 并拼装成source、channel、sink运行组件
            // 之后通过监听模式传递到启动、停止工作模式
            boolean reload = true;// 热加载
            int reloadInterval = 3;// 测试用3秒，原始值30秒
            File configurationFile = new File(ENGINE_CONFIG_STORE_PATH);
            if (!configurationFile.exists()) {
                throw new ParseException("The specified configuration file does not exist: " + ENGINE_CONFIG_STORE_PATH);
            }
            AgentComponent agent;
            List<LifecycleAware> components = Lists.newArrayList();
            EventBus eventBus = new EventBus("agent-event-bus");
            if (reload) {
                PollingPropertiesFileConfigurationProvider configurationProvider = new PollingPropertiesFileConfigurationProvider(agentName, configurationFile, eventBus, reloadInterval);
                components.add(configurationProvider);
                agent = new AgentComponent(components);
                eventBus.register(agent);
                agent.start();
            } else {
                PropertiesFileConfigurationProvider configurationProvider = new PropertiesFileConfigurationProvider(agentName, configurationFile);
                agent = new AgentComponent();
                eventBus.register(agent);
                eventBus.post(configurationProvider.getConfiguration());
            }

            final AgentComponent appReference = agent;
            Runtime.getRuntime().addShutdownHook(new Thread("agent-shutdown-hook") {
                @Override
                public void run() {
                    appReference.stop();
                }
            });
        } catch (ParseException e) {
            logger.error("A fatal error occurred while running. Exception follows.", e);
            System.exit(-1);
        }

    }

}
