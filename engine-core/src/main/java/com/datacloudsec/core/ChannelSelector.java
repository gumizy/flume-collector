package com.datacloudsec.core;

import com.datacloudsec.core.conf.Configurable;

import java.util.List;

public interface ChannelSelector extends OriginalComponent, Configurable {

    void setChannels(List<Channel> channels);

    List<Channel> getRequiredChannels();

    List<Channel> getAllChannels();

}
