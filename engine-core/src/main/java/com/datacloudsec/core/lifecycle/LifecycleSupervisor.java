package com.datacloudsec.core.lifecycle;

import com.datacloudsec.config.CollectorEngineException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class LifecycleSupervisor implements LifecycleAware {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleSupervisor.class);

    private Map<LifecycleAware, Future<?>> monitorFutures;

    private ThreadPoolExecutor monitorService;

    private LifecycleState lifecycleState;

    /**
     * 10个线程的线程池，使用了guava的ThreadFactoryBuilder，
     * guava的ThreadFactoryBuilder可以传入一个namFormat参数用户来表示线程的name，
     * 它内部会使用数字增量表示%d，比如一下的nameFormat，10个线程，
     * 名字分别是thread-call-runner-1，thread-call-runner-2 … thread-call-runner-10:
     */
    public LifecycleSupervisor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("lifecycleSupervisor-" + Thread.currentThread().getId() + "-%d").build();

        lifecycleState = LifecycleState.IDLE;
        monitorFutures = new HashMap<>();
        monitorService = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public synchronized void start() {
        logger.info("Starting lifecycle supervisor {}", Thread.currentThread().getId());
        lifecycleState = LifecycleState.START;
        logger.debug("Lifecycle supervisor started");
    }

    @Override
    public synchronized void stop() {
        logger.info("Stopping lifecycle supervisor {}", Thread.currentThread().getId());
        if (monitorService != null) {
            monitorService.shutdown();
            try {
                monitorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for monitor service to stop");
            }
            if (!monitorService.isTerminated()) {
                monitorService.shutdownNow();
                try {
                    while (!monitorService.isTerminated()) {
                        monitorService.awaitTermination(10, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException e) {
                    logger.error("Interrupted while waiting for monitor service to stop");
                }
            }
        }
        /* If we've failed, preserve the error state. */
        if (lifecycleState.equals(LifecycleState.START)) {
            lifecycleState = LifecycleState.STOP;
        }
        monitorFutures.clear();
        logger.debug("Lifecycle supervisor stopped");
    }

    public synchronized void supervise(LifecycleAware lifecycleAware, LifecycleState desiredState) {
        if (this.monitorService.isShutdown() || this.monitorService.isTerminated() || this.monitorService.isTerminating()) {
            throw new CollectorEngineException("Supervise called on " + lifecycleAware + "after shutdown has been initiated. " + lifecycleAware + " will not" + " be started");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Supervising service:{}  desiredState:{}", new Object[]{lifecycleAware, desiredState});
        }

        MonitorRunnable monitorRunnable = new MonitorRunnable();
        monitorRunnable.lifecycleAware = lifecycleAware;
        monitorRunnable.monitorService = monitorService;
        monitorRunnable.lifecycleState = desiredState;

        Future<?> future = monitorService.submit(monitorRunnable);
        monitorFutures.put(lifecycleAware, future);
    }

    public synchronized void unsupervise(LifecycleAware lifecycleAware) {
        logger.debug("Unsupervising service:{}", lifecycleAware);

        synchronized (lifecycleAware) {
            logger.info("Stopping component: {}", lifecycleAware);
            lifecycleAware.stop();
        }
        monitorFutures.get(lifecycleAware).cancel(false);
        monitorService.purge();
        monitorFutures.remove(lifecycleAware);
    }

    @Override
    public synchronized LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    public static class MonitorRunnable implements Runnable {

        public ThreadPoolExecutor monitorService;
        public LifecycleAware lifecycleAware;
        public LifecycleState lifecycleState;

        @Override
        public void run() {
            try {
                synchronized (lifecycleAware) {
                    switch (lifecycleState) {
                        case START:
                            try {
                                lifecycleAware.start();
                            } catch (Throwable e) {
                                logger.error("Unable to start " + lifecycleAware + " - Exception follows.", e);
                                if (e instanceof Error) {
                                    try {
                                        lifecycleAware.stop();
                                        logger.warn("Component {} stopped, since it could not be" + "successfully started due to missing dependencies", lifecycleAware);
                                    } catch (Throwable e1) {
                                        logger.error("Unsuccessful attempt to " + "shutdown component: {} due to missing dependencies." + " Please shutdown the agent" + "or disable this component, or the agent will be" + "in an undefined state.", e1);
                                        if (e1 instanceof Error) {
                                            throw (Error) e1;
                                        }
                                    }
                                }
                            }
                            break;
                        case STOP:
                            try {
                                lifecycleAware.stop();
                            } catch (Throwable e) {
                                logger.error("Unable to stop " + lifecycleAware + " - Exception follows.", e);
                                if (e instanceof Error) {
                                    throw (Error) e;
                                }
                            }
                            break;
                        default:
                            logger.warn("I refuse to acknowledge {} as a desired state", lifecycleAware.getLifecycleState());
                    }
                }
            } catch (Throwable t) {
                logger.error("Unexpected error", t);
            }
            logger.debug("Status check complete");
        }
    }

}
