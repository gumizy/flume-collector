package com.datacloudsec.core.parser;

import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.CounterGroup;
import com.datacloudsec.core.ParserProcessor;
import com.datacloudsec.core.ParserRunner;
import com.datacloudsec.core.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Date 2019/1/12 13:52
 */
public class PollableParserRunner extends ParserRunner {
    private static final Logger logger = LoggerFactory.getLogger(PollableParserRunner.class);
    private static final long backoffSleepIncrement = 1000;
    private static final long maxBackoffSleep = 5000;

    private AtomicBoolean shouldStop;

    private CounterGroup counterGroup;
    private PollingRunner runner;
    private Thread runnerThread;
    private LifecycleState lifecycleState;

    public PollableParserRunner() {
        shouldStop = new AtomicBoolean();
        counterGroup = new CounterGroup();
        lifecycleState = LifecycleState.IDLE;
    }

    @Override
    public void start() {
        ParserProcessor parser = getPolicy();
        parser.start();
        runner = new PollingRunner();
        runner.shouldStop = shouldStop;
        runner.counterGroup = counterGroup;
        runner.policy = parser;

        runnerThread = new Thread(runner);
        runnerThread.setName(getClass().getSimpleName() + "-" + parser.getClass().getSimpleName());
        runnerThread.start();

        lifecycleState = LifecycleState.START;
    }

    @Override
    public void stop() {

        runner.shouldStop.set(true);

        try {
            runnerThread.interrupt();
            runnerThread.join(5000);
        } catch (InterruptedException e) {
            logger.warn("Interrupted while waiting for polling runner to stop. Please report this.", e);
            Thread.currentThread().interrupt();
        }
        getPolicy().stop();
        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public LifecycleState getLifecycleState() {
        return null;
    }

    public static class PollingRunner implements Runnable {
        private ParserProcessor policy;
        private AtomicBoolean shouldStop;
        private CounterGroup counterGroup;

        @Override
        public void run() {
            while (!shouldStop.get()) {
                try {
                    if (policy.process().equals(ParserProcessor.Status.BACKOFF)) {
                        counterGroup.incrementAndGet("runner.backoffs");

                        Thread.sleep(Math.min(counterGroup.incrementAndGet("runner.backoffs.consecutive") * backoffSleepIncrement, maxBackoffSleep));
                    } else {
                        counterGroup.set("runner.backoffs.consecutive", 0L);
                    }
                } catch (InterruptedException e) {
                    logger.debug("Interrupted while processing an event. Exiting.");
                    counterGroup.incrementAndGet("runner.interruptions");
                } catch (Exception e) {
                    logger.error("Unable to deliver event. Exception follows.", e);
                    if (e instanceof EventDeliveryException) {
                        counterGroup.incrementAndGet("runner.deliveryErrors");
                    } else {
                        counterGroup.incrementAndGet("runner.errors");
                    }
                    try {
                        Thread.sleep(maxBackoffSleep);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            logger.debug("Polling runner exiting. Metrics:{}", counterGroup);
        }
    }
}
