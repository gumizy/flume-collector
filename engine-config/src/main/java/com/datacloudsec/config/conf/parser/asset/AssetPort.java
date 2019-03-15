package com.datacloudsec.config.conf.parser.asset;

import java.io.Serializable;

public class AssetPort implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String portId;
	private String protocol;
	private String service;
	private String operationSystem;
	private int state; //1.开启
	
	public String getPortId() {
		return portId;
	}
	public void setPortId(String portId) {
		this.portId = portId;
	}
	public String getService() {
		return service;
	}
	public int getState() {
		return state;
	}
	public void setService(String service) {
		this.service = service;
	}
	public void setState(int state) {
		this.state = state;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getProtocol() {
		return protocol;
	}
	public String getOperationSystem() {
		return operationSystem;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public void setOperationSystem(String operationSystem) {
		this.operationSystem = operationSystem;
	}
	
}
