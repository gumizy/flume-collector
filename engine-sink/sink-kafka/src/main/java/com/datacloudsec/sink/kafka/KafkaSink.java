

package com.datacloudsec.sink.kafka;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.config.conf.LogPrivacyUtil;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SinkCounter;
import com.datacloudsec.core.sink.AbstractSink;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

import static com.datacloudsec.sink.kafka.KafkaSinkConstants.*;

public class KafkaSink extends AbstractSink implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(KafkaSink.class);

    private final Properties kafkaProps = new Properties();
    private KafkaProducer producer;

    private String topic;
    private String bootStrapServers;
    private SinkCounter counter;

    public KafkaSink() {
        super();
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.READY;
        Channel channel = getChannel();
        try {
            List<Event> events = channel.takes();
            if (events == null || events.size() == 0) {
                result = Status.BACKOFF;
                return result;
            }
            if (this.producer != null) {
                boolean sendLog = true;
                for (Event event : events) {
                    long startTime = System.currentTimeMillis();
                    if (event.getValue().isEmpty()) {
                        result = Status.BACKOFF;
                        counter.incrementBatchEmptyCount();
                        logger.error("KafkaSink event value map is empty, will drop!");
                    } else {
                        String sendStr = event.valueMapToJSONString();
                        this.producer.send(new ProducerRecord<>(topic, sendStr));
                    }
                    long duration = System.currentTimeMillis() - startTime;
                    if (duration >= 5000) {
                        sendLog = false;
                        logger.warn("KafkaSink send connection timed out --> {}:{}", bootStrapServers);
                        counter.incrementEventWriteFail();
                        break;// 单次超时，结束循环，进入新一个循环周期
                    }
                }
                if (sendLog) {
                    logger.info("KafkaSink send events {}..., channel is {}...", events.size(), channel.getName());
                    counter.addToEventDrainAttemptCount(events.size());
                }
            } else {
                logger.error("KafkaSink producer is null!");
            }
        } catch (Exception ex) {
            String errorMsg = "Failed to publish events";
            logger.error("Failed to publish events", ex);
            counter.incrementEventWriteOrChannelFail(ex);
            throw new EventDeliveryException(errorMsg, ex);
        }

        return result;
    }

    @Override
    public SinkCounter getSinkCounter() {
        return counter;
    }

    @Override
    public synchronized void start() {
        producer = new KafkaProducer<>(kafkaProps);
        counter.start();
        super.start();
    }

    @Override
    public synchronized void stop() {
        producer.close();
        counter.stop();
        logger.info("Kafka Sink {} stopped. Metrics: {}", getName(), counter);
        super.stop();
    }

    @Override
    public void configure(Context context) {

        // topic
        topic = context.getString(TOPIC_CONFIG);
        if (topic == null || topic.isEmpty()) {
            topic = DEFAULT_SA_TOPIC;
            logger.warn("Topic was not specified. Using {} as the topic.", topic);
        }
        // server
        bootStrapServers = context.getString(BOOTSTRAP_SERVERS_CONFIG);
        if (bootStrapServers == null || bootStrapServers.isEmpty()) {
            bootStrapServers = DEFAULT_SA_SERVER;
            logger.warn("Kafka bootStrapServers was not specified. Using {} as the topic.", DEFAULT_SA_SERVER);
        }

        setProducerProps(context, bootStrapServers);
        if (logger.isDebugEnabled() && LogPrivacyUtil.allowLogPrintConfig()) {
            logger.debug("Kafka producer properties: {}", kafkaProps);
        }

        if (counter == null) {
            counter = new SinkCounter(getName());
        }
    }

    private void setProducerProps(Context context, String bootStrapServers) {
        kafkaProps.clear();
        kafkaProps.put(ProducerConfig.ACKS_CONFIG, context.getInteger(ACK) == null ? DEFAULT_SA_ACKS : context.getInteger(ACK));
        kafkaProps.put(ProducerConfig.BATCH_SIZE_CONFIG, context.getInteger(BATCH_SIZE) == null ? DEFAULT_SA_BATCH_SIZE : context.getInteger(BATCH_SIZE));
        kafkaProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, context.getInteger(BATCH_SIZE) == null ? DEFAULT_SA_MAX_BLOCK_MS : context.getInteger(BATCH_SIZE));

//        kafkaProps.put(ProducerConfig.ACKS_CONFIG, DEFAULT_SA_ACKS);
//        kafkaProps.put(ProducerConfig.BATCH_SIZE_CONFIG, DEFAULT_SA_BATCH_SIZE);
//        kafkaProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, DEFAULT_SA_MAX_BLOCK_MS);

        //Defaults overridden based on config
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, DEFAULT_SA_KEY_SERIALIZER);
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DEFAULT_SA_VALUE_SERIALIZER);
        kafkaProps.put(ProducerConfig.RETRIES_CONFIG, DEFAULT_SA_TRETRIES);
        kafkaProps.put(ProducerConfig.LINGER_MS_CONFIG, DEFAULT_SA_LINGER_MS);
        kafkaProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, DEFAULT_SA_BUFFER_MEMORY);
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
    }

}

