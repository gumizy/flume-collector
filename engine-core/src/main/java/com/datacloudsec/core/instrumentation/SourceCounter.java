package com.datacloudsec.core.instrumentation;

import org.apache.commons.lang.ArrayUtils;

public class SourceCounter extends MonitoredCounterGroup implements SourceCounterMBean {

    private static final String COUNTER_EVENTS_RECEIVED = "src.events.received";
    private static final String COUNTER_EVENTS_DROP = "src.events.dropped";
    private static final String COUNTER_EVENTS_ACCEPTED = "src.events.accepted";

    private static final String COUNTER_APPEND_RECEIVED = "src.append.received";
    private static final String COUNTER_APPEND_ACCEPTED = "src.append.accepted";

    private static final String COUNTER_APPEND_BATCH_RECEIVED = "src.append-batch.received";
    private static final String COUNTER_APPEND_BATCH_ACCEPTED = "src.append-batch.accepted";

    private static final String COUNTER_OPEN_CONNECTION_COUNT = "src.open-connection.count";

    private static final String COUNTER_EVENT_READ_FAIL = "src.event.read.fail";

    private static final String COUNTER_GENERIC_PROCESSING_FAIL = "src.generic.processing.fail";

    private static final String COUNTER_CHANNEL_WRITE_FAIL = "src.channel.write.fail";

    private static final String[] ATTRIBUTES = {COUNTER_EVENTS_DROP, COUNTER_EVENTS_RECEIVED, COUNTER_EVENTS_ACCEPTED, COUNTER_APPEND_RECEIVED, COUNTER_APPEND_ACCEPTED, COUNTER_APPEND_BATCH_RECEIVED, COUNTER_APPEND_BATCH_ACCEPTED, COUNTER_OPEN_CONNECTION_COUNT, COUNTER_EVENT_READ_FAIL, COUNTER_CHANNEL_WRITE_FAIL, COUNTER_GENERIC_PROCESSING_FAIL};

    public SourceCounter(String name) {
        super(MonitoredCounterGroup.Type.SOURCE, name, ATTRIBUTES);
    }

    public SourceCounter(String name, String[] attributes) {
        super(Type.SOURCE, name, (String[]) ArrayUtils.addAll(attributes, ATTRIBUTES));
    }

    @Override
    public long getEventReceivedCount() {
        return get(COUNTER_EVENTS_RECEIVED);
    }

    public long incrementEventReceivedCount() {
        return increment(COUNTER_EVENTS_RECEIVED);
    }

    public long incrementEventDropCount() {
        return increment(COUNTER_EVENTS_DROP);
    }

    public long getEventDropCount() {
        return get(COUNTER_EVENTS_DROP);
    }

    public long addToEventReceivedCount(long delta) {
        return addAndGet(COUNTER_EVENTS_RECEIVED, delta);
    }

    @Override
    public long getEventAcceptedCount() {
        return get(COUNTER_EVENTS_ACCEPTED);
    }

    public long incrementEventAcceptedCount() {
        return increment(COUNTER_EVENTS_ACCEPTED);
    }

    public long addToEventAcceptedCount(long delta) {
        return addAndGet(COUNTER_EVENTS_ACCEPTED, delta);
    }

    @Override
    public long getAppendReceivedCount() {
        return get(COUNTER_APPEND_RECEIVED);
    }

    @Override
    public long getAppendAcceptedCount() {
        return get(COUNTER_APPEND_ACCEPTED);
    }

    @Override
    public long getAppendBatchReceivedCount() {
        return get(COUNTER_APPEND_BATCH_RECEIVED);
    }

    public long incrementAppendBatchReceivedCount() {
        return increment(COUNTER_APPEND_BATCH_RECEIVED);
    }

    @Override
    public long getAppendBatchAcceptedCount() {
        return get(COUNTER_APPEND_BATCH_ACCEPTED);
    }

    public long getOpenConnectionCount() {
        return get(COUNTER_OPEN_CONNECTION_COUNT);
    }

    public long incrementEventReadFail() {
        return increment(COUNTER_EVENT_READ_FAIL);
    }

    @Override
    public long getEventReadFail() {
        return get(COUNTER_EVENT_READ_FAIL);
    }

    public long incrementChannelWriteFail() {
        return increment(COUNTER_CHANNEL_WRITE_FAIL);
    }

    public long addEventChannelWriteFile(long delta) {
        return addAndGet(COUNTER_CHANNEL_WRITE_FAIL, delta);
    }

    @Override
    public long getChannelWriteFail() {
        return get(COUNTER_CHANNEL_WRITE_FAIL);
    }

    public long incrementGenericProcessingFail() {
        return increment(COUNTER_GENERIC_PROCESSING_FAIL);
    }

    @Override
    public long getGenericProcessingFail() {
        return get(COUNTER_GENERIC_PROCESSING_FAIL);
    }

}
