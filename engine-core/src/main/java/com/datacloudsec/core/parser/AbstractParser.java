package com.datacloudsec.core.parser;

import com.datacloudsec.core.Parser;
import com.datacloudsec.core.lifecycle.LifecycleState;

/**
 * @Date 2019/1/12 15:51
 */
public abstract class AbstractParser implements Parser {

    private String name;

    private String type;

    private LifecycleState lifecycleState;

    public AbstractParser() {
        lifecycleState = LifecycleState.IDLE;
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
    public synchronized void setName(String name) {
        this.name = name;
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

    public String toString() {
        return this.getClass().getName() + "{name:" + name + ",state:" + lifecycleState + "}";
    }

}
