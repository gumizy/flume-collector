

package com.datacloudsec.core.lifecycle;

public interface LifecycleAware {

    void start();

    void stop();

    LifecycleState getLifecycleState();

}
