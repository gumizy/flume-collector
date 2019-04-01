package com.datacloudsec.core.channel;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelSelector;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.interceptor.Interceptor;
import com.datacloudsec.core.interceptor.InterceptorBuilderFactory;
import com.datacloudsec.core.interceptor.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * ChannelProcessor
 */
public class ChannelProcessor implements Configurable {

    private Logger logger = LoggerFactory.getLogger(ChannelProcessor.class);

    private final ChannelSelector selector;

    private final InterceptorChain interceptorChain;

    public ChannelProcessor(ChannelSelector selector) {
        this.selector = selector;
        this.interceptorChain = new InterceptorChain();
    }

    public void initialize() {
        interceptorChain.initialize();
    }

    public void close() {
        interceptorChain.close();
    }

    @Override
    public void configure(Context context) {
        configureInterceptors(context);
    }

    private void configureInterceptors(Context context) {
        List<Interceptor> interceptors = new LinkedList<>();
        String interceptorListStr = context.getString("interceptors", "");
        if (interceptorListStr.isEmpty()) {
            return;
        }
        Map<String, String> parameters = context.getParameters();
        Set<String> keySet = parameters.keySet();
        Map<String, Map<String, String>> m = recursion(keySet, parameters);

        Iterator<String> iterator = m.keySet().iterator();
        Map<String, Interceptor> inters = new HashMap<String, Interceptor>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Map<String, String> map = m.get(key);

            String type = map.get("type");
            if (type == null) {
                logger.error("Type not specified for interceptor , with configuration : {}" + context);
                throw new CollectorEngineException("Interceptor.Type not specified");
            }
            Interceptor.Builder builder;
            try {
                builder = InterceptorBuilderFactory.newInstance(type);
                builder.configure(new Context(map));
                Interceptor build = builder.build();
                String[] split = key.split("\\.");
                inters.put(split[1], build);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        String[] types = interceptorListStr.split("\\s+");
        for (String t : types) {
            interceptors.add(inters.get(t));
        }
        interceptorChain.setInterceptors(interceptors);
    }

    private Map<String, Map<String, String>> recursion(Set<String> values, Map<String, String> m) {
        Map<String, Map<String, String>> map = new HashMap<>();
        for (String value : values) {
            if (value.contains(".")) {
                String keyPrefix = value.substring(0, value.lastIndexOf("."));
                String keySubfix = value.substring(value.lastIndexOf(".") + 1);
                Map<String, String> contextMap = map.computeIfAbsent(keyPrefix, k -> new HashMap<>());
                contextMap.put(keySubfix, m.get(value));
            }
        }
        return map;
    }

    public ChannelSelector getSelector() {
        return selector;
    }

    public void processEventBatch(List<Event> events) {
        events = interceptorChain.intercept(events);
        //如果日志的级别为空，设置级别为信息
        if (events != null) {
            for (Event event : events) {
                setEventDefaultValue(event);
            }
            List<Channel> requiredChannels = selector.getRequiredChannels();
            for (Channel reqChannel : requiredChannels) {
                reqChannel.puts(events);
            }
        }
    }

    public void processEvent(Event event, boolean normalization) {
        if (normalization) {
            event = interceptorChain.intercept(event);
        }
        if (event == null) {
            return;
        }
        if (normalization) {
            setEventDefaultValue(event);
        }
        List<Channel> requiredChannels = selector.getRequiredChannels();
        if (requiredChannels != null) {
            for (Channel reqChannel : requiredChannels) {
                try {
                    reqChannel.put(event);
                } catch (Throwable t) {
                    logger.error("Error while writing to required channel: " + reqChannel, t);
                }
            }
        }
    }

    private void setEventDefaultValue(Event event) {
        Integer level = event.getEventLevel();
        if (level == null) {
            event.setEventLevel(-1);
        }
    }
}
