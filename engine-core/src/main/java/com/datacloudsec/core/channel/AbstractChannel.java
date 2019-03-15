package com.datacloudsec.core.channel;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelException;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.lifecycle.LifecycleAware;
import com.datacloudsec.core.lifecycle.LifecycleState;

import java.util.List;

public abstract class AbstractChannel implements Channel, LifecycleAware, Configurable {

    private String name;

    private String type;

    private LifecycleState lifecycleState;

    public AbstractChannel() {
        lifecycleState = LifecycleState.IDLE;
    }

    @Override
    public synchronized void setName(String name) {
        this.name = name;
    }

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

    @Override
    public synchronized String getName() {
        return name;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    protected abstract void doPut(Event event) throws InterruptedException;

    protected abstract void doPuts(List<Event> event) throws InterruptedException;

    protected abstract Event doTake() throws InterruptedException;

    protected abstract List<Event> doTakes() throws InterruptedException;

    @Override
    public void configure(Context context) {

    }

    @Override
    public void put(Event event) {
        try {
            doPut(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChannelException("put object error: ", e);
        }
    }

    @Override
    public void puts(List<Event> events) {
        try {
            doPuts(events);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChannelException("put object error: ", e);
        }
    }

    @Override
    public Event take() {
        try {
            return doTake();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public List<Event> takes() {
        try {
            return doTakes();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public String toString() {
        return this.getClass().getName() + "{name: " + name + "}";
    }

}
