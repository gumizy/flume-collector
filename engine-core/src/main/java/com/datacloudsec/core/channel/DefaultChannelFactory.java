package com.datacloudsec.core.channel;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.conf.channel.ChannelType;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class DefaultChannelFactory implements ChannelFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultChannelFactory.class);

    @Override
    public Channel create(String name, String type) throws CollectorEngineException {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(type, "type");
        logger.info("Creating instance of channel {} type {}", name, type);
        Class<? extends Channel> channelClass = getClass(type);
        try {
            return channelClass.newInstance();
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to create channel: " + name + ", type: " + type + ", class: " + channelClass.getName(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Channel> getClass(String type) throws CollectorEngineException {
        String channelClassName = type;
        ChannelType channelType = ChannelType.OTHER;
        try {
            channelType = ChannelType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            logger.debug("Channel type {} is a custom type", type);
        }
        if (!channelType.equals(ChannelType.OTHER)) {
            channelClassName = channelType.getClassName();
        }
        try {
            return (Class<? extends Channel>) Class.forName(channelClassName);
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to load channel type: " + type + ", class: " + channelClassName, ex);
        }
    }
}
