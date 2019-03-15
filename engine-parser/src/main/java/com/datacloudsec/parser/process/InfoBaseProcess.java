package com.datacloudsec.parser.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datacloudsec.core.conf.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author gumizy
 * @Date 2018/7/12 20:26
 */
public class InfoBaseProcess implements ParserMetadataProcess {
    private static Logger logger = LoggerFactory.getLogger(InfoBaseProcess.class);

    @Override
    public void process(String value) {
        logger.info("InfoBaseProcess doInfoBaseProcess start......");
        try {
            if (StringUtils.isNotBlank(value)) {
                JSONObject jsonObject = JSON.parseObject(value);
                String virusInfoStr = jsonObject.getString(VirusInfo.class.getSimpleName());
                String vulnerabilityInfoStr = jsonObject.getString(VulnerabilityInfo.class.getSimpleName());
                String malwareCodeStr = jsonObject.getString(MalwareCode.class.getSimpleName());
                String malwareDomainStr = jsonObject.getString(MalwareDomain.class.getSimpleName());
                String trojansInfoStr = jsonObject.getString(TrojansInfo.class.getSimpleName());
                String malwareIPStr = jsonObject.getString(MalwareIP.class.getSimpleName());
                String malwareDNSStr = jsonObject.getString("MalwareDNS");
                String malwareURLStr = jsonObject.getString(MalwareURL.class.getSimpleName());

                List<VirusInfo> virusInfos = JSON.parseArray(virusInfoStr, VirusInfo.class);
                List<VulnerabilityInfo> vulnerabilityInfos = JSON.parseArray(vulnerabilityInfoStr, VulnerabilityInfo.class);
                List<MalwareCode> malwareCodes = JSON.parseArray(malwareCodeStr, MalwareCode.class);
                List<MalwareDomain> malwareDomains = JSON.parseArray(malwareDomainStr, MalwareDomain.class);
                List<TrojansInfo> trojansInfos = JSON.parseArray(trojansInfoStr, TrojansInfo.class);
                List<MalwareIP> malwareIPS = JSON.parseArray(malwareIPStr, MalwareIP.class);
                List<MalwareIP> malwareDNS = JSON.parseArray(malwareDNSStr, MalwareIP.class);
                List<MalwareURL> malwareURLS = JSON.parseArray(malwareURLStr, MalwareURL.class);

                // virus
                if (virusInfos != null) {
                    VirusInfoSearch.initVirusInfo(virusInfos);
                }
                // loophole
                if (vulnerabilityInfos != null) {
                    VulnerabilityInfoSearch.initVulnerabilityInfo(vulnerabilityInfos);
                }
                // code
                if (malwareCodes != null) {
                    MalwareCodeSearch.initMalwareCode(malwareCodes);
                }
                // domain
                if (malwareDomains != null) {
                    MalwareDomainSearch.initMalwareDomain(malwareDomains);
                }
                // trojan
                if (trojansInfos != null) {
                    TrojansInfoSearch.initTrojansInfo(trojansInfos);
                }
                // ip
                if (malwareIPS != null) {
                    MalwareIpSearch.initMalwareIps(malwareIPS);
                }

                // dns
                if (malwareDNS != null) {
                    MalwareIpSearch.initMalwareDNS(malwareDNS);
                }

                // url
                if (malwareURLS != null) {
                    MalwareURLSearch.initMalwareURL(malwareURLS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("InfoBaseProcess doInfoBaseProcess end......");

    }
}
