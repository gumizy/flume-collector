package com.datacloudsec.core;

import com.datacloudsec.core.lifecycle.LifecycleAware;
import com.datacloudsec.core.source.EventDrivenSourceRunner;
import com.datacloudsec.core.source.PollableSourceRunner;

public abstract class SourceRunner implements LifecycleAware {

    private Source source;

    public static SourceRunner forSource(Source source) {
        SourceRunner runner;

        if (source instanceof PollableSource) {
            runner = new PollableSourceRunner();
            runner.setSource(source);
        } else if (source instanceof EventDrivenSource) {
            runner = new EventDrivenSourceRunner();
            runner.setSource(source);
        } else {
            throw new IllegalArgumentException("No known runner type for source " + source);
        }

        return runner;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

}
