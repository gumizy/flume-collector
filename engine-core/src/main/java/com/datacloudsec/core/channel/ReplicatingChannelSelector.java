package com.datacloudsec.core.channel;

import com.datacloudsec.config.Context;
import com.datacloudsec.core.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * ReplicatingChannelSelector
 */
public class ReplicatingChannelSelector extends AbstractChannelSelector {

    List<Channel> requiredChannels = null;

    @Override
    public List<Channel> getRequiredChannels() {
        if (requiredChannels == null) {
            return getAllChannels();
        }
        return requiredChannels;
    }

    @Override
    public void configure(Context context) {
        requiredChannels = new ArrayList<Channel>(getAllChannels());
    }
}
