package com.datacloudsec.config.conf.intercepor;

import java.io.Serializable;

/**
 * AnalysisEventMappingBean
 *
 * @author gumizy 2017/9/7
 */
public class AnalysisEventMappingBean implements Serializable {

    private String from;

    private String to;

    private Object defaultValue;

    private Boolean toString;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getToString() {
        return toString;
    }

    public void setToString(Boolean toString) {
        this.toString = toString;
    }
}
