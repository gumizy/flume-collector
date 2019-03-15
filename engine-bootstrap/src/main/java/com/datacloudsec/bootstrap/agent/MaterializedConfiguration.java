

package com.datacloudsec.bootstrap.agent;

import com.datacloudsec.core.Channel;
import com.datacloudsec.core.SinkRunner;
import com.datacloudsec.core.SourceRunner;
import com.google.common.collect.ImmutableMap;

public interface MaterializedConfiguration {

    void addSourceRunner(String name, SourceRunner sourceRunner);

    void addSinkRunner(String name, SinkRunner sinkRunner);

    void addChannel(String name, Channel channel);

    ImmutableMap<String, SourceRunner> getSourceRunners();

    ImmutableMap<String, SinkRunner> getSinkRunners();

    ImmutableMap<String, Channel> getChannels();

}
