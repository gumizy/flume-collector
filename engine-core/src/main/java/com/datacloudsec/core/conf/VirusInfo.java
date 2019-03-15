package com.datacloudsec.core.conf;

import java.io.Serializable;

/**
 * VirusInfo
 */
public class VirusInfo implements Serializable {
    private String virusName;

    private String virusHash;

    private String virusClass;

    public VirusInfo() {
        super();
    }

    public VirusInfo(String virusName, String virusHash) {
        super();
        this.virusName = virusName;
        this.virusHash = virusHash;
    }

    public VirusInfo(String virusName, String virusHash, String virusClass) {
        super();
        this.virusName = virusName;
        this.virusHash = virusHash;
        this.virusClass = virusClass;
    }

    public String getVirusName() {
        return virusName;
    }

    public void setVirusName(String virusName) {
        this.virusName = virusName;
    }

    public String getVirusHash() {
        return virusHash;
    }

    public void setVirusHash(String virusHash) {
        this.virusHash = virusHash;
    }

    public String getVirusClass() {
        return virusClass;
    }

    public void setVirusClass(String virusClass) {
        this.virusClass = virusClass;
    }
}
