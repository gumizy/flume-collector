package com.datacloudsec.bootstrap.agent;

import com.datacloudsec.config.ContextKit;
import com.datacloudsec.core.CounterGroup;
import com.datacloudsec.core.lifecycle.LifecycleAware;
import com.datacloudsec.core.lifecycle.LifecycleState;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollingPropertiesFileConfigurationProvider extends PropertiesFileConfigurationProvider implements LifecycleAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingPropertiesFileConfigurationProvider.class);

    private final EventBus eventBus;
    private final File file;
    private final int interval;
    private final CounterGroup counterGroup;
    private LifecycleState lifecycleState;

    private ScheduledExecutorService executorService;

    public PollingPropertiesFileConfigurationProvider(String agentName, File file, EventBus eventBus, int interval) {
        super(agentName, file);
        this.eventBus = eventBus;
        this.file = file;
        this.interval = interval;
        counterGroup = new CounterGroup();
        lifecycleState = LifecycleState.IDLE;
    }

    @Override
    public void start() {
        LOGGER.info("Configuration provider starting");

        Preconditions.checkState(file != null, "The parameter file must not be null");

        executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("conf-file-poller-%d").build());

        FileWatcherRunnable fileWatcherRunnable = new FileWatcherRunnable(file, counterGroup);

        executorService.scheduleWithFixedDelay(fileWatcherRunnable, 0, interval, TimeUnit.SECONDS);

        lifecycleState = LifecycleState.START;

        LOGGER.debug("Configuration provider started");
    }

    @Override
    public void stop() {
        LOGGER.info("Configuration provider stopping");

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                LOGGER.debug("File watcher has not terminated. Forcing shutdown of executor.");
                executorService.shutdownNow();
                while (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    LOGGER.debug("Waiting for file watcher to terminate");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted while waiting for file watcher to terminate");
            Thread.currentThread().interrupt();
        }
        lifecycleState = LifecycleState.STOP;
        LOGGER.debug("Configuration provider stopped");
    }

    @Override
    public synchronized LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    @Override
    public String toString() {
        return "{ file:" + file + " counterGroup:" + counterGroup + "  provider:" + getClass().getCanonicalName() + " agentName:" + getAgentName() + " }";
    }

    public class FileWatcherRunnable implements Runnable {

        private final File file;
        private final CounterGroup counterGroup;

        private long lastChange;

        public FileWatcherRunnable(File file, CounterGroup counterGroup) {
            super();
            this.file = file;
            this.counterGroup = counterGroup;
            this.lastChange = 0L;
        }

        @Override
        public void run() {
            LOGGER.debug("Checking file:{} for changes", file);

            counterGroup.incrementAndGet("file.checks");

            long lastModified = file.lastModified();
            // 判断系统授权是否存在
            if (!ContextKit.isAuthOK()) {
                MaterializedConfiguration conf = new SimpleMaterializedConfiguration();
                counterGroup.incrementAndGet("authorized.expiration");
                eventBus.post(conf);
                return;
            }
            // 判断是否开启
            if (!ContextKit.isEnabled()) {
                MaterializedConfiguration conf = new SimpleMaterializedConfiguration();
                counterGroup.incrementAndGet("not.opened");
                eventBus.post(conf);
                return;
            }
            if (lastModified > lastChange) {
                LOGGER.info("Reloading configuration file:{}", file);

                counterGroup.incrementAndGet("file.loads");

                lastChange = lastModified;

                try {
                    eventBus.post(getConfiguration());
                } catch (Exception e) {
                    LOGGER.error("Failed to load configuration data. Exception follows.", e);
                } catch (NoClassDefFoundError e) {
                    LOGGER.error("Failed to start agent because dependencies were not " + "found in classpath. Error follows.", e);
                } catch (Throwable t) {
                    // caught because the caller does not handle or log Throwables
                    LOGGER.error("Unhandled error", t);
                }
            }
        }
    }

}
