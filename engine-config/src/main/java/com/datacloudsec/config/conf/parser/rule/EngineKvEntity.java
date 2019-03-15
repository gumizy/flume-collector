package com.datacloudsec.config.conf.parser.rule;

import java.io.Serializable;

/**
 * EngineRuleEntity
 */
public class EngineKvEntity implements Serializable {

    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
