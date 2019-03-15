package com.datacloudsec.config.conf.parser;

import com.datacloudsec.config.conf.ComponentWithClassName;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Date 2019/1/18 16:18
 */
public enum ParserType implements ComponentWithClassName {
    OTHER(null),
    DEFAULTPARSER("com.datacloudsec.parser.parser.DefaultParser$Builder"),
    JSONPARSER("com.datacloudsec.parser.parser.JsonParser$Builder"),
    KVDECOMPOSEPARSER("com.datacloudsec.parser.parser.KvDecomposeParser$Builder"),
    KVREGULARPARSER("com.datacloudsec.parser.parser.KvRegularParser$Builder");

    public static Map<Integer, String> parserTransfer = Maps.newHashMap();

    static {
        parserTransfer.put(1, "DEFAULTPARSER");
        parserTransfer.put(2, "KVDECOMPOSEPARSER");
        parserTransfer.put(3, "KVREGULARPARSER");
        parserTransfer.put(4, "JSONPARSER");
    }

    private final String parserClassName;

    ParserType(String parserClassName) {
        this.parserClassName = parserClassName;
    }

    @Override
    public String getClassName() {
        return parserClassName;
    }
}
