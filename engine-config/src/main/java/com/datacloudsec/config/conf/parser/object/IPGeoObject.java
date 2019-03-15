package com.datacloudsec.config.conf.parser.object;


import java.io.Serializable;
import java.util.Date;

/**
 * IPGeoObject
 *
 * @author gumizy 2017/11/20
 */
public class IPGeoObject implements Serializable {


    private Integer id;

    private Long ipStart;

    private Long ipEnd;

    private String ipStartV6;

    private String ipEndV6;

    private Integer ipVersion;

    private String ipStartStr;

    private String ipEndStr;

    private String adminName;

    private Integer geoId;

    private Date insertTime;

    private String extraInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIpStart() {
        return ipStart;
    }

    public void setIpStart(Long ipStart) {
        this.ipStart = ipStart;
    }

    public Long getIpEnd() {
        return ipEnd;
    }

    public void setIpEnd(Long ipEnd) {
        this.ipEnd = ipEnd;
    }

    public String getIpStartV6() {
        return ipStartV6;
    }

    public void setIpStartV6(String ipStartV6) {
        this.ipStartV6 = ipStartV6;
    }

    public String getIpEndV6() {
        return ipEndV6;
    }

    public void setIpEndV6(String ipEndV6) {
        this.ipEndV6 = ipEndV6;
    }

    public Integer getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(Integer ipVersion) {
        this.ipVersion = ipVersion;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getGeoId() {
        return geoId;
    }

    public void setGeoId(Integer geoId) {
        this.geoId = geoId;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getIpStartStr() {
        return ipStartStr;
    }

    public void setIpStartStr(String ipStartStr) {
        this.ipStartStr = ipStartStr;
    }

    public String getIpEndStr() {
        return ipEndStr;
    }

    public void setIpEndStr(String ipEndStr) {
        this.ipEndStr = ipEndStr;
    }
}
