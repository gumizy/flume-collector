package com.datacloudsec.source.redis;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.PollableSource;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.core.source.PollableSourceConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import static com.datacloudsec.core.PollableSource.Status.READY;

/**
 * @Date 2019/1/28 10:15
 */
public class RedisSource extends AbstractSource implements Configurable, PollableSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSource.class);

    private static final String DATA_KEY = "collector.channel.redis.channel";

    private static final int BATCH_SIZE = 1000;

    private volatile Jedis jedis = null;

    public RedisSource() {
        super();
    }

    private SourceCounter counter;

    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.BACKOFF;
        if (jedis != null) {
            String value = jedis.rpop(DATA_KEY);
            if (StringUtils.isNotBlank(value)) {
                counter.incrementEventReceivedCount();
                Event event = JSON.parseObject(value, Event.class);
                this.getChannelProcessor().processEvent(event, false);
                result = READY;
            }
        }
        return result;
    }

    @Override
    public synchronized void start() {

        counter.start();
        super.start();
    }

    @Override
    public synchronized void stop() {
        if (jedis != null) {
            jedis.close();
        }
        if (counter != null) {
            counter.stop();
        }
        LOGGER.info("Redis Source {} stopped. Metrics: {}", getName(), counter);
        super.stop();

        parserRunner.stop();
    }

    @Override
    public void configure(Context context) {
        String host = context.getString("host", "127.0.0.1");
        Integer port = context.getInteger("port", 6379);
        if (jedis == null) {
            synchronized (this) {
                if (jedis == null) {
                    jedis = new Jedis(host, port);
                }
            }
        }
        if (counter == null) {
            counter = new SourceCounter(getName());
        }
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
