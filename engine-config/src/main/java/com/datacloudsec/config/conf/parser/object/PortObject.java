package com.datacloudsec.config.conf.parser.object;

import java.io.Serializable;
import java.util.Date;

/**
 * PortObject
 *
 * @author gumizy 2017/7/24
 */
public class PortObject implements Serializable {


    private Integer id;

    private String name;

    private String description;

    private String port;

    private String portType; // all, tcp, udp

    private String excludePort;

    private Integer group;

    private String groupMember;

    private Integer predefined;

    private Integer deleted;

    private String adminName;

    private Date insertTime;

    private Date updateTime;

    private String extraInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getExcludePort() {
        return excludePort;
    }

    public void setExcludePort(String excludePort) {
        this.excludePort = excludePort;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(String groupMember) {
        this.groupMember = groupMember;
    }

    public Integer getPredefined() {
        return predefined;
    }

    public void setPredefined(Integer predefined) {
        this.predefined = predefined;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
}
