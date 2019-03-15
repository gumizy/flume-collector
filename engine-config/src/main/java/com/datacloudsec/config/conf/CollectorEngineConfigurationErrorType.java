
package com.datacloudsec.config.conf;

public enum CollectorEngineConfigurationErrorType {
  OTHER(null),
  AGENT_CONFIGURATION_INVALID("Agent configuration is invalid."),
  PROPERTY_NAME_NULL("Property needs a name."),
  PROPERTY_VALUE_NULL("Property value missing."),
  AGENT_NULL_ATTR("Null names and values not supported."),
  AGENT_ATTR_KEY_NAME_ERROR("Agent attr key name is error,should be source channel sink"),
  AGENT_ATTR_KEY_MISSING("Agent attr key is required."),
  AGENT_ATTR_VALUE_MISSING("Agent attr value is required."),
  CONFIGURATION_KEY_ERROR("Configuration Key is invalid."),
  DUPLICATE_PROPERTY("Property already configured."),
  INVALID_PROPERTY("No such property."),
  PROPERTY_PART_OF_ANOTHER_GROUP("This property is part of another group."),
  ATTRS_MISSING("Required attributes missing."),
  ILLEGAL_PROPERTY_NAME("This attribute name is invalid."),
  DEFAULT_VALUE_ASSIGNED(
      "Value in configuration is invalid for this key, assigned default value."),
  CONFIG_ERROR("Configuration of component failed.");
  private final String error;

  private CollectorEngineConfigurationErrorType(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }
}
