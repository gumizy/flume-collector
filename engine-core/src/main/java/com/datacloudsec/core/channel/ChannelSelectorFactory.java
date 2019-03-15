package com.datacloudsec.core.channel;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.config.conf.channel.ChannelSelectorType;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelSelector;
import com.datacloudsec.core.conf.Configurables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChannelSelectorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSelectorFactory.class);

    public static ChannelSelector create(List<Channel> channels, Map<String, String> config) {

        ChannelSelector selector = getSelectorForType(config.get(BasicConfigurationConstants.CONFIG_TYPE));

        selector.setChannels(channels);

        Context context = new Context();
        context.putAll(config);

        Configurables.configure(selector, context);
        return selector;
    }


    private static ChannelSelector getSelectorForType(String type) {
        if (type == null || type.trim().length() == 0) {
            return new ReplicatingChannelSelector();
        }

        String selectorClassName = type;
        ChannelSelectorType selectorType = ChannelSelectorType.OTHER;

        try {
            selectorType = ChannelSelectorType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("Selector type {} is a custom type", type);
        }

        if (!selectorType.equals(ChannelSelectorType.OTHER)) {
            selectorClassName = selectorType.getClassName();
        }

        ChannelSelector selector = null;

        try {
            @SuppressWarnings("unchecked") Class<? extends ChannelSelector> selectorClass = (Class<? extends ChannelSelector>) Class.forName(selectorClassName);
            selector = selectorClass.newInstance();
        } catch (Exception ex) {
            throw new CollectorEngineException("Unable to load selector type: " + type + ", class: " + selectorClassName, ex);
        }

        return selector;
    }

}
