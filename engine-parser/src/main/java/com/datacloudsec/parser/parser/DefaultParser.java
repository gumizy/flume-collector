package com.datacloudsec.parser.parser;

import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.parser.event.EventDecodeRule;
import com.datacloudsec.core.Parser;
import com.datacloudsec.parser.AbstractParserBuilder;

import java.util.regex.Matcher;

/**
 * DefaultParser 默认解析器
 */
public class DefaultParser extends AbstractParserBuilder {

    @Override
    protected void setEventField(String sendHost, Event event, Matcher matcher) {
        super.setEventField(sendHost, event, matcher, true);

    }

    public static class Builder extends AbstractBuilder {

        @Override
        public Parser build(EventDecodeRule parseRule) {
            return super.build(new DefaultParser(), parseRule);
        }
    }
}
