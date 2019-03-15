package com.datacloudsec.config.conf.parser.asset;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asset
 *
 * @author gumizy 2017/6/22
 */
public class Asset implements Serializable {

     private Integer assetId;

    private String assetName;

    private String assetCode;

    private Integer sourceType;

    private String ip;

    private BigInteger ipValue;

    private String mac;

    private Integer assetTypeId; // 资产类型

    private String assetTypeIdPath;

    private Integer assetOrgId; // 组织机构

    private String assetOrgIdPath;

    private Integer assetSysId; // 业务系统

    private String assetSysIdPath;

    private Integer assetLocationId; // 物理位置

    private String assetLocationIdPath;

    private Integer keyAsset;

    private String chargeUser;

    private String user;

    private String email;

    private String phone;

    private Integer status;

    private Integer factoryId;

    private String modelNumber;

    private Integer secretLevel;  // 机密等级

    private Integer completeLevel; // 完整等级

    private Integer availableLevel; // 可用等级

    private String description;

    private Date insertTime;

    private Date updateTime;

    private Integer confirmStatus;

    private Integer deleted;

    private String adminName;

    private String extraInfo;
    
     private String hostName;
    
     private String operationSystem;
    
     private List<AssetPort> ports;
    
     private boolean hisAlarm;
    
     private boolean conStatus;
    
//    @Transient
//    private List<EventAlarmLog> eventAlarmLogs;

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BigInteger getIpValue() {
        return ipValue;
    }

    public void setIpValue(BigInteger ipValue) {
        this.ipValue = ipValue;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Integer assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public String getAssetTypeIdPath() {
        return assetTypeIdPath;
    }

    public void setAssetTypeIdPath(String assetTypeIdPath) {
        this.assetTypeIdPath = assetTypeIdPath;
    }

    public Integer getAssetOrgId() {
        return assetOrgId;
    }

    public void setAssetOrgId(Integer assetOrgId) {
        this.assetOrgId = assetOrgId;
    }

    public String getAssetOrgIdPath() {
        return assetOrgIdPath;
    }

    public void setAssetOrgIdPath(String assetOrgIdPath) {
        this.assetOrgIdPath = assetOrgIdPath;
    }

    public Integer getAssetSysId() {
        return assetSysId;
    }

    public void setAssetSysId(Integer assetSysId) {
        this.assetSysId = assetSysId;
    }

    public String getAssetSysIdPath() {
        return assetSysIdPath;
    }

    public void setAssetSysIdPath(String assetSysIdPath) {
        this.assetSysIdPath = assetSysIdPath;
    }

    public Integer getAssetLocationId() {
        return assetLocationId;
    }

    public void setAssetLocationId(Integer assetLocationId) {
        this.assetLocationId = assetLocationId;
    }

    public String getAssetLocationIdPath() {
        return assetLocationIdPath;
    }

    public void setAssetLocationIdPath(String assetLocationIdPath) {
        this.assetLocationIdPath = assetLocationIdPath;
    }

    public Integer getKeyAsset() {
        return keyAsset;
    }

    public void setKeyAsset(Integer keyAsset) {
        this.keyAsset = keyAsset;
    }

    public String getChargeUser() {
        return chargeUser;
    }

    public void setChargeUser(String chargeUser) {
        this.chargeUser = chargeUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Integer factoryId) {
        this.factoryId = factoryId;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    public Integer getCompleteLevel() {
        return completeLevel;
    }

    public void setCompleteLevel(Integer completeLevel) {
        this.completeLevel = completeLevel;
    }

    public Integer getAvailableLevel() {
        return availableLevel;
    }

    public void setAvailableLevel(Integer availableLevel) {
        this.availableLevel = availableLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
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

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
    public Map<String, Object> toDataMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("assetId", this.assetId);
        map.put("assetName", this.assetName);
        map.put("assetCode", this.assetCode);
        map.put("ip", this.ip);
        map.put("ipValue", this.ipValue);
        return map;
    }
	public List<AssetPort> getPorts() {
		return ports;
	}

	public void setPorts(List<AssetPort> ports) {
		this.ports = ports;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getOperationSystem() {
		return operationSystem;
	}

	public void setOperationSystem(String operationSystem) {
		this.operationSystem = operationSystem;
	}

//	public List<EventAlarmLog> getEventAlarmLogs() {
//		return eventAlarmLogs;
//	}
//
//	public void setEventAlarmLogs(List<EventAlarmLog> eventAlarmLogs) {
//		this.eventAlarmLogs = eventAlarmLogs;
//	}

	public boolean isHisAlarm() {
		return hisAlarm;
	}

	public boolean isConStatus() {
		return conStatus;
	}

	public void setHisAlarm(boolean hisAlarm) {
		this.hisAlarm = hisAlarm;
	}

	public void setConStatus(boolean conStatus) {
		this.conStatus = conStatus;
	}
	
}
