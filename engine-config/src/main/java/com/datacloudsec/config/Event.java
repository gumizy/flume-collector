package com.datacloudsec.config;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.config.event.AbstractEvent;
import com.datacloudsec.config.event.CommonConstants;
import com.datacloudsec.config.event.EventConstants;
import com.datacloudsec.config.geo.GeoPoint;
import com.datacloudsec.config.tools.TimeIdUtil;
import com.datacloudsec.config.tools.UUIDUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;
import java.util.regex.Matcher;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.EVENT_INDEX_TYPE;
import static com.datacloudsec.config.conf.BasicConfigurationConstants.NO_PARSER_TYPE;

/**
 * Event事件：数据采集后转换为一条事件
 */
public class Event extends AbstractEvent {

    private static final long serialVersionUID = 1L;

    @Override
    public Long getId() {
        return getLong(EventConstants.ID);
    }

    @Override
    public void setId(Long id) {
        putValue(EventConstants.ID, id);
    }

    // 默认字段 ===============================================================

    public String getEventUUID() {
        return getString(EventConstants.EVENT_UUID);
    }

    public void setEventUUID(String eventUUID) {
        putValue(EventConstants.EVENT_UUID, eventUUID);
    }

    public String getEventName() {
        return getString(EventConstants.EVENTNAME);
    }

    public void setEventName(String eventName) {
        putValue(EventConstants.EVENTNAME, eventName);
    }

    public String getEventType() {
        return getString(EventConstants.EVENTTYPE);
    }

    public void setEventType(String eventType) {
        putValue(EventConstants.EVENTTYPE, eventType);
    }

    public Long getOccurTime() {
        return getLong(EventConstants.OCCURTIME);
    }

    public void setOccurTime(Long occur_time) {
        putValue(EventConstants.OCCURTIME, occur_time);
    }

    public String getSrcAddress() {
        return getString(EventConstants.SRCADDRESS);
    }

    public void setSrcAddress(String src_address) {
        putValue(EventConstants.SRCADDRESS, src_address);
    }

    public String getSrcNatAddress() {
        return getString(EventConstants.SRCNATADDRESS);
    }

    public void setSrcNatAddress(String src_nat_address) {
        putValue(EventConstants.SRCNATADDRESS, src_nat_address);
    }

    public String getDstAddress() {
        return getString(EventConstants.DSTADDRESS);
    }

    public void setDstAddress(String dst_address) {
        putValue(EventConstants.DSTADDRESS, dst_address);
    }

    public String getDstNatAddress() {
        return getString(EventConstants.DSTNATADDRESS);
    }

    public void setDstNatAddress(String dst_nat_address) {
        putValue(EventConstants.DSTNATADDRESS, dst_nat_address);
    }

    public Integer getEventLevel() {
        return getInteger(EventConstants.EVENTLEVEL);
    }

    public void setEventLevel(Integer event_level) {
        putValue(EventConstants.EVENTLEVEL, event_level);
    }

    public void setRuleId(Integer ruleId) {
        putValue(EventConstants.RULEID, ruleId);
    }

    public Integer getRuleId() {
        return getInteger(EventConstants.RULEID);
    }

    // 基础字段  ===============================================================

    public Long getReceiveTime() {
        return getLong(EventConstants.RECEIVETIME);
    }

    public String getNexthop() {
        return getString(EventConstants.NEXTHOP);
    }

    public void setNexthop(String nexthop) {
        putValue(EventConstants.NEXTHOP, nexthop);
    }

    public Integer getInput() {
        return getInteger(EventConstants.INPUT);
    }

    public void setInput(int input) {
        putValue(EventConstants.INPUT, input);
    }

    public Integer getOutput() {
        return getInteger(EventConstants.OUTPUT);
    }

    public void setOutput(int output) {
        putValue(EventConstants.OUTPUT, output);
    }

    public Long getFirstTime() {
        return getLong(EventConstants.FIRSTTIME);
    }

    public void setFirstTime(long firstTime) {
        putValue(EventConstants.FIRSTTIME, firstTime);
    }

    public Long getSysUpTime() {
        return getLong(EventConstants.SYS_UPTIME);
    }

    public void setSysUpTime(long firstTime) {
        putValue(EventConstants.SYS_UPTIME, firstTime);
    }

    public Long getEndTime() {
        return getLong(EventConstants.ENDTIME);
    }

    public void setEndTime(long endTime) {
        putValue(EventConstants.ENDTIME, endTime);
    }

    public Integer getTcpFlags() {
        return getInteger(EventConstants.TCPFLAGS);
    }

    public void setTcpFlags(int tcpFlags) {
        putValue(EventConstants.TCPFLAGS, tcpFlags);
    }

    public String getTos() {
        return getString(EventConstants.TOS);
    }

    public void setTos(String tos) {
        putValue(EventConstants.TOS, tos);
    }

    public String getSrcAs() {
        return getString(EventConstants.SRCAS);
    }

    public void setSrcAs(String srcAs) {
        putValue(EventConstants.SRCAS, srcAs);
    }

    public String getDstAs() {
        return getString(EventConstants.DSTAS);
    }

    public void setDstAs(String dstAs) {
        putValue(EventConstants.DSTAS, dstAs);
    }

    public void setSrcMask(String srcMask) {
        putValue(EventConstants.SRCMASK, srcMask);
    }

    public String getSrcMask() {
        return getString(EventConstants.SRCMASK);
    }

    public void setDstMask(String dstMask) {
        putValue(EventConstants.DSTMASK, dstMask);
    }

    public String getDstMask() {
        return getString(EventConstants.DSTMAC);
    }

    public Integer getTranProtocol() {
        return getInteger(EventConstants.TRANPROTOCOL);
    }

    public void setTranProtocol(Integer transProtocol) {
        putValue(EventConstants.TRANPROTOCOL, transProtocol);
    }

    public void setReceiveTime(Long receive_time) {
        putValue(EventConstants.RECEIVETIME, receive_time);
    }

    public String getSrcMac() {
        return getString(EventConstants.SRCMAC);
    }

    public void setSrcMac(String src_mac) {
        putValue(EventConstants.SRCMAC, src_mac);
    }

    public Integer getSrcPort() {
        return getInteger(EventConstants.SRCPORT);
    }

    public void setSrcPort(Integer src_port) {
        putValue(EventConstants.SRCPORT, src_port);
    }

    public Integer getSrcNatPort() {
        return getInteger(EventConstants.DSTNATPORT);
    }

    public void setSrcNatPort(Integer dst_nat_port) {
        putValue(EventConstants.DSTNATPORT, dst_nat_port);
    }

    public String getSrcName() {
        return getString(EventConstants.SRCNAME);
    }

    public void setSrcName(String src_name) {
        putValue(EventConstants.SRCNAME, src_name);
    }

    public String getDstMac() {
        return getString(EventConstants.DSTMAC);
    }

    public void setDstMac(String dst_mac) {
        putValue(EventConstants.DSTMAC, dst_mac);
    }

    public Integer getDstPort() {
        return getInteger(EventConstants.DSTPORT);
    }

    public void setDstPort(Integer dst_port) {
        putValue(EventConstants.DSTPORT, dst_port);
    }

    public Integer getDstNatPort() {
        return getInteger(EventConstants.DSTNATPORT);
    }

    public void setDstNatPort(Integer dst_nat_port) {
        putValue(EventConstants.DSTNATPORT, dst_nat_port);
    }

    public String getDstName() {
        return getString(EventConstants.DSTNAME);
    }

    public void setDstName(String dst_name) {
        putValue(EventConstants.DSTNAME, dst_name);
    }

    public String getNetProtocol() {
        return getString(EventConstants.NETPROTOCOL);
    }

    public void setNetProtocol(String net_protocol) {
        putValue(EventConstants.NETPROTOCOL, net_protocol);
    }

    public String getAppProtocol() {
        return getString(EventConstants.APPPROTOCOL);
    }

    public void setAppProtocol(String app_protocol) {
        putValue(EventConstants.APPPROTOCOL, app_protocol);
    }

    public String getOriginalLevel() {
        return getString(EventConstants.ORIGINALLEVEL);
    }

    public void setOriginalLevel(String original_level) {
        putValue(EventConstants.ORIGINALLEVEL, original_level);
    }

    public String getOriginalName() {
        return getString(EventConstants.ORIGINALNAME);
    }

    public void setOriginalName(String original_name) {
        putValue(EventConstants.ORIGINALNAME, original_name);
    }

    public String getRequestMsg() {
        return getString(EventConstants.REQUESTMSG);
    }

    public void setRequestMsg(String request_msg) {
        putValue(EventConstants.REQUESTMSG, request_msg);
    }

    public String getURL() {
        return getString(EventConstants.URL);
    }

    public void setURL(String url) {
        putValue(EventConstants.URL, url);
    }

    public String getVendor() {
        return getString(EventConstants.VENDOR);
    }

    public void setVendor(String vendor) {
        putValue(EventConstants.VENDOR, vendor);
    }

    public String getProduct() {
        return getString(EventConstants.PRODUCT);
    }

    public void setProduct(String product) {
        putValue(EventConstants.PRODUCT, product);
    }

    public String getOriginalType() {
        return getString(EventConstants.ORIGINALTYPE);
    }

    public void setOriginalType(String original_type) {
        putValue(EventConstants.ORIGINALTYPE, original_type);
    }

    public Long getMergeCount() {
        return getLong(EventConstants.MERGECOUNT);
    }

    public void setMergeCount(Long merge_count) {
        putValue(EventConstants.MERGECOUNT, merge_count);
    }

    public String getUserAccount() {
        return getString(EventConstants.USERACCOUNT);
    }

    public void setUserAccount(String user_account) {
        putValue(EventConstants.USERACCOUNT, user_account);
    }

    public String getUserName() {
        return getString(EventConstants.USERNAME);
    }

    public void setUserName(String user_name) {
        putValue(EventConstants.USERNAME, user_name);
    }

    public String getAction() {
        return getString(EventConstants.ACTION);
    }

    public void setAction(Integer action) {
        putValue(EventConstants.ACTION, action);
    }

    public String getOperation() {
        return getString(EventConstants.OPERATION);
    }

    public void setOperation(Integer operation) {
        putValue(EventConstants.OPERATION, operation);
    }

    public String getOperationObject() {
        return getString(EventConstants.OPERATIONOBJECT);
    }

    public void setOperationObject(String operation_object) {
        putValue(EventConstants.OPERATIONOBJECT, operation_object);
    }

    public String getResult() {
        return getString(EventConstants.RESULT);
    }

    public void setResult(String result) {
        putValue(EventConstants.RESULT, result);
    }

    public String getResponse() {
        return getString(EventConstants.RESPONSE);
    }

    public void setResponse(Integer response) {
        putValue(EventConstants.RESPONSE, response);
    }

    public Long getDurationTime() {
        return getLong(EventConstants.DURATIONTIME);
    }

    public void setDurationTime(Long duration_time) {
        putValue(EventConstants.DURATIONTIME, duration_time);
    }

    public Double getMonitorValue() {
        return getDouble(EventConstants.MONITORVALUE);
    }

    public void setMonitorValue(Double monitor_value) {
        putValue(EventConstants.MONITORVALUE, monitor_value);
    }

    public String getOriginalLog() {
        return getString(EventConstants.ORIGINALLOG);
    }

    public void setOriginalLog(String original_log) {
        putValue(EventConstants.ORIGINALLOG, original_log);
    }

    public String getDevName() {
        return getString(EventConstants.DEVNAME);
    }

    public void setDevName(String dev_name) {
        putValue(EventConstants.DEVNAME, dev_name);
    }

    public String getDevType() {
        return getString(EventConstants.DEVTYPE);
    }

    public void setDevType(String dev_type) {
        putValue(EventConstants.DEVTYPE, dev_type);
    }

    public String getDevAddress() {
        return getString(EventConstants.DEVADDRESS);
    }

    public void setDevAddress(String dev_address) {
        putValue(EventConstants.DEVADDRESS, dev_address);
    }

    public String getDevAssetId() {
        return getString(EventConstants.DEVASSETID);
    }

    public void setDevAssetId(String dev_asset_id) {
        putValue(EventConstants.DEVASSETID, dev_asset_id);
    }

    public String getModuleName() {
        return getString(EventConstants.MODULENAME);
    }

    public void setModuleName(String module_name) {
        putValue(EventConstants.MODULENAME, module_name);
    }

    public Integer getProgramName() {
        return getInteger(EventConstants.PROGRAMNAME);
    }

    public void setProgramName(Integer program_name) {
        putValue(EventConstants.PROGRAMNAME, program_name);
    }

    public Long getSendByte() {
        return getLong(EventConstants.SENDBYTE);
    }

    public void setSendByte(Long send_byte) {
        putValue(EventConstants.SENDBYTE, send_byte);
    }

    public Long getTotalByte() {
        return getLong(EventConstants.TOTALBYTE);
    }

    public void setTotalByte(Long total_byte) {
        putValue(EventConstants.TOTALBYTE, total_byte);
    }

    public Long getReceiveByte() {
        return getLong(EventConstants.RECEIVEBYTE);
    }

    public void setReceiveByte(Long receive_byte) {
        putValue(EventConstants.RECEIVEBYTE, receive_byte);
    }

    public Long getSendPackage() {
        return getLong(EventConstants.SENDPACKAGE);
    }

    public void setSendPackage(Long send_package) {
        putValue(EventConstants.SENDPACKAGE, send_package);
    }

    public Long getReceivePackage() {
        return getLong(EventConstants.RECEIVEPACKAGE);
    }

    public void setReceivePackage(Long receive_package) {
        putValue(EventConstants.RECEIVEPACKAGE, receive_package);
    }

    public Long getTotalPackage() {
        return getLong(EventConstants.TOTALPACKAGE);
    }

    public void setTotalPackage(Long total_package) {
        putValue(EventConstants.TOTALPACKAGE, total_package);
    }

    public String getVirusName() {
        return getString(EventConstants.VIRUSNAME);
    }

    public void setVirusName(String virus_name) {
        putValue(EventConstants.VIRUSNAME, virus_name);
    }

    public String getVirusType() {
        return getString(EventConstants.VIRUSTYPE);
    }

    public void setVirusType(String virus_type) {
        putValue(EventConstants.VIRUSTYPE, virus_type);
    }

    public String getSendMail() {
        return getString(EventConstants.SENDMAIL);
    }

    public void setSendMail(String send_mail) {
        putValue(EventConstants.SENDMAIL, send_mail);
    }

    public String getReceiveMail() {
        return getString(EventConstants.RECEIVEMAIL);
    }

    public void setReceiveMail(String receive_mail) {
        putValue(EventConstants.RECEIVEMAIL, receive_mail);
    }

    public String getMailSubject() {
        return getString(EventConstants.MAILSUBJECT);
    }

    public void setMailSubject(String mail_subject) {
        putValue(EventConstants.MAILSUBJECT, mail_subject);
    }

    public Long getSendFileSize() {
        return getLong(EventConstants.SENDFILESIZE);
    }

    public void setSendFileSize(Long send_file_size) {
        putValue(EventConstants.SENDFILESIZE, send_file_size);
    }

    public Long getReceiveFileSize() {
        return getLong(EventConstants.RECEIVEFILESIZE);
    }

    public void setReceiveFileSize(Long receive_file_size) {
        putValue(EventConstants.RECEIVEFILESIZE, receive_file_size);
    }

    public String getVirusPath() {
        return getString(EventConstants.VIRUSPATH);
    }

    public void setVirusPath(String virus_path) {
        putValue(EventConstants.VIRUSPATH, virus_path);
    }

    public String getEventDigest() {
        return getString(EventConstants.EVENTDIGEST);
    }

    public void setEventDigest(String event_digest) {
        putValue(EventConstants.EVENTDIGEST, event_digest);
    }

    // 补全字段  ===============================================================

    public GeoPoint getSrcGeo() {
        return getGeoPoint(EventConstants.SRCGEO);
    }

    public void setSrcGeo(GeoPoint src_geo) {
        putValue(EventConstants.SRCGEO, src_geo);
    }

    public String getSrcCountry() {
        return getString(EventConstants.SRCCOUNTRY);
    }

    public void setSrcCountry(String src_country) {
        putValue(EventConstants.SRCCOUNTRY, src_country);
    }

    public String getSrcProvince() {
        return getString(EventConstants.SRCPROVINCE);
    }

    public void setSrcProvince(String src_province) {
        putValue(EventConstants.SRCPROVINCE, src_province);
    }

    public String getSrcCity() {
        return getString(EventConstants.SRCCITY);
    }

    public void setSrcCity(String src_city) {
        putValue(EventConstants.SRCCITY, src_city);
    }

    public String getSrcLocationId() {
        return getString(EventConstants.SRCLOCATIONID);
    }

    public void setSrcLocationId(String src_location_id) {
        putValue(EventConstants.SRCLOCATIONID, src_location_id);
    }

    public String getSrcAssetId() {
        return getString(EventConstants.SRCASSETID);
    }

    public void setSrcAssetId(String src_asset_id) {
        putValue(EventConstants.SRCASSETID, src_asset_id);
    }

    public String getSrcAssetTypeId() {
        return getString(EventConstants.SRCASSETTYPEID);
    }

    public void setSrcAssetTypeId(String src_asset_type_id) {
        putValue(EventConstants.SRCASSETTYPEID, src_asset_type_id);
    }

    public String getSrcAssetDepartmentId() {
        return getString(EventConstants.SRCASSETDEPARTMENTID);
    }

    public void setSrcAssetDepartmentId(String src_asset_department_id) {
        putValue(EventConstants.SRCASSETDEPARTMENTID, src_asset_department_id);
    }

    public String getSrcAssetBusinessId() {
        return getString(EventConstants.SRCASSETBUSINESSID);
    }

    public void setSrcAssetBusinessId(String src_asset_business_id) {
        putValue(EventConstants.SRCASSETBUSINESSID, src_asset_business_id);
    }

    public String getSrcAssetDomainId() {
        return getString(EventConstants.SRCASSETDOMAINID);
    }

    public void setSrcAssetDomainId(String src_asset_domain_id) {
        putValue(EventConstants.SRCASSETDOMAINID, src_asset_domain_id);
    }

    public GeoPoint getDstGeo() {
        return getGeoPoint(EventConstants.DSTGEO);
    }

    public void setDstGeo(String dst_geo) {
        putValue(EventConstants.DSTGEO, dst_geo);
    }

    public String getDstCountry() {
        return getString(EventConstants.DSTCOUNTRY);
    }

    public void setDstCountry(String dst_country) {
        putValue(EventConstants.DSTCOUNTRY, dst_country);
    }

    public String getDstProvince() {
        return getString(EventConstants.DSTPROVINCE);
    }

    public void setDstProvince(String dst_province) {
        putValue(EventConstants.DSTPROVINCE, dst_province);
    }

    public String getDstCity() {
        return getString(EventConstants.DSTCITY);
    }

    public void setDstCity(String dst_city) {
        putValue(EventConstants.DSTCITY, dst_city);
    }

    public String getDstLocationId() {
        return getString(EventConstants.DSTLOCATIONID);
    }

    public void setDstLocationId(String dst_location_id) {
        putValue(EventConstants.DSTLOCATIONID, dst_location_id);
    }

    public String getDstAssetId() {
        return getString(EventConstants.DSTASSETID);
    }

    public void setDstAssetId(String dst_asset_id) {
        putValue(EventConstants.DSTASSETID, dst_asset_id);
    }

    public String getDstAssetTypeId() {
        return getString(EventConstants.DSTASSETTYPEID);
    }

    public void setDstAssetTypeId(String dst_asset_type_id) {
        putValue(EventConstants.DSTASSETTYPEID, dst_asset_type_id);
    }

    public String getDstAssetDepartmentId() {
        return getString(EventConstants.DSTASSETDEPARTMENTID);
    }

    public void setDstAssetDepartmentId(String dst_asset_department_id) {
        putValue(EventConstants.DSTASSETDEPARTMENTID, dst_asset_department_id);
    }

    public String getDstAssetBusinessId() {
        return getString(EventConstants.DSTASSETBUSINESSID);
    }

    public void setDstAssetBusinessId(String dst_asset_business_id) {
        putValue(EventConstants.DSTASSETBUSINESSID, dst_asset_business_id);
    }

    public String getDstAssetDomainId() {
        return getString(EventConstants.DSTASSETDOMAINID);
    }

    public void setDstAssetDomainId(String dst_asset_domain_id) {
        putValue(EventConstants.DSTASSETDOMAINID, dst_asset_domain_id);
    }

    public String getEventContent() {
        return getString(EventConstants.EVENTCONTENT);
    }

    public void setEventContent(String event_content) {
        putValue(EventConstants.EVENTCONTENT, event_content);
    }

    public String getCollectType() {
        return getString(EventConstants.COLLECTTYPE);
    }

    public void setCollectType(String collect_type) {
        putValue(EventConstants.COLLECTTYPE, collect_type);
    }

    public String getCollectorAddress() {
        return getString(EventConstants.COLLECTORADDRESS);
    }

    public void setCollectorAddress(String collector_address) {
        putValue(EventConstants.COLLECTORADDRESS, collector_address);
    }

    public String getSaSpApDaDp() {
        return getString(EventConstants.SASPAPDADP);
    }

    public void setSaSpApDaDp(String sa_sp_ap_da_dp) {
        putValue(EventConstants.SASPAPDADP, sa_sp_ap_da_dp);
    }

    public String getSaDa() {
        return getString(EventConstants.SADA);
    }

    public void setSaDa(String sa_da) {
        putValue(EventConstants.SADA, sa_da);
    }

    public String getLongitudeLatitudeName() {
        return getString(EventConstants.LONLANAME);
    }

    public void setLongitudeLatitudeName(String longitude_latitude_name) {
        putValue(EventConstants.LONLANAME, longitude_latitude_name);
    }

    public String getEmployeeWorkNum() {
        return getString(EventConstants.EMPLOYEEWORKNUM);
    }

    public void setEmployeeWorkNum(String employee_work_num) {
        putValue(EventConstants.EMPLOYEEWORKNUM, employee_work_num);
    }

    public String getEmployeeAccount() {
        return getString(EventConstants.EMPLOYEEACCOUNT);
    }

    public void setEmployeeAccount(String employee_account) {
        putValue(EventConstants.EMPLOYEEACCOUNT, employee_account);
    }

    public String getEmployeeName() {
        return getString(EventConstants.EMPLOYEENAME);
    }

    public void setEmployeeName(String employee_name) {
        putValue(EventConstants.EMPLOYEENAME, employee_name);
    }

    public String getEmployeeDepartment() {
        return getString(EventConstants.EMPLOYEEDEPARTMENT);
    }

    public void setEmployeeDepartment(String employee_department) {
        putValue(EventConstants.EMPLOYEEDEPARTMENT, employee_department);
    }

    public String getDepartmentName() {
        return getString(EventConstants.EDEPARTMENTNAME);
    }

    public void setDepartmentName(String department_name) {
        putValue(EventConstants.EDEPARTMENTNAME, department_name);
    }

    public String getEmployeeRole() {
        return getString(EventConstants.EMPLOYEEROLE);
    }

    public void setEmployeeRole(String employee_role) {
        putValue(EventConstants.EMPLOYEEROLE, employee_role);
    }

    public String getEmployeeWorkCity() {
        return getString(EventConstants.EMPLOYEEWORKNUM);
    }

    public void setEmployeeWorkCity(String employee_work_city) {
        putValue(EventConstants.EMPLOYEEWORKCITY, employee_work_city);
    }

    @Override
    public void putValue(String field, Object value) {
        if (EventConstants.INTEGER_SET.contains(field)) {
            if (value != null) {
                valueMap.put(field, NumberUtils.toInt(value.toString().trim()));
            }
        } else if (EventConstants.LONG_SET.contains(field)) {
            if (value != null) {
                valueMap.put(field, NumberUtils.toLong(value.toString().trim()));
            }
        } else if (EventConstants.DOUBLE_SET.contains(field)) {
            if (value != null) {
                valueMap.put(field, NumberUtils.toDouble(value.toString().trim()));
            }
        } else if (EventConstants.IP_SET.contains(field)) {
            if (value != null) {
                String value0 = value.toString().trim();
                Matcher matcher = CommonConstants.IP_PATTERN.matcher(value0);
                if (matcher.matches()) {
                    valueMap.put(field, value0);
                }
            }
        } else if (EventConstants.GEOPOINT_SET.contains(field)) {
            valueMap.put(field, value);
        } else {
            valueMap.put(field, value);
        }
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    public String valueMapToJSONString() {
        return JSON.toJSONString(this.valueMap);
    }

    /**
     * 设置map值
     *
     * @param map map
     */
    public void valuesFromMap(Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null) {
                    this.putValue(key, value);
                }
            }
        }
    }

    /**
     * 设置部分默认值
     */
    public void valuesDefault() {
        if (StringUtils.isBlank(this.getEventUUID())) {
            this.setEventUUID(UUIDUtil.generateShortUUID());
        }
        if (this.getId() == null) {
            this.setId(TimeIdUtil.generator(System.currentTimeMillis()));
        }
        if (this.getMergeCount() == null) {
            this.setMergeCount(1L);
        }
        if (this.getReceiveTime() == null || this.getReceiveTime() <= 0) {
            this.setReceiveTime(System.currentTimeMillis());
        }
        if (this.getOccurTime() == null || this.getOccurTime() <= 0) {
            this.setOccurTime(this.getReceiveTime());
        }
        if (this.getFirstTime() == null || this.getFirstTime() <= 0) {
            this.setFirstTime(this.getReceiveTime());
        }
        if (this.getEndTime() == null || this.getEndTime() <= 0) {
            this.setEndTime(this.getReceiveTime());
        }
        if (this.getIndexType() == null) {
            this.setIndexType(EVENT_INDEX_TYPE);
        }
        if (this.getEventType() == null) {
            this.setEventType(NO_PARSER_TYPE);
        }
        if (this.getCollectorAddress() == null) {
            this.setCollectorAddress("");
        }
        if (this.getDevAddress() == null) {
            this.setDevAddress(this.getCollectorAddress());
        }
        if (this.getOriginalLog() == null) {
            this.setOriginalLog("");
        }
        if (this.getRuleId() == null) {
            this.setRuleId(-1);
        }
        if (this.getEndTime() != null && this.getFirstTime() != null && getEndTime() >= getFirstTime()) {
            this.setDurationTime(this.getEndTime() - this.getFirstTime());
        }
        if (this.getFirstTime() != null && this.getFirstTime() > 0 && this.getFirstTime().toString().length() == 10) {
            this.setFirstTime(this.getFirstTime() * 1000);
        }
        if (this.getEndTime() != null && this.getEndTime() > 0 && this.getEndTime().toString().length() == 10) {
            this.setEndTime(this.getEndTime() * 1000);
        }
    }
}
