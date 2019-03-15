package com.datacloudsec.parser;

import com.datacloudsec.config.Event;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.config.event.EventConstants;
import com.datacloudsec.config.tools.UUIDUtil;
import com.datacloudsec.core.Parser;
import com.datacloudsec.core.ProcessException;
import com.datacloudsec.core.parser.AbstractParser;
import com.datacloudsec.core.parser.DefaultParserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.EVENT_INDEX_TYPE;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.NO_PARSER_TYPE;
import static com.datacloudsec.config.conf.parser.ParserType.parserTransfer;

/**
 * ParserChain
 */
public class ParserChain extends AbstractParser {

    private Logger logger = LoggerFactory.getLogger(ParserChain.class);

    private Charset charset = Charset.defaultCharset();

    private Integer hostId;

    private List<Parser> parsers;

    private Integer collectType;

    public void addParser(List<EventDecodeRule> parseRules, Integer hostId) {
        try {
            parsers = new ArrayList<>();
            if (parseRules == null) {
                return;
            }
            DefaultParserFactory parserFactory = new DefaultParserFactory();
            for (EventDecodeRule rule : parseRules) {
                Integer decodeType = rule.getDecodeType();
                // 由于历史原因，类型是数字，需要转义下，要不然改动太大
                String parserType = parserTransfer.get(decodeType);
                if (parserType == null) {
                    continue;
                }
                Builder build = parserFactory.create(rule.getRuleName(), parserType);
                if (build == null) {
                    continue;
                }
                Parser parser = build.build(rule);
                parser.setType(parserType);
                parser.setName(rule.getRuleName());
                parsers.add(parser);
            }

            this.hostId = hostId;
        } catch (Exception e) {
            logger.error("Event parser load for Collector with ip: {} failed, cause by : {}", hostId, e.getMessage());
        }
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    /**
     * 按host发送的内容进行解析
     *
     * @param sendHost   sendHost
     * @param logContent logContent
     * @return Event
     * @throws ProcessException ProcessException
     */
    @Override
    public List<Event> parse(String sendHost, String logContent) throws ProcessException {
        List<Event> events = null;
        // 有解析规则进行解析
        if (CollectionUtils.isNotEmpty(parsers)) {
            for (Parser parser : parsers) {
                events = parser.parse(sendHost, logContent);
                if (CollectionUtils.isNotEmpty(events)) {
                    break;
                }
            }
        }

        // return list
        if (CollectionUtils.isNotEmpty(events)) {
            return events;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Event parse NULL! Send host: " + sendHost + ", content: " + logContent);
            }
            final Event event = new Event();
            event.setEventUUID(UUIDUtil.generateShortUUID());
            event.setReceiveTime(System.currentTimeMillis());
            event.setIndexType(EVENT_INDEX_TYPE);
            event.setEventType(NO_PARSER_TYPE);
            event.setOriginalLog(logContent);
            event.putValue(EventConstants.DEVADDRESS, sendHost);
            event.valuesDefault();
            return Collections.singletonList(event);
        }
    }

    public List<Event> doParser(String sendHostIp, SourceMessage sourceMessage) {
        String message = sourceMessage.getSourceMessage(charset);
        if (message != null) {
            return parse(sendHostIp, message);
        }
        return null;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }
}
