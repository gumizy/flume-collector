

package com.datacloudsec.core;

import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.lifecycle.LifecycleAware;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Source extends LifecycleAware, OriginalComponent {

    void setChannelProcessor(ChannelProcessor channelProcessor);

    ChannelProcessor getChannelProcessor();

    SourceCounter getSourceCounter();

}
