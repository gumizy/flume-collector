package com.datacloudsec.source.kafka;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.config.conf.LogPrivacyUtil;
import com.datacloudsec.config.conf.source.SimpleSourceMessage;
import com.datacloudsec.core.PollableSource;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.parser.PollableParserRunner;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.core.source.PollableSourceConstants;
import com.datacloudsec.parser.EventParserProcessor;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.CONFIG_HOST;
import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;
import static com.datacloudsec.core.PollableSource.Status.READY;
import static com.datacloudsec.source.kafka.KafkaSourceConstants.*;

/**
 * @Date 2019/1/28 10:15
 */
public class KafkaSource extends AbstractSource implements Configurable, PollableSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSource.class);

    private final Properties kafkaProps = new Properties();
    private KafkaConsumer<String, String> consumer;

    private String[] topics;
    private int maxBatchDurationMillis;

    private String bootStrapServers;
    private SourceCounter counter;

    private String host;

    public KafkaSource() {
        super();
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.BACKOFF;
        Duration duration = Duration.ofMillis(maxBatchDurationMillis);
        ConsumerRecords<String, String> records = consumer.poll(duration);
        if (records != null && !records.isEmpty()) {
            for (ConsumerRecord<String, String> record : records) {
                String eventLog = record.value();

                SimpleSourceMessage sourceLogMsg = new SimpleSourceMessage(host);
                sourceLogMsg.setHeaders(PARSER_IDENTIFICATION, host + COLLECT_TYPE_SEPARATOR + COLLECT_TYPE_KAFKA);
                sourceLogMsg.setMessage(eventLog);

                parserProcessor.put(sourceLogMsg);
            }
            result = READY;
        }
        return result;
    }

    @Override
    public synchronized void start() {

        parserRunner = new PollableParserRunner();
        parserRunner.setPolicy(parserProcessor);
        parserRunner.start();

        //initialize a consumer.
        consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(Arrays.asList(topics));

        counter.start();
        super.start();
    }

    @Override
    public synchronized void stop() {
        if (consumer != null) {
            consumer.wakeup();
            consumer.close();
        }
        if (counter != null) {
            counter.stop();
        }
        LOGGER.info("Kafka Sink {} stopped. Metrics: {}", getName(), counter);
        super.stop();

        parserRunner.stop();
    }

    @Override
    public void configure(Context context) {

        validContext(context);

        maxBatchDurationMillis = context.getInteger(KafkaSourceConstants.BATCH_DURATION_MS, KafkaSourceConstants.DEFAULT_BATCH_DURATION);

        setProducerProps(context, bootStrapServers);
        if (LOGGER.isDebugEnabled() && LogPrivacyUtil.allowLogPrintConfig()) {
            LOGGER.debug("Kafka producer properties: {}", kafkaProps);
        }

        if (counter == null) {
            counter = new SourceCounter(getName());
        }
        parserProcessor = new EventParserProcessor(this);
        parserProcessor.configure(context);
    }

    private void validContext(Context ctx) {
        host = ctx.getString(CONFIG_HOST);
        Preconditions.checkNotNull(host, "Kafka source host is invalid ,Please set up the data source IP to match the collector");

        String topicConfig = ctx.getString(TOPIC_CONFIG);
        Preconditions.checkState(StringUtils.isNotBlank(topicConfig), "Kafka topic can not be empty");

        topics = topicConfig.split("\\s+");
        Preconditions.checkState(topics.length != 0, "Kafka topic can not be empty");
        bootStrapServers = ctx.getString(BOOTSTRAP_SERVERS_CONFIG);
        Preconditions.checkState(StringUtils.isNotBlank(bootStrapServers), "Kafka bootStrapServers can not be empty");
    }

    private void setProducerProps(Context context, String bootStrapServers) {
        kafkaProps.clear();

        kafkaProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, context.getInteger(TIMEOUT) == null ? SA_SESSION_TIMEOUT_MS : context.getInteger(TIMEOUT));
        kafkaProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, context.getInteger(INTERVAL) == null ? SA_AUTO_COMMIT_INTERVAL_MS : context.getInteger(INTERVAL));
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, context.getString(GROUP_ID) == null ? SA_DEFAULT_GROUP_ID : context.getString(GROUP_ID));
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);

        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DEFAULT_SA_KEY_DESERIALIZER);
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DEFAULT_SA_VALUE_DESERIALIZER);

    }

    @Override
    public SourceCounter getSourceCounter() {
        return counter;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return PollableSourceConstants.DEFAULT_BACKOFF_SLEEP_INCREMENT;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return PollableSourceConstants.DEFAULT_MAX_BACKOFF_SLEEP;
    }
}
