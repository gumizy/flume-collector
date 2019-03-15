package com.datacloudsec.core.conf;

import java.io.Serializable;

/**
 * TrojansInfo
 */
public class TrojansInfo implements Serializable {
    private String trojansName;

    private String trojansHash;

    private String trojansClass;

    public TrojansInfo() {
        super();
    }

    public TrojansInfo(String trojansName, String trojansHash, String trojansClass) {
        super();
        this.trojansName = trojansName;
        this.trojansHash = trojansHash;
        this.trojansClass = trojansClass;
    }

    public String getTrojansName() {
        return trojansName;
    }

    public void setTrojansName(String trojansName) {
        this.trojansName = trojansName;
    }

    public String getTrojansHash() {
        return trojansHash;
    }

    public void setTrojansHash(String trojansHash) {
        this.trojansHash = trojansHash;
    }

    public String getTrojansClass() {
        return trojansClass;
    }

    public void setTrojansClass(String trojansClass) {
        this.trojansClass = trojansClass;
    }
}
