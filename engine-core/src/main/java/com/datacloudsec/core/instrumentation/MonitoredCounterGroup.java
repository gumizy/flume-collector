package com.datacloudsec.core.instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class MonitoredCounterGroup {

    private static final Logger logger = LoggerFactory.getLogger(MonitoredCounterGroup.class);

    private static final String COUNTER_GROUP_START_TIME = "start.time";

    private static final String COUNTER_GROUP_STOP_TIME = "stop.time";

    private final Type type;
    private final String name;
    private final Map<String, AtomicLong> counterMap;

    private AtomicLong startTime;
    private AtomicLong stopTime;

    protected MonitoredCounterGroup(Type type, String name, String... attrs) {
        this.type = type;
        this.name = name;

        Map<String, AtomicLong> counterInitMap = new HashMap<String, AtomicLong>();

        for (String attribute : attrs) {
            counterInitMap.put(attribute, new AtomicLong(0L));
        }

        counterMap = Collections.unmodifiableMap(counterInitMap);

        startTime = new AtomicLong(0L);
        stopTime = new AtomicLong(0L);

    }

    public void start() {

        stopTime.set(0L);
        for (String counter : counterMap.keySet()) {
            counterMap.get(counter).set(0L);
        }
        startTime.set(System.currentTimeMillis());
        logger.info("Component type: " + type + ", name: " + name + " started");
    }

    public void stop() {
        stopTime.set(System.currentTimeMillis());
        logger.info("Component type: " + type + ", name: " + name + " stopped");
        final String typePrefix = type.name().toLowerCase(Locale.ENGLISH);
        logger.info("Shutdown Metric for type: " + type + ", " + "name: " + name + ". " + typePrefix + "." + COUNTER_GROUP_START_TIME + " == " + startTime);
        logger.info("Shutdown Metric for type: " + type + ", " + "name: " + name + ". " + typePrefix + "." + COUNTER_GROUP_STOP_TIME + " == " + stopTime);
        final List<String> mapKeys = new ArrayList<String>(counterMap.keySet());
        Collections.sort(mapKeys);
        for (final String counterMapKey : mapKeys) {
            final long counterMapValue = get(counterMapKey);
            logger.info("Shutdown Metric for type: " + type + ", " + "name: " + name + ". " + counterMapKey + " == " + counterMapValue);
        }
    }

    public long getStartTime() {
        return startTime.get();
    }

    public long getStopTime() {
        return stopTime.get();
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(type.name()).append(":");
        sb.append(name).append("{");
        boolean first = true;
        Iterator<String> counterIterator = counterMap.keySet().iterator();
        while (counterIterator.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String counterName = counterIterator.next();
            sb.append(counterName).append("=").append(get(counterName));
        }
        sb.append("}");

        return sb.toString();
    }

    protected long get(String counter) {
        return counterMap.get(counter).get();
    }

    protected void set(String counter, long value) {
        counterMap.get(counter).set(value);
    }

    protected long addAndGet(String counter, long delta) {
        return counterMap.get(counter).addAndGet(delta);
    }

    protected long increment(String counter) {
        return counterMap.get(counter).incrementAndGet();
    }

    public enum Type {
        SOURCE, CHANNEL_PROCESSOR, CHANNEL, SINK_PROCESSOR, SINK, INTERCEPTOR, SERIALIZER, OTHER
    }

    public String getType() {
        return type.name();
    }

}
