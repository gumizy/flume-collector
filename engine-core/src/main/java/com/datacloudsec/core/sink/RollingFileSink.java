package com.datacloudsec.core.sink;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.conf.BatchSizeSupported;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SinkCounter;
import com.datacloudsec.core.output.PathManager;
import com.datacloudsec.core.output.PathManagerFactory;
import com.datacloudsec.core.serialization.EventSerializer;
import com.datacloudsec.core.serialization.EventSerializerFactory;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RollingFileSink extends AbstractSink implements Configurable, BatchSizeSupported {

    private static final Logger logger = LoggerFactory.getLogger(RollingFileSink.class);
    private static final long defaultRollInterval = 3600;
    private static final int defaultBatchSize = 100;

    private int batchSize = defaultBatchSize;

    private File directory;
    private long rollInterval;
    private OutputStream outputStream;
    private ScheduledExecutorService rollService;

    private String serializerType;
    private Context serializerContext;
    private EventSerializer serializer;

    private SinkCounter sinkCounter;

    private PathManager pathController;
    private volatile boolean shouldRotate;

    public RollingFileSink() {
        shouldRotate = false;
    }

    @Override
    public void configure(Context context) {

        String pathManagerType = context.getString("sink.pathManager", "ROLLTIME");
        String directory = context.getString("directory","./output_file");
        String rollInterval = context.getString("rollInterval");

        serializerType = context.getString("serializer", "TEXT");
        serializerContext = new Context(context.getSubProperties("sink." + EventSerializer.CTX_PREFIX));

        Context pathManagerContext = new Context(context.getSubProperties("sink." + PathManager.CTX_PREFIX));
        pathController = PathManagerFactory.getInstance(pathManagerType, pathManagerContext);

        Preconditions.checkArgument(directory != null, "Directory may not be null");
        Preconditions.checkNotNull(serializerType, "Serializer type is undefined");

        if (rollInterval == null) {
            this.rollInterval = defaultRollInterval;
        } else {
            this.rollInterval = Long.parseLong(rollInterval);
        }

        batchSize = context.getInteger("sink.batchSize", defaultBatchSize);

        this.directory = new File(directory);
        if(!this.directory.exists()){
            this.directory.mkdirs();
        }
        if (sinkCounter == null) {
            sinkCounter = new SinkCounter(getName());
        }
    }

    @Override
    public void start() {
        logger.info("Starting {}...", this);
        sinkCounter.start();
        super.start();

        pathController.setBaseDirectory(directory);
        if (rollInterval > 0) {

            rollService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("rollingFileSink-roller-" + Thread.currentThread().getId() + "-%d").build());

            /*
             * Every N seconds, mark that it's time to rotate. We purposefully do NOT
             * touch anything other than the indicator flag to avoid error handling
             * issues (e.g. IO exceptions occuring in two different threads.
             * Resist the urge to actually perform rotation in a separate thread!
             */
            rollService.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    logger.debug("Marking time to rotate file {}", pathController.getCurrentFile());
                    shouldRotate = true;
                }

            }, rollInterval, rollInterval, TimeUnit.SECONDS);
        } else {
            logger.info("RollInterval is not valid, file rolling will not happen.");
        }
        logger.info("RollingFileSink {} started.", getName());
    }

    @Override
    public Status process() throws EventDeliveryException {
        if (shouldRotate) {
            logger.debug("Time to rotate {}", pathController.getCurrentFile());

            if (outputStream != null) {
                logger.debug("Closing file {}", pathController.getCurrentFile());

                try {
                    serializer.flush();
                    serializer.beforeClose();
                    outputStream.close();
                    sinkCounter.incrementConnectionClosedCount();
                    shouldRotate = false;
                } catch (IOException e) {
                    sinkCounter.incrementConnectionFailedCount();
                    throw new EventDeliveryException("Unable to rotate file " + pathController.getCurrentFile() + " while delivering event", e);
                } finally {
                    serializer = null;
                    outputStream = null;
                }
                pathController.rotate();
            }
        }

        if (outputStream == null) {
            File currentFile = pathController.getCurrentFile();
            logger.debug("Opening output stream for file {}", currentFile);
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(currentFile));
                serializer = EventSerializerFactory.getInstance(serializerType, serializerContext, outputStream);
                serializer.afterCreate();
                sinkCounter.incrementConnectionCreatedCount();
            } catch (IOException e) {
                sinkCounter.incrementConnectionFailedCount();
                throw new EventDeliveryException("Failed to open file " + pathController.getCurrentFile() + " while delivering event", e);
            }
        }

        Channel channel = getChannel();
        Event event;
        Status result = Status.READY;

        try {
            int eventAttemptCounter = 0;
            for (int i = 0; i < batchSize; i++) {
                event = channel.take();
                if (event != null) {
                    sinkCounter.incrementEventDrainAttemptCount();
                    eventAttemptCounter++;
                    serializer.write(event);

                } else {
                    // No events found, request back-off semantics from runner
                    result = Status.BACKOFF;
                    break;
                }
            }
            serializer.flush();
            outputStream.flush();
            sinkCounter.addToEventDrainSuccessCount(eventAttemptCounter);
        } catch (Exception ex) {
            sinkCounter.incrementEventWriteOrChannelFail(ex);
            throw new EventDeliveryException("Failed to process transaction", ex);
        } finally {
        }

        return result;
    }

    @Override
    public SinkCounter getSinkCounter() {
        return null;
    }

    @Override
    public void stop() {
        logger.info("RollingFile sink {} stopping...", getName());
        sinkCounter.stop();
        super.stop();

        if (outputStream != null) {
            logger.debug("Closing file {}", pathController.getCurrentFile());

            try {
                serializer.flush();
                serializer.beforeClose();
                outputStream.close();
                sinkCounter.incrementConnectionClosedCount();
            } catch (IOException e) {
                sinkCounter.incrementConnectionFailedCount();
                logger.error("Unable to close output stream. Exception follows.", e);
            } finally {
                outputStream = null;
                serializer = null;
            }
        }
        if (rollInterval > 0) {
            rollService.shutdown();

            while (!rollService.isTerminated()) {
                try {
                    rollService.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.debug("Interrupted while waiting for roll service to stop. " + "Please report this.", e);
                }
            }
        }
        logger.info("RollingFile sink {} stopped. Event metrics: {}", getName(), sinkCounter);
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public long getRollInterval() {
        return rollInterval;
    }

    public void setRollInterval(long rollInterval) {
        this.rollInterval = rollInterval;
    }

    @Override
    public long getBatchSize() {
        return batchSize;
    }
}
