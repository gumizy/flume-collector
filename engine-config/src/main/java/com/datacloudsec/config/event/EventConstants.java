package com.datacloudsec.config.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface EventConstants extends CommonConstants {

    // 默认字段 ===============================================================

    // 唯一ID
    String EVENT_UUID = "event_uuid";

    // 事件分类
    String EVENTTYPE = "event_type";

    // 事件名称
    String EVENTNAME = "event_name";

    // 事件级别
    String EVENTLEVEL = "event_level";

    // 发生时间
    String OCCURTIME = "occur_time";

    // 源地址
    String SRCADDRESS = "src_address";

    // 目的地址
    String DSTADDRESS = "dst_address";

    //解析规则ID
    String RULEID = "rule_id";

    // 基础字段 ===============================================================

    //下一跳
    String NEXTHOP = "nexthop";

    //接入接口索引
    String INPUT = "input";

    //流出接口索引
    String OUTPUT = "output";

    //应用启动时间
    String SYS_UPTIME = "sys_uptime";

    //开始时间
    String FIRSTTIME = "first_time";

    //结束时间
    String ENDTIME = "end_time";

    //标志位
    String TCPFLAGS = "tcp_flags";

    //服务类型
    String TOS = "tos";

    //源自治域
    String SRCAS = "src_as";

    //目的自治域
    String DSTAS = "dst_as";

    //源地址掩码
    String SRCMASK = "src_mask";

    //目的地址掩码
    String DSTMASK = "dst_mask";

    //传输层协议
    String TRANPROTOCOL = "tran_protocol";

    // 合并数量
    String MERGECOUNT = "merge_count";

    // 事件摘要
    String EVENTDIGEST = "event_digest";

    // 网络协议
    String NETPROTOCOL = "net_protocol";

    // 应用协议
    String APPPROTOCOL = "app_protocol";

    // 源名称
    String SRCNAME = "src_name";

    // 源MAC
    String SRCMAC = "src_mac";

    // 源端口
    String SRCPORT = "src_port";

    // 源转换地址
    String SRCNATADDRESS = "src_nat_address";

    // 源转换端口
    String SRCNATPORT = "src_nat_port";

    // 目的MAC
    String DSTMAC = "dst_mac";

    // 目的端口
    String DSTPORT = "dst_port";

    // 目的名称
    String DSTNAME = "dst_name";

    // 目的转换地址
    String DSTNATADDRESS = "dst_nat_address";

    // 目的转换端口
    String DSTNATPORT = "dst_nat_port";

    // 用户帐号
    String USERACCOUNT = "user_account";

    // 用户名称
    String USERNAME = "user_name";

    // 程序名称
    String PROGRAMNAME = "program_name";

    // 操作
    String ACTION = "action";

    // 操作
    String OPERATION = "operation";

    // 操作对象
    String OPERATIONOBJECT = "operation_object";

    // 结果
    String RESULT = "result";

    // 响应
    String RESPONSE = "response";

    // 设备名称
    String DEVNAME = "dev_name";

    // 设备类型
    String DEVTYPE = "dev_type";

    // 设备地址
    String DEVADDRESS = "dev_address";

    // 发送字节数
    String SENDBYTE = "send_byte";

    // 接收字节数
    String RECEIVEBYTE = "receive_byte";

    // 总流量字节数
    String TOTALBYTE = "total_byte";

    // 总包数
    String TOTALPACKAGE = "total_package";

    // 发送包数
    String SENDPACKAGE = "send_package";

    // 接收包数
    String RECEIVEPACKAGE = "receive_package";

    // 持续时间
    String DURATIONTIME = "duration_time";

    // 监控数值
    String MONITORVALUE = "monitor_value";

    // 原始级别
    String ORIGINALLEVEL = "original_level";

    // 原始类型
    String ORIGINALTYPE = "original_type";

    // 原始名称
    String ORIGINALNAME = "original_name";

    // 请求信息
    String REQUESTMSG = "request_msg";

    // 原始日志
    String ORIGINALLOG = "original_log";

    // 网址
    String URL = "url";

    // 厂商
    String VENDOR = "vendor";

    // 产品
    String PRODUCT = "product";

    // 模块名称
    String MODULENAME = "module_name";

    // 病毒名称
    String VIRUSNAME = "virus_name";

    // 病毒类型
    String VIRUSTYPE = "virus_type";

    // 病毒地址
    String VIRUSPATH = "virus_path";

    // 发件人
    String SENDMAIL = "send_mail";

    // 收件人
    String RECEIVEMAIL = "receive_mail";

    // 邮件主题
    String MAILSUBJECT = "mail_subject";

    // 发送文件大小
    String SENDFILESIZE = "send_file_size";

    // 接收文件大小
    String RECEIVEFILESIZE = "receive_file_size";

    // 补全字段 ===============================================================

    // 接收时间
    String RECEIVETIME = "receive_time";

    // 采集类型
    String COLLECTTYPE = "collect_type";

    // 采集器地址
    String COLLECTORADDRESS = "collector_address";

    // 事件内容
    String EVENTCONTENT = "event_content";

    // 源GEO
    String SRCGEO = "src_geo";

    // 源国家
    String SRCCOUNTRY = "src_country";

    // 源省份
    String SRCPROVINCE = "src_province";

    // 源城市
    String SRCCITY = "src_city";

    // 源资产ID
    String SRCASSETID = "src_asset_id";

    // 源资产类型ID
    String SRCASSETTYPEID = "src_asset_type_id";

    // 源自定义位置ID
    String SRCLOCATIONID = "src_asset_location_id";

    // 源资产组织机构ID
    String SRCASSETDEPARTMENTID = "src_asset_department_id";

    // 源资产业务系统ID
    String SRCASSETBUSINESSID = "src_asset_business_id";

    // 源资产安全域ID
    String SRCASSETDOMAINID = "src_asset_domain_id";

    // 目的GEO
    String DSTGEO = "dst_geo";

    // 目的国家
    String DSTCOUNTRY = "dst_country";

    // 目的省份
    String DSTPROVINCE = "dst_province";

    // 目的城市
    String DSTCITY = "dst_city";

    // 目的资产ID
    String DSTASSETID = "dst_asset_id";

    // 目的资产类型ID
    String DSTASSETTYPEID = "dst_asset_type_id";

    // 源物理位置ID
    String DSTLOCATIONID = "dst_asset_location_id";

    // 目的资产组织机构ID
    String DSTASSETDEPARTMENTID = "dst_asset_department_id";

    // 目的资产业务系统ID
    String DSTASSETBUSINESSID = "dst_asset_business_id";

    // 目的资产安全域ID
    String DSTASSETDOMAINID = "dst_asset_domain_id";

    // 设备资产ID
    String DEVASSETID = "dev_asset_id";

    // 设备资产类型ID
    String DEVASSETTYPEID = "dev_asset_type_id";

    // 五元组
    String SASPAPDADP = "sa_sp_ap_da_dp";

    // 二元组
    String SADA = "sa_da";

    // 经纬度和地理位置名称
    String LONLANAME = "longitude_latitude_name";

    //自然人字段 ===============================================================

    // 员工工号
    String EMPLOYEEWORKNUM = "employee_work_num";

    // 员工帐号
    String EMPLOYEEACCOUNT = "employee_account";

    // 员工名称
    String EMPLOYEENAME = "employee_name";

    // 员工部门
    String EMPLOYEEDEPARTMENT = "employee_department";

    // 员工部门
    String EDEPARTMENTNAME = "department_name";

    // 员工公司
    String EMPLOYEECOMPANY = "employee_company";

    // 角色/职位
    String EMPLOYEEROLE = "employee_role";

    // 工作城市
    String EMPLOYEEWORKCITY = "employee_work_city";

    // 连接状态
    String CONN_STATUS = "conn_status";

    // 使用率
    String CPU_USAGE = "cpu_usage";
    String MEM_USAGE = "mem_usage";
    String DISK_USAGE = "disk_usage";


    // 类型约束////////////////////////////////////////////////////////////////

    Set<String> TIME2LONG_SET = new HashSet<>(Arrays.asList(OCCURTIME));

    Set<String> IP_SET = new HashSet<>(Arrays.asList(SRCADDRESS, SRCNATADDRESS, DSTADDRESS,
            DSTNATADDRESS, DEVADDRESS, NEXTHOP, COLLECTORADDRESS));

    Set<String> LONG_SET = new HashSet<>(Arrays.asList(ID, SENDBYTE, RECEIVEBYTE, DURATIONTIME,
            SENDPACKAGE, RECEIVEPACKAGE, OCCURTIME, SYS_UPTIME, FIRSTTIME, ENDTIME));

    Set<String> INTEGER_SET = new HashSet<>(Arrays.asList(MERGECOUNT, EVENTLEVEL, TRANPROTOCOL,
            SRCPORT, SRCNATPORT, DSTPORT, DSTNATPORT, INPUT, OUTPUT, TCPFLAGS, TOS));

    Set<String> DOUBLE_SET = new HashSet<>(Arrays.asList(MONITORVALUE, CPU_USAGE, MEM_USAGE, DISK_USAGE));

    Set<String> GEOPOINT_SET = new HashSet<>(Arrays.asList(SRCGEO, DSTGEO));
}
