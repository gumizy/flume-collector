package com.datacloudsec.config.conf;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.channel.ChannelType;
import com.datacloudsec.config.conf.sink.SinkType;
import com.datacloudsec.config.conf.source.SourceType;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.*;
import static com.datacloudsec.config.conf.CollectorEngineConfigurationErrorType.*;
import static com.datacloudsec.config.conf.channel.ChannelType.MEMORY;

public class CollectorEngineConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorEngineConfiguration.class);

    public static final String NEWLINE = System.getProperty("line.separator", "\n");
    public static final String INDENTSTEP = "  ";
    private static AgentConfiguration agentConfiguration;

    public  AgentConfiguration getAgentConfiguration() {
        return agentConfiguration;
    }

    public CollectorEngineConfiguration(Map<String, String> properties) {
        if (agentConfiguration != null) {
            agentConfiguration.clear();
        }
        for (Entry<String, String> entry : properties.entrySet()) {
            if (!addRawProperty(entry.getKey(), entry.getValue())) {
                LOGGER.warn("Configuration property ignored: {} = {}", entry.getKey(), entry.getValue());
            }
        }
        validateConfiguration();
    }

    private void validateConfiguration() {
        String agentName = AGENTN_NAME;
        AgentConfiguration aconf = agentConfiguration;
        if (!aconf.isValid()) {
            LOGGER.warn("Agent configuration invalid for agent '{}'. It will be removed.", agentName);
            LOGGER.error(AGENT_CONFIGURATION_INVALID.getError());
        }

        LOGGER.info("Post-validation agent configuration contains configuration for agents: {}", aconf);
    }

    private boolean addRawProperty(String rawName, String rawValue) {
        if (rawName == null || rawValue == null) {
            LOGGER.error(AGENT_NULL_ATTR.getError());
            return false;
        }
        String name = rawName.trim();
        String value = rawValue.trim();

        if (value.isEmpty()) {
            LOGGER.error(PROPERTY_VALUE_NULL.getError());
            return false;
        }

        if (agentConfiguration == null) {
            agentConfiguration = new AgentConfiguration();
        }
        AgentConfiguration aconf = agentConfiguration;

        return aconf.addProperty(name, value);
    }

    public static class AgentConfiguration {
        private Map<String, Context> sourceContextMap;
        private Map<String, Context> sinkContextMap;
        private Map<String, Context> channelContextMap;

        private Set<String> sinkSet;
        private Set<String> sourceSet;
        private Set<String> channelSet;

        private AgentConfiguration() {

            sourceContextMap = new HashMap<>();
            sinkContextMap = new HashMap<>();
            channelContextMap = new HashMap<>();
        }

        public Map<String, Context> getSourceContext() {
            return sourceContextMap;
        }

        public Map<String, Context> getSinkContext() {
            return sinkContextMap;
        }

        public Map<String, Context> getChannelContext() {
            return channelContextMap;
        }

        public Set<String> getSinkSet() {
            return sinkSet;
        }

        public Set<String> getSourceSet() {
            return sourceSet;
        }

        public Set<String> getChannelSet() {
            return channelSet;
        }

        private boolean isValid() {
            LOGGER.info("Initial configuration: {}", getPrevalidationConfig());

            validateChannels();
            validateSources();
            validateSinks();

            LOGGER.info("Valid configuration: {}", getPostvalidationConfig());
            return true;
        }

        private void validateChannels() {
            if (channelContextMap.isEmpty()) {
                LOGGER.debug("Application.properties no valid channel,we will created default memory channel context for collector agent ");
                Context context = new Context();
                channelContextMap.put(DEFAULT_CHANNEL_NAME, context);
                context.put(CONFIG_TYPE, MEMORY.name());
            }

            Set<String> channels = new HashSet<>(channelContextMap.keySet());
            Preconditions.checkNotNull(channels.isEmpty(), "Agent configuration  does not contain any valid channels. Marking it as invalid.");
            for (String channelName : channels) {
                Context srcContext = channelContextMap.get(channelName);
                ChannelType srcType = getKnownChannel(srcContext.getString(BasicConfigurationConstants.CONFIG_TYPE));
                if (srcType == null) {
                    channelContextMap.remove(channelName);
                }
                channelSet = channelContextMap.keySet();
            }
        }

        private void validateSources() {
            Set<String> sources = new HashSet<>(sourceContextMap.keySet());
            Preconditions.checkNotNull(!sources.isEmpty(), "Agent configuration  does not contain any valid sources. Marking it as invalid.");
            for (String sourceName : sources) {
                Context srcContext = sourceContextMap.get(sourceName);
                String channel = srcContext.getString(CONFIG_CHANNEL);
                if (channel == null) {
                    LOGGER.warn("Source {} not match channel ,will set default memory channel ", sourceName);
                    srcContext.put(CONFIG_CHANNEL, DEFAULT_CHANNEL_NAME);
                }
                SourceType srcType = getKnownSource(srcContext.getString(CONFIG_TYPE));
                if (srcType == null) {
                    sourceContextMap.remove(sourceName);
                    LOGGER.error("Source {} is an invalid configuration ", sourceName);
                }
                sourceSet = sourceContextMap.keySet();
            }
        }

        private void validateSinks() {
            Set<String> sinks = new HashSet<>(sinkContextMap.keySet());
            Preconditions.checkNotNull(sinks.isEmpty(), "Agent configuration  does not contain any valid sinks. Marking it as invalid.");
            for (String sinkName : sinks) {
                Context srcContext = sinkContextMap.get(sinkName);
                String channel = srcContext.getString(CONFIG_CHANNEL);
                if (channel == null) {
                    LOGGER.warn("Sink {} not match channel ,will set default memory channel ", sinkName);
                    srcContext.put(CONFIG_CHANNEL, DEFAULT_CHANNEL_NAME);
                }
                SinkType srcType = getKnownSink(srcContext.getString(BasicConfigurationConstants.CONFIG_TYPE));
                if (srcType == null) {
                    sinkContextMap.remove(sinkName);
                    LOGGER.warn("Sink {} is an invalid configuration ", sinkName);
                }
                sinkSet = sinkContextMap.keySet();
            }
        }

        private ChannelType getKnownChannel(String type) {
            return getKnownComponent(type, ChannelType.values());
        }

        private SinkType getKnownSink(String type) {
            return getKnownComponent(type, SinkType.values());
        }

        private SourceType getKnownSource(String type) {
            return getKnownComponent(type, SourceType.values());
        }

        private <T extends ComponentWithClassName> T getKnownComponent(String type, T[] values) {
            for (T value : values) {
                if (value.toString().equalsIgnoreCase(type)) return value;
                String src = value.getClassName();
                if (src != null && src.equalsIgnoreCase(type)) return value;
            }
            return null;
        }

        public String getPrevalidationConfig() {
            StringBuilder sb = new StringBuilder("AgentConfiguration[");
            sb.append(AGENTN_NAME).append("]").append(NEWLINE);
            sb.append("SOURCES: ").append(sourceContextMap).append(NEWLINE);
            sb.append("CHANNELS: ").append(channelContextMap).append(NEWLINE);
            sb.append("SINKS: ").append(sinkContextMap).append(NEWLINE);

            return sb.toString();
        }

        public String getPostvalidationConfig() {
            StringBuilder sb = new StringBuilder("AgentConfiguration created without Configuration stubs for which only basic syntactical validation was performed[");
            sb.append(AGENTN_NAME).append("]").append(NEWLINE);
            if (!sourceContextMap.isEmpty() || !sinkContextMap.isEmpty() || !channelContextMap.isEmpty()) {
                if (!sourceContextMap.isEmpty()) {
                    sb.append("SOURCES: ").append(sourceContextMap).append(NEWLINE);
                }

                if (!channelContextMap.isEmpty()) {
                    sb.append("CHANNELS: ").append(channelContextMap).append(NEWLINE);
                }

                if (!sinkContextMap.isEmpty()) {
                    sb.append("SINKS: ").append(sinkContextMap).append(NEWLINE);
                }
            }

            return sb.toString();
        }

        private boolean addProperty(String key, String value) {
            if (addAsSourceConfig(key, value) || addAsChannelValue(key, value) || addAsSinkConfig(key, value)) {
                return true;
            }
            LOGGER.error("Invalid property specified: {}", INVALID_PROPERTY.getError());
            return false;
        }

        private boolean addAsSinkConfig(String key, String value) {
            return addComponentConfig(key, value, CONFIG_SINK_PREFIX, sinkContextMap);
        }

        private boolean addAsChannelValue(String key, String value) {
            return addComponentConfig(key, value, CONFIG_CHANNEL_PREFIX, channelContextMap);
        }

        private boolean addAsSourceConfig(String key, String value) {
            return addComponentConfig(key, value, CONFIG_SOURCE_PREFIX, sourceContextMap);
        }

        private boolean addComponentConfig(String key, String value, String configPrefix, Map<String, Context> contextMap) {
            ComponentNameAndConfigKey parsed = parseConfigKey(key, configPrefix);
            if (parsed != null) {
                String name = parsed.getComponentName().trim();
                LOGGER.info("Processing addComponentConfig:{}={}", name,value);
                Context context = contextMap.get(name);

                if (context == null) {
                    LOGGER.debug("Created context for {}: {}", name, parsed.getConfigKey());
                    context = new Context();
                    contextMap.put(name, context);
                }

                context.put(parsed.getConfigKey(), value);
                return true;
            }

            return false;
        }

        private ComponentNameAndConfigKey parseConfigKey(String key, String prefix) {
            if (!key.startsWith(prefix)) {
                return null;
            }

            // <prefix><component-name>.<config-key>
            int index = key.indexOf('.', prefix.length() + 1);

            if (index == -1) {
                return null;
            }

            String name = key.substring(prefix.length(), index);
            String configKey = key.substring(prefix.length() + name.length() + 1);

            // name and config key must be non-empty
            if (name.isEmpty() || configKey.isEmpty()) {
                return null;
            }

            return new ComponentNameAndConfigKey(name, configKey);
        }

        public void clear() {
            sourceContextMap.clear();
            channelContextMap.clear();
            sinkContextMap.clear();

            sourceSet.clear();
            channelSet.clear();
            sinkSet.clear();
        }
    }

    public static class ComponentNameAndConfigKey {

        private final String componentName;
        private final String configKey;

        private ComponentNameAndConfigKey(String name, String configKey) {
            this.componentName = name;
            this.configKey = configKey;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getConfigKey() {
            return configKey;
        }
    }
}
