package com.datacloudsec.source.jdbc;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.core.PollableSource;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.parser.PollableParserRunner;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.parser.EventParserProcessor;
import com.datacloudsec.source.jdbc.reader.ReliableJdbcEventReader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.CONFIG_HOST;
import static com.datacloudsec.source.jdbc.JdbcSourceConstants.MINIMUM_BATCH_TIME_DEFAULT;
import static com.datacloudsec.source.jdbc.JdbcSourceConstants.MINIMUM_BATCH_TIME_PARAM;

public class JdbcSource extends AbstractSource implements Configurable, PollableSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSource.class);

    private long minimum_batch_time = MINIMUM_BATCH_TIME_DEFAULT;

    private ReliableJdbcEventReader reader;

    private SourceCounter sourceCounter;

    private String host;

    public JdbcSource() {
        super();
        reader = new ReliableJdbcEventReader();
    }

    @Override
    public void configure(Context context) {
        host = context.getString(CONFIG_HOST);
        Preconditions.checkNotNull(host, "Please set up the data source IP to match the collector");
        try {
            String value = context.getString(MINIMUM_BATCH_TIME_PARAM);
            if (value != null) {
                minimum_batch_time = Integer.parseInt(value);
            }
        } catch (Exception e) {
            throw new CollectorEngineException("Configured value for " + MINIMUM_BATCH_TIME_PARAM + " is not a number", e);
        }

        reader.configure(context);
        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
        parserProcessor = new EventParserProcessor(this);
        parserProcessor.configure(context);
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status status;

        try {
            List<SourceMessage> messages = reader.readMessages(host);
            if (!messages.isEmpty()) {
                parserProcessor.puts(messages);
                reader.commit();
                sourceCounter.addToEventReceivedCount(messages.size());
                sourceCounter.incrementAppendBatchReceivedCount();
                status = Status.READY;
                LOGGER.debug("Number of messages produced: " + messages.size());
            }else {
                status = Status.BACKOFF;
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            reader.rollback();
            throw new EventDeliveryException(e);
        }

        return status;
    }

    private void sleep(long batchStartTime) {
        long elapsedTime = System.currentTimeMillis() - batchStartTime;

        if (elapsedTime <= minimum_batch_time) {
            try {
                Thread.sleep(minimum_batch_time - elapsedTime);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        parserRunner = new PollableParserRunner();
        parserRunner.setPolicy(parserProcessor);
        parserRunner.start();
        sourceCounter.start();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        try {
            reader.close();
            sourceCounter.stop();
            parserRunner.stop();
        } catch (IOException e) {
        }

        LOGGER.info("JdbcSource {} stopped. Metrics: {}", getName(), sourceCounter);
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 1000;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 5000;
    }

    @VisibleForTesting
    public SourceCounter getCounters() {
        return sourceCounter;
    }

    @Override
    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }
}
