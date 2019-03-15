package com.datacloudsec.parser.cache;

import com.datacloudsec.parser.ParserChain;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PraserProcessorCache
 */
public class PraserProcessorCache implements Serializable {

    private static Map<String, ParserChain> parserMap = new ConcurrentHashMap<>();
    private static Set<String> validateIpSet = Collections.synchronizedSet(new HashSet<>());

    public static void addParserChain(String parserIdentification, ParserChain parserChain) {
        parserMap.put(parserIdentification, parserChain);
    }

    public static ParserChain getParserChain(String parserIdentification) {
        return parserMap.get(parserIdentification);
    }

    public static Map<String, ParserChain> getParserMap() {
        return parserMap;
    }

    public static void clearAllParserChain() {
        parserMap.clear();
    }

    public static Set<String> getValidateIpSet() {
        return validateIpSet;
    }

    public static void setValidateIpSet(String ip) {
        if (StringUtils.isNotBlank(ip)) {
            validateIpSet.add(ip);
        }
    }


}
