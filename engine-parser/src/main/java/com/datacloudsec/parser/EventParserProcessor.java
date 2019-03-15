package com.datacloudsec.parser;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.config.tools.UUIDUtil;
import com.datacloudsec.core.ParserException;
import com.datacloudsec.core.Source;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.parser.AbstractParserProcessor;
import com.datacloudsec.parser.cache.PraserProcessorCache;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.EVENT_INDEX_TYPE;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.NO_PARSER_TYPE;
import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;

/**
 * @Date 2019/1/12 15:50
 */
public class EventParserProcessor extends AbstractParserProcessor implements Configurable {
    private static final Logger logger = LoggerFactory.getLogger(EventParserProcessor.class);

    protected LinkedBlockingQueue<SourceMessage> queue;

    private static final int DEFAULT_INITIAL_SIZE = 640000;

    private Source source;

    private boolean normalization;
    private boolean forward;
    private Charset forwardCharset = StandardCharsets.UTF_8;

    public EventParserProcessor(Source source) {
        super();
        this.source = source;
    }

    @Override
    public synchronized void start() {
        queue = new LinkedBlockingQueue<>(DEFAULT_INITIAL_SIZE);
        super.start();
    }

    @Override
    public synchronized void stop() {
        queue.clear();
        super.stop();
    }

    @Override
    public void puts(List<SourceMessage> messages) {
        for (SourceMessage message : messages) {
            put(message);
        }
    }

    @Override
    public void put(SourceMessage message) throws ParserException {
        try {
            this.queue.put(message);
        } catch (InterruptedException e) {
            logger.info("Parser process Interrupted");
        }
    }

    @Override
    public void configure(Context context) {
        normalization = context.getBoolean(NORMALIZATION, true);
        forward = context.getBoolean(FORWARD, false);
        forwardCharset = Charset.forName(context.getString(FORWARD_CHARSET, "UTF-8"));
    }

    @Override
    public Status process() {
        try {
            // 从QUEUE中取数据（阻塞模式，直到取到数据为止）
            SourceMessage sourceMessage = queue.take();

            List<Event> events = null;
            String sendHostIp = sourceMessage.getSendHostIp();
            String parserIdentification = sourceMessage.getParserIdentification();
            String message = null;
            // 来源是否是转发模式发过来的数据
            if (forward) {
                message = sourceMessage.getSourceMessage(forwardCharset);
                Matcher matcher = FORWARD_PATTERN.matcher(message);
                if (matcher.find()) {
                    String ip = matcher.group(1);
                    String msg = matcher.group(2);
                    if (ip != null && msg != null) {
                        sendHostIp = ip;
                        message = msg;
                    }
                }
            }
            // 是否解析
            if (normalization && sendHostIp != null && parserIdentification != null) {
                final ParserChain parserChain = PraserProcessorCache.getParserChain(parserIdentification);
                if (parserChain != null) {
                    events = parserChain.doParser(sendHostIp, sourceMessage);
                }
            }

            if (CollectionUtils.isEmpty(events)) {
                if (message == null) {
                    message = sourceMessage.getSourceMessage(forwardCharset);
                }
                Event event = new Event();
                event.setEventUUID(UUIDUtil.generateShortUUID());
                event.setReceiveTime(System.currentTimeMillis());
                event.setIndexType(EVENT_INDEX_TYPE);
                event.setEventType(NO_PARSER_TYPE);
                event.setCollectorAddress(sendHostIp);
                event.setOriginalLog(message);
                processEvents(event, forward);
            } else {
                for (Event event : events) {
                    processEvents(event, forward);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Parser log interrupted");
            return Status.BACKOFF;
        } catch (Throwable e) {
            logger.error("Parser log error: {}", e);
            return Status.BACKOFF;
        }
        return Status.READY;
    }

    private void processEvents(Event event, boolean forward) {
        // set default values
        event.valuesDefault();
        // channel process
        ChannelProcessor processor = source.getChannelProcessor();
        if (processor != null) {
            processor.processEvent(event, forward);
        }
    }
}
