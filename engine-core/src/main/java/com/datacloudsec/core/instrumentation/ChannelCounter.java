package com.datacloudsec.core.instrumentation;

import org.apache.commons.lang.ArrayUtils;

public class ChannelCounter extends MonitoredCounterGroup {

    private static final String COUNTER_CHANNEL_SIZE = "channel.current.size";

    private static final String COUNTER_EVENT_PUT_ATTEMPT = "channel.event.put.attempt";

    private static final String COUNTER_EVENT_TAKE_ATTEMPT = "channel.event.take.attempt";

    private static final String COUNTER_EVENT_PUT_SUCCESS = "channel.event.put.success";

    private static final String COUNTER_EVENT_TAKE_SUCCESS = "channel.event.take.success";

    private static final String COUNTER_CHANNEL_CAPACITY = "channel.capacity";

    private static final String[] ATTRIBUTES = {COUNTER_CHANNEL_SIZE, COUNTER_EVENT_PUT_ATTEMPT, COUNTER_EVENT_TAKE_ATTEMPT, COUNTER_EVENT_PUT_SUCCESS, COUNTER_EVENT_TAKE_SUCCESS, COUNTER_CHANNEL_CAPACITY};

    public ChannelCounter(String name) {
        super(MonitoredCounterGroup.Type.CHANNEL, name, ATTRIBUTES);
    }

    public ChannelCounter(String name, String[] attributes) {
        super(MonitoredCounterGroup.Type.CHANNEL, name, (String[]) ArrayUtils.addAll(attributes, ATTRIBUTES));
    }

    public long getChannelSize() {
        return get(COUNTER_CHANNEL_SIZE);
    }

    public void setChannelSize(long newSize) {
        set(COUNTER_CHANNEL_SIZE, newSize);
    }

    public long getEventPutAttemptCount() {
        return get(COUNTER_EVENT_PUT_ATTEMPT);
    }

    public long incrementEventPutAttemptCount() {
        return increment(COUNTER_EVENT_PUT_ATTEMPT);
    }

    public long getEventTakeAttemptCount() {
        return get(COUNTER_EVENT_TAKE_ATTEMPT);
    }

    public long incrementEventTakeAttemptCount() {
        return increment(COUNTER_EVENT_TAKE_ATTEMPT);
    }

    public long getEventPutSuccessCount() {
        return get(COUNTER_EVENT_PUT_SUCCESS);
    }

    public long addToEventPutSuccessCount(long delta) {
        return addAndGet(COUNTER_EVENT_PUT_SUCCESS, delta);
    }

    public long getEventTakeSuccessCount() {
        return get(COUNTER_EVENT_TAKE_SUCCESS);
    }

    public long addToEventTakeSuccessCount(long delta) {
        return addAndGet(COUNTER_EVENT_TAKE_SUCCESS, delta);
    }

    public void setChannelCapacity(long capacity) {
        set(COUNTER_CHANNEL_CAPACITY, capacity);
    }

    public long getChannelCapacity() {
        return get(COUNTER_CHANNEL_CAPACITY);
    }

    public double getChannelFillPercentage() {
        long capacity = getChannelCapacity();
        if (capacity != 0L) {
            return (getChannelSize() / (double) capacity) * 100;
        }
        return Double.MAX_VALUE;
    }

}
