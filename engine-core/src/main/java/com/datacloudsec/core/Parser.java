package com.datacloudsec.core;

import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.core.lifecycle.LifecycleAware;

import java.util.List;

/**
 * Parser
 */
public interface Parser<T> extends LifecycleAware, OriginalComponent {

    List<Event> parse(String sendHost, String logContent) throws ProcessException;

    interface Builder {

        Parser build(EventDecodeRule parseRule);
    }
}
