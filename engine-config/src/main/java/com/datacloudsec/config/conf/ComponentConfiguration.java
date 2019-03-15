package com.datacloudsec.config.conf;

import com.datacloudsec.config.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datacloudsec.config.conf.CollectorEngineConfigurationErrorType.ATTRS_MISSING;

public abstract class ComponentConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentConfiguration.class);
    protected String componentName;

    private String type;
    protected boolean configured;
    private boolean notFoundConfigClass;

    public boolean isNotFoundConfigClass() {
        return notFoundConfigClass;
    }

    public void setNotFoundConfigClass() {
        this.notFoundConfigClass = true;
    }

    protected ComponentConfiguration(String componentName) {
        this.componentName = componentName;
        this.type = null;
        configured = false;
    }

    public void configure(Context context) throws ConfigurationException {
        failIfConfigured();
        String confType = context.getString(BasicConfigurationConstants.CONFIG_TYPE);
        if (confType != null && !confType.isEmpty()) {
            this.type = confType;
        }
        // Type can be set by child class constructors, so check if it was.
        if (this.type == null || this.type.isEmpty()) {
            LOGGER.error(ATTRS_MISSING.getError());
            throw new ConfigurationException("Component has no type. Cannot configure. " + componentName);
        }
    }

    protected void failIfConfigured() throws ConfigurationException {
        if (configured) {
            throw new ConfigurationException("Already configured component." + componentName);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int indentCount) {
        StringBuilder indentSb = new StringBuilder();

        for (int i = 0; i < indentCount; i++) {
            indentSb.append(CollectorEngineConfiguration.INDENTSTEP);
        }

        String indent = indentSb.toString();
        StringBuilder sb = new StringBuilder(indent);

        sb.append("ComponentConfiguration[").append(componentName).append("]");
        sb.append(CollectorEngineConfiguration.NEWLINE).append(indent).append(CollectorEngineConfiguration.INDENTSTEP).append("CONFIG: ");
        sb.append(CollectorEngineConfiguration.NEWLINE).append(indent).append(CollectorEngineConfiguration.INDENTSTEP);

        return sb.toString();
    }

    public String getComponentName() {
        return componentName;
    }

    protected void setConfigured() {
        configured = true;
    }

    public enum ComponentType {
        OTHER(null), CONFIG_FILTER("ConfigFilter"), SOURCE("Source"), SINK("Sink"), SINK_PROCESSOR("SinkProcessor"), SINKGROUP("Sinkgroup"), CHANNEL("Channel"), CHANNELSELECTOR("ChannelSelector");

        private final String componentType;

        private ComponentType(String type) {
            componentType = type;
        }

        public String getComponentType() {
            return componentType;
        }
    }
}
