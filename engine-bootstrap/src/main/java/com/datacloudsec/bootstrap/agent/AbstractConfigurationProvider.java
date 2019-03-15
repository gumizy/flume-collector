package com.datacloudsec.bootstrap.agent;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.config.conf.CollectorEngineConfiguration;
import com.datacloudsec.config.conf.CollectorEngineConfiguration.AgentConfiguration;
import com.datacloudsec.config.conf.source.SourceType;
import com.datacloudsec.config.tools.PropertiesUtil;
import com.datacloudsec.core.*;
import com.datacloudsec.core.annotations.Disposable;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.channel.ChannelSelectorFactory;
import com.datacloudsec.core.channel.DefaultChannelFactory;
import com.datacloudsec.core.conf.BatchSizeSupported;
import com.datacloudsec.core.conf.Configurables;
import com.datacloudsec.core.conf.TransactionCapacitySupported;
import com.datacloudsec.core.sink.DefaultSinkFactory;
import com.datacloudsec.core.sink.DefaultSinkProcessor;
import com.datacloudsec.core.source.DefaultSourceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractConfigurationProvider implements ConfigurationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurationProvider.class);

    private final String agentName;
    private final SourceFactory sourceFactory;
    private final SinkFactory sinkFactory;
    private final ChannelFactory channelFactory;

    private final Map<Class<? extends Channel>, Map<String, Channel>> channelCache;

    public AbstractConfigurationProvider(String agentName) {
        super();
        this.agentName = agentName;
        this.sourceFactory = new DefaultSourceFactory();
        this.sinkFactory = new DefaultSinkFactory();
        this.channelFactory = new DefaultChannelFactory();

        channelCache = new HashMap<>();
    }

    protected abstract CollectorEngineConfiguration getCollectorEngineConfiguration();

    @Override
    public MaterializedConfiguration getConfiguration() {
        MaterializedConfiguration conf = new SimpleMaterializedConfiguration();
        CollectorEngineConfiguration fconfig = getCollectorEngineConfiguration();
        AgentConfiguration agentConf = fconfig.getAgentConfiguration();
        if (agentConf != null) {
            Map<String, ChannelComponent> channelComponentMap = Maps.newHashMap();
            Map<String, SourceRunner> sourceRunnerMap = Maps.newHashMap();
            Map<String, SinkRunner> sinkRunnerMap = Maps.newHashMap();
            try {
                loadChannels(agentConf, channelComponentMap);
                loadSources(agentConf, channelComponentMap, sourceRunnerMap);
                loadSinks(agentConf, channelComponentMap, sinkRunnerMap);
                Set<String> channelNames = new HashSet<>(channelComponentMap.keySet());
                for (String channelName : channelNames) {
                    ChannelComponent channelComponent = channelComponentMap.get(channelName);
                    if (channelComponent.components.isEmpty()) {
                        LOGGER.warn(String.format("Channel %s has no components connected" + " and has been removed.", channelName));
                        channelComponentMap.remove(channelName);
                        Map<String, Channel> nameChannelMap = channelCache.get(channelComponent.channel.getClass());
                        if (nameChannelMap != null) {
                            nameChannelMap.remove(channelName);
                        }
                    } else {
                        LOGGER.info(String.format("Channel %s connected to %s", channelName, channelComponent.components.toString()));
                        conf.addChannel(channelName, channelComponent.channel);
                    }
                }
                for (Map.Entry<String, SourceRunner> entry : sourceRunnerMap.entrySet()) {
                    conf.addSourceRunner(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<String, SinkRunner> entry : sinkRunnerMap.entrySet()) {
                    conf.addSinkRunner(entry.getKey(), entry.getValue());
                }
            } catch (InstantiationException ex) {
                LOGGER.error("Failed to instantiate component", ex);
            } finally {
                channelComponentMap.clear();
                sourceRunnerMap.clear();
                sinkRunnerMap.clear();
            }
        } else {
            LOGGER.warn("No configuration found for this host:{}", getAgentName());
        }
        return conf;
    }

    public String getAgentName() {
        return agentName;
    }

    private void loadChannels(AgentConfiguration agentConf, Map<String, ChannelComponent> channelComponentMap) {
        LOGGER.info("Creating channels");

        Set<String> channelNames = agentConf.getChannelSet();

        for (String chName : channelNames) {
            Context context = agentConf.getChannelContext().get(chName);
            if (context != null) {
                Channel channel = getOrCreateChannel(chName, context.getString(BasicConfigurationConstants.CONFIG_TYPE));
                try {
                    Configurables.configure(channel, context);
                    channelComponentMap.put(chName, new ChannelComponent(channel));
                    LOGGER.info("Created channel " + chName);
                } catch (Exception e) {
                    String msg = String.format("Channel %s has been removed due to an " + "error during configuration", chName);
                    LOGGER.error(msg, e);
                }
            }
        }

    }

    private Channel getOrCreateChannel(String name, String type) throws CollectorEngineException {
        Class<? extends Channel> channelClass = channelFactory.getClass(type);
        if (channelClass.isAnnotationPresent(Disposable.class)) {
            Channel channel = channelFactory.create(name, type);
            channel.setName(name);
            channel.setType(type);
            return channel;
        }
        Map<String, Channel> channelMap = channelCache.get(channelClass);
        if (channelMap == null) {
            channelMap = new HashMap<>();
            channelCache.put(channelClass, channelMap);
        }
        Channel channel = channelMap.get(name);
        if (channel == null) {
            channel = channelFactory.create(name, type);
            channel.setName(name);
            channelMap.put(name, channel);
        }
        return channel;
    }

    private void loadSources(AgentConfiguration agentConf, Map<String, ChannelComponent> channelComponentMap, Map<String, SourceRunner> sourceRunnerMap) throws InstantiationException {

        Set<String> sourceNames = agentConf.getSourceSet();

        Map<String, Context> sourceContexts = agentConf.getSourceContext();
        for (String sourceName : sourceNames) {
            Context context = sourceContexts.get(sourceName);
            if (context != null) {
                String sourceType = context.getString(BasicConfigurationConstants.CONFIG_TYPE);
                Source source = sourceFactory.create(sourceName, sourceType);
                try {
                    Configurables.configure(source, context);
                    String[] channelNames = context.getString(BasicConfigurationConstants.CONFIG_CHANNEL).split("\\s+");
                    List<Channel> sourceChannels = getSourceChannels(channelComponentMap, source, Arrays.asList(channelNames));
                    if (sourceChannels.isEmpty()) {
                        String msg = String.format("Source %s is not connected to a " + "channel", sourceName);
                        throw new IllegalStateException(msg);
                    }
                    Map<String, String> selectorConfig = context.getSubProperties(BasicConfigurationConstants.CONFIG_SOURCE_CHANNELSELECTOR_PREFIX);

                    ChannelSelector selector = ChannelSelectorFactory.create(sourceChannels, selectorConfig);

                    ChannelProcessor channelProcessor = new ChannelProcessor(selector);
                    // ntaflow和netflow
                    String netflowName = SourceType.NETFLOW.name();
                    String ntaflow = SourceType.NTAFLOW.name();
                    if (netflowName.equalsIgnoreCase(sourceType) || ntaflow.equalsIgnoreCase(sourceType)) {
                        builtFlowInterceptorContent(context);
                    } else {
                        // 其他采集方式
                        builtInterceptorContent(context);
                    }

                    Configurables.configure(channelProcessor, context);
                    source.setChannelProcessor(channelProcessor);
                    sourceRunnerMap.put(sourceName, SourceRunner.forSource(source));
                    for (Channel channel : sourceChannels) {
                        ChannelComponent channelComponent = Preconditions.checkNotNull(channelComponentMap.get(channel.getName()), String.format("Channel %s", channel.getName()));
                        channelComponent.components.add(sourceName);
                    }
                } catch (Exception e) {
                    String msg = String.format("Source %s has been removed due to an " + "error during configuration", sourceName);
                    LOGGER.error(msg, e);
                }
            }
        }
    }

    private List<Channel> getSourceChannels(Map<String, ChannelComponent> channelComponentMap, Source source, Collection<String> channelNames) throws InstantiationException {
        List<Channel> sourceChannels = new ArrayList<>();
        for (String chName : channelNames) {
            ChannelComponent channelComponent = channelComponentMap.get(chName);
            if (channelComponent != null) {
                checkSourceChannelCompatibility(source, channelComponent.channel);
                sourceChannels.add(channelComponent.channel);
            }
        }
        return sourceChannels;
    }

    private void checkSourceChannelCompatibility(Source source, Channel channel) throws InstantiationException {
        if (source instanceof BatchSizeSupported && channel instanceof TransactionCapacitySupported) {
            long transCap = ((TransactionCapacitySupported) channel).getTransactionCapacity();
            long batchSize = ((BatchSizeSupported) source).getBatchSize();
            if (transCap < batchSize) {
                String msg = String.format("Incompatible source and channel settings defined. " + "source's batch size is greater than the channels transaction capacity. " + "Source: %s, batch size = %d, channel %s, transaction capacity = %d", source.getName(), batchSize, channel.getName(), transCap);
                throw new InstantiationException(msg);
            }
        }
    }

    private void checkSinkChannelCompatibility(Sink sink, Channel channel) throws InstantiationException {
        if (sink instanceof BatchSizeSupported && channel instanceof TransactionCapacitySupported) {
            long transCap = ((TransactionCapacitySupported) channel).getTransactionCapacity();
            long batchSize = ((BatchSizeSupported) sink).getBatchSize();
            if (transCap < batchSize) {
                String msg = String.format("Incompatible sink and channel settings defined. " + "sink's batch size is greater than the channels transaction capacity. " + "Sink: %s, batch size = %d, channel %s, transaction capacity = %d", sink.getName(), batchSize, channel.getName(), transCap);
                throw new InstantiationException(msg);
            }
        }
    }

    private void loadSinks(AgentConfiguration agentConf, Map<String, ChannelComponent> channelComponentMap, Map<String, SinkRunner> sinkRunnerMap) throws InstantiationException {
        Set<String> sinkNames = agentConf.getSinkSet();
        Map<String, Sink> sinks = new HashMap<>();

        Map<String, Context> sinkContexts = agentConf.getSinkContext();
        for (String sinkName : sinkNames) {
            Context context = sinkContexts.get(sinkName);
            if (context != null) {
                Sink sink = sinkFactory.create(sinkName, context.getString(BasicConfigurationConstants.CONFIG_TYPE));
                try {
                    Configurables.configure(sink, context);
                    ChannelComponent channelComponent = channelComponentMap.get(context.getString(BasicConfigurationConstants.CONFIG_CHANNEL));
                    if (channelComponent == null) {
                        String msg = String.format("Sink %s is not connected to a " + "channel", sinkName);
                        throw new IllegalStateException(msg);
                    }
                    checkSinkChannelCompatibility(sink, channelComponent.channel);
                    sink.setChannel(channelComponent.channel);
                    sinks.put(sinkName, sink);
                    channelComponent.components.add(sinkName);
                } catch (Exception e) {
                    String msg = String.format("Sink %s has been removed due to an " + "error during configuration", sinkName);
                    LOGGER.error(msg, e);
                }
            }
        }

        loadSinkGroups(sinks, sinkRunnerMap);
    }

    private void loadSinkGroups(Map<String, Sink> sinks, Map<String, SinkRunner> sinkRunnerMap) {
        // add any unassigned sinks to solo collectors
        for (Map.Entry<String, Sink> entry : sinks.entrySet()) {
            try {
                SinkProcessor pr = new DefaultSinkProcessor();
                List<Sink> sinkMap = new ArrayList<>();
                sinkMap.add(entry.getValue());
                pr.setSinks(sinkMap);
                Configurables.configure(pr, new Context());
                sinkRunnerMap.put(entry.getKey(), new SinkRunner(pr));
            } catch (Exception e) {
                String msg = String.format("SinkGroup %s has been removed due to " + "an error during configuration", entry.getKey());
                LOGGER.error(msg, e);
            }
        }
    }

    private static class ChannelComponent {
        final Channel channel;
        final List<String> components;

        ChannelComponent(Channel channel) {
            this.channel = channel;
            components = Lists.newArrayList();
        }
    }

    protected Map<String, String> toMap(Properties properties) {
        Map<String, String> result = Maps.newHashMap();
        Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String name = (String) propertyNames.nextElement();
            String value = properties.getProperty(name);
            result.put(name, value);
        }
        return result;
    }

    protected void builtInterceptorContent(Context context) {
        Properties properties = new Properties();
        properties.setProperty("interceptors.twometa.type", "TWOMETA");
        properties.setProperty("interceptors.fivemeta.type", "FIVEMETA");

        properties.setProperty("interceptors.cus_depart.type", "DEPART");
        properties.setProperty("interceptors.cus_depart." + BasicConfigurationConstants.CUS_DEPART_ENRICH_FIELDS, "user_name,employee_name,employee_role,employee_department,department_name");
        properties.setProperty("interceptors.cus_depart." + BasicConfigurationConstants.CUS_DEPART_ENRICH_CONFIG_FIELD, "src_address");

        properties.setProperty("interceptors.src_geo.type", "GEO");
        properties.setProperty("interceptors.src_geo." + BasicConfigurationConstants.GEO_ENRICH_FIELDS, "src_country,src_country_code,src_province,src_city,src_lat,src_lng");
        properties.setProperty("interceptors.src_geo." + BasicConfigurationConstants.GEO_ENRICH_CONFIG_FIELD, "src_address");

        properties.setProperty("interceptors.dst_geo.type", "GEO");
        properties.setProperty("interceptors.dst_geo." + BasicConfigurationConstants.GEO_ENRICH_FIELDS, "dst_country,dst_country_code,dst_province,dst_city,dst_lat,dst_lng");
        properties.setProperty("interceptors.dst_geo." + BasicConfigurationConstants.GEO_ENRICH_CONFIG_FIELD, "dst_address");

        properties.setProperty("interceptors.src_asset.type", "ASSET");
        String assetProperty = "src_asset_id,src_asset_type_id,src_asset_org_id,";
        assetProperty += "src_asset_sys_id,src_asset_location_id";
        properties.setProperty("interceptors.src_asset." + BasicConfigurationConstants.ASSET_ENRICH_FIELDS, assetProperty);
        properties.setProperty("interceptors.src_asset." + BasicConfigurationConstants.ASSET_ENRICH_CONFIG_FIELD, "src_address");

        properties.setProperty("interceptors.dst_asset.type", "ASSET");
        assetProperty = "dst_asset_id,dst_asset_type_id,dst_asset_org_id,";
        assetProperty += "dst_asset_sys_id,dst_asset_location_id";
        properties.setProperty("interceptors.dst_asset." + BasicConfigurationConstants.ASSET_ENRICH_FIELDS, assetProperty);
        properties.setProperty("interceptors.dst_asset." + BasicConfigurationConstants.ASSET_ENRICH_CONFIG_FIELD, "dst_address");

        properties.setProperty("interceptors.malwaredns.type", "MALWAREIP");
        properties.setProperty("interceptors.malwaredns." + BasicConfigurationConstants.MALWAREIP_ENRICH_FIELDS, "maldns_class");
        properties.setProperty("interceptors.malwaredns." + BasicConfigurationConstants.MALWAREIP_REFLECT_ENRICH_FIELDS, "src_address");

        properties.setProperty("interceptors.malwareip.type", "MALWAREIP");
        properties.setProperty("interceptors.malwareip." + BasicConfigurationConstants.MALWAREIP_ENRICH_FIELDS, "malip_class");
        properties.setProperty("interceptors.malwareip." + BasicConfigurationConstants.MALWAREIP_REFLECT_ENRICH_FIELDS, "src_address");

        properties.setProperty("interceptors.malwareurl.type", "MALWAREURL");
        properties.setProperty("interceptors.malwareurl." + BasicConfigurationConstants.MALWAREURL_ENRICH_FIELDS, "malurl_class");
        properties.setProperty("interceptors.malwareurl." + BasicConfigurationConstants.MALWAREURL_REFLECT_ENRICH_FIELDS, "url");

        properties.setProperty("interceptors.malwaredomain.type", "MALWAREDOMAIN");
        properties.setProperty("interceptors.malwaredomain." + BasicConfigurationConstants.MALWAREDOMAIN_ENRICH_FIELDS, "maldomain_class");
        properties.setProperty("interceptors.malwaredomain." + BasicConfigurationConstants.MALWAREDOMAIN_REFLECT_ENRICH_FIELDS, "url");

        properties.setProperty("interceptors.malwarecode.type", "MALWARECODE");
        properties.setProperty("interceptors.malwarecode." + BasicConfigurationConstants.MALWARECODE_ENRICH_FIELDS, "malcode_name,malcode_class");
        properties.setProperty("interceptors.malwarecode." + BasicConfigurationConstants.MALWARECODE_REFLECT_ENRICH_FIELDS, "malcode_hash");

        properties.setProperty("interceptors.virus.type", "VIRUS");
        properties.setProperty("interceptors.virus." + BasicConfigurationConstants.VIRUS_ENRICH_FIELDS, "virus_name,virus_class");
        properties.setProperty("interceptors.virus." + BasicConfigurationConstants.VIRUS_REFLECT_ENRICH_FIELDS, "virus_hash");

        properties.setProperty("interceptors.vulnerability.type", "VULNERABILITY");
        properties.setProperty("interceptors.vulnerability." + BasicConfigurationConstants.VULNERABILITY_ENRICH_FIELDS, "vul_name,vul_class");
        properties.setProperty("interceptors.vulnerability." + BasicConfigurationConstants.VULNERABILITY_REFLECT_ENRICH_FIELDS, "vul_hash");

        properties.setProperty("interceptors.trojans.type", "TROJANS");
        properties.setProperty("interceptors.trojans." + BasicConfigurationConstants.TROJANS_ENRICH_FIELDS, "trojans_name,trojans_class");
        properties.setProperty("interceptors.trojans." + BasicConfigurationConstants.TROJANS_REFLECT_ENRICH_FIELDS, "trojans_hash");

        properties.setProperty("interceptors.fieldmap.type", "FIELDMAP");

        String interceptors = "twometa fivemeta src_geo dst_geo src_asset dst_asset malwaredns malwareip malwareurl malwaredomain malwarecode virus vulnerability trojans fieldmap cus_depart";
        properties.setProperty("interceptors", interceptors);
        context.putAll(PropertiesUtil.toMap(properties));
    }

    protected void builtFlowInterceptorContent(Context context) {
        Properties properties = new Properties();
        properties.setProperty("interceptors.twometa.type", "TWOMETA");
        properties.setProperty("interceptors.fivemeta.type", "FIVEMETA");

        properties.setProperty("interceptors", "twometa fivemeta");
        context.putAll(PropertiesUtil.toMap(properties));
    }

}