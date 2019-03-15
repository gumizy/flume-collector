package com.datacloudsec.core.parser;

import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.core.ParserException;
import com.datacloudsec.core.ParserProcessor;
import com.datacloudsec.core.lifecycle.LifecycleState;

import java.util.List;

/**
 * @Date 2019/1/12 18:27
 */
public abstract class AbstractParserProcessor implements ParserProcessor {

    private LifecycleState lifecycleState;

    private Integer collectType;

    public AbstractParserProcessor() {
        lifecycleState = LifecycleState.IDLE;
    }

    /**
     * 1-syslogudp
     * 2-jdbc
     * 3-ftp
     * 4-http
     * 5-kafka
     */
    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public abstract void put(SourceMessage message) throws ParserException;

    @Override
    public synchronized void start() {

        lifecycleState = LifecycleState.START;
    }

    @Override
    public synchronized void stop() {
        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public synchronized LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    public abstract void puts(List<SourceMessage> messages);

}
