package com.datacloudsec.core;

import com.datacloudsec.core.lifecycle.LifecycleAware;

/**
 * @Date 2019/1/12 13:49
 */
public abstract class ParserRunner implements LifecycleAware {
    private ParserProcessor policy;

    public ParserProcessor getPolicy() {
        return policy;
    }

    public void setPolicy(ParserProcessor policy) {
        this.policy = policy;
    }
}
