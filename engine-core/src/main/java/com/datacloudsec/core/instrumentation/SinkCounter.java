package com.datacloudsec.core.instrumentation;

import com.datacloudsec.core.ChannelException;
import org.apache.commons.lang.ArrayUtils;

public class SinkCounter extends MonitoredCounterGroup {

    private static final String COUNTER_CONNECTION_CREATED = "sink.connection.creation.count";

    private static final String COUNTER_CONNECTION_CLOSED = "sink.connection.closed.count";

    private static final String COUNTER_CONNECTION_FAILED = "sink.connection.failed.count";

    private static final String COUNTER_BATCH_EMPTY = "sink.batch.empty";

    private static final String COUNTER_BATCH_UNDERFLOW = "sink.batch.underflow";

    private static final String COUNTER_BATCH_COMPLETE = "sink.batch.complete";

    private static final String COUNTER_EVENT_DRAIN_ATTEMPT = "sink.event.drain.attempt";

    private static final String COUNTER_EVENT_DRAIN_SUCCESS = "sink.event.drain.sucess";

    private static final String COUNTER_EVENT_WRITE_FAIL = "sink.event.write.fail";

    private static final String COUNTER_CHANNEL_READ_FAIL = "sink.channel.read.fail";

    private static final String[] ATTRIBUTES = {COUNTER_CONNECTION_CREATED, COUNTER_CONNECTION_CLOSED, COUNTER_CONNECTION_FAILED, COUNTER_BATCH_EMPTY, COUNTER_BATCH_UNDERFLOW, COUNTER_BATCH_COMPLETE, COUNTER_EVENT_DRAIN_ATTEMPT, COUNTER_EVENT_DRAIN_SUCCESS, COUNTER_EVENT_WRITE_FAIL, COUNTER_CHANNEL_READ_FAIL};

    public SinkCounter(String name) {
        super(MonitoredCounterGroup.Type.SINK, name, ATTRIBUTES);
    }

    public SinkCounter(String name, String[] attributes) {
        super(MonitoredCounterGroup.Type.SINK, name, (String[]) ArrayUtils.addAll(attributes, ATTRIBUTES));
    }

    public long getConnectionCreatedCount() {
        return get(COUNTER_CONNECTION_CREATED);
    }

    public long incrementConnectionCreatedCount() {
        return increment(COUNTER_CONNECTION_CREATED);
    }

    public long getConnectionClosedCount() {
        return get(COUNTER_CONNECTION_CLOSED);
    }

    public long incrementConnectionClosedCount() {
        return increment(COUNTER_CONNECTION_CLOSED);
    }

    public long getConnectionFailedCount() {
        return get(COUNTER_CONNECTION_FAILED);
    }

    public long incrementConnectionFailedCount() {
        return increment(COUNTER_CONNECTION_FAILED);
    }

    public long getBatchEmptyCount() {
        return get(COUNTER_BATCH_EMPTY);
    }

    public long incrementBatchEmptyCount() {
        return increment(COUNTER_BATCH_EMPTY);
    }

    public long getBatchUnderflowCount() {
        return get(COUNTER_BATCH_UNDERFLOW);
    }

    public long incrementBatchUnderflowCount() {
        return increment(COUNTER_BATCH_UNDERFLOW);
    }

    public long getBatchCompleteCount() {
        return get(COUNTER_BATCH_COMPLETE);
    }

    public long incrementBatchCompleteCount() {
        return increment(COUNTER_BATCH_COMPLETE);
    }

    public long getEventDrainAttemptCount() {
        return get(COUNTER_EVENT_DRAIN_ATTEMPT);
    }

    public long incrementEventDrainAttemptCount() {
        return increment(COUNTER_EVENT_DRAIN_ATTEMPT);
    }

    public long addToEventDrainAttemptCount(long delta) {
        return addAndGet(COUNTER_EVENT_DRAIN_ATTEMPT, delta);
    }

    public long getEventDrainSuccessCount() {
        return get(COUNTER_EVENT_DRAIN_SUCCESS);
    }

    public long incrementEventDrainSuccessCount() {
        return increment(COUNTER_EVENT_DRAIN_SUCCESS);
    }

    public long addToEventDrainSuccessCount(long delta) {
        return addAndGet(COUNTER_EVENT_DRAIN_SUCCESS, delta);
    }

    public long incrementEventWriteFail() {
        return increment(COUNTER_EVENT_WRITE_FAIL);
    }

    public long getEventWriteFail() {
        return get(COUNTER_EVENT_WRITE_FAIL);
    }

    public long incrementChannelReadFail() {
        return increment(COUNTER_CHANNEL_READ_FAIL);
    }

    public long getChannelReadFail() {
        return get(COUNTER_CHANNEL_READ_FAIL);
    }

    public long incrementEventWriteOrChannelFail(Throwable t) {
        if (t instanceof ChannelException) {
            return incrementChannelReadFail();
        }
        return incrementEventWriteFail();
    }

}
