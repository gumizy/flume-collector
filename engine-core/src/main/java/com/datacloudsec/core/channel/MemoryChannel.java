package com.datacloudsec.core.channel;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.core.instrumentation.ChannelCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MemoryChannel
 */
public class MemoryChannel extends AbstractChannel {

    private static Logger logger = LoggerFactory.getLogger(MemoryChannel.class);

    private static final Integer defaultCapacity = 1000000;

    private Integer capacity = defaultCapacity;

    private ReentrantReadWriteLock queueLock = new ReentrantReadWriteLock();

    private LinkedList<Event> queue;

    private ChannelCounter counter;

    public MemoryChannel() {
        super();
    }

    @Override
    public void configure(Context context) {
        try {
            capacity = context.getInteger("capacity", defaultCapacity);
        } catch (NumberFormatException e) {
            capacity = defaultCapacity;
            logger.warn("Invalid capacity specified, initializing channel to " + "default capacity of {}", defaultCapacity);
        }

        if (capacity <= 0) {
            capacity = defaultCapacity;
            logger.warn("Invalid capacity specified, initializing channel to " + "default capacity of {}", defaultCapacity);
        }

        try {
            queueLock.writeLock().lock();
            queue = new LinkedList<>();
        } finally {
            queueLock.writeLock().unlock();
        }
        counter = new ChannelCounter(getName());
    }

    @Override
    protected void doPut(Event event) {
        try {
            queueLock.writeLock().lock();
            if (queue.size() < capacity) {
                queue.addLast(event);
            } else {
                if (logger.isDebugEnabled())
                    logger.info("Put queue for MemoryChannel of capacity " + queue.size() + " full");
            }
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    protected void doPuts(List<Event> events) {
        if (events == null || events.size() < 1) return;
        try {
            queueLock.writeLock().lock();
            if (queue.size() < capacity) {
                queue.addAll(events);
            } else {
                if (logger.isDebugEnabled())
                    logger.info("Put queue for MemoryChannel of capacity " + queue.size() + " full");
            }
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    protected Event doTake() throws InterruptedException {
        try {
            queueLock.readLock().lock();
            if (queue.isEmpty()) {
                return null;
            }
            return queue.removeFirst();
        } finally {
            queueLock.readLock().unlock();
        }
    }

    @Override
    protected List<Event> doTakes() throws InterruptedException {
        List<Event> result = new LinkedList<>();
        try {
            queueLock.readLock().lock();
            result.addAll(queue);
            queue.clear();
        } finally {
            queueLock.readLock().unlock();
        }
        return result;
    }

    @Override
    public synchronized void start() {
        super.start();
        counter.start();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        counter.stop();
    }

    @Override
    public ChannelCounter getChannelCounter() {
        counter.setChannelCapacity(queue.size());
        return counter;
    }
}
