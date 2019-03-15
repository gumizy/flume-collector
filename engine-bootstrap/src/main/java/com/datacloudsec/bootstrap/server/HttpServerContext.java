package com.datacloudsec.bootstrap.server;

import com.datacloudsec.bootstrap.server.annotation.RequestMapping;
import com.datacloudsec.bootstrap.server.core.Handler;
import com.datacloudsec.bootstrap.server.core.HttpHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.CONTEXT_PATH;
import static com.datacloudsec.bootstrap.server.HttpServerConstants.PKG_BASE;

/**
 * Http Server Context全局对象封装类
 */
public class HttpServerContext {

    private static Logger logger = LoggerFactory.getLogger(HttpServerContext.class);


    private static Map<String, HttpHandler> contextMap = new HashMap<>();

    static {
        Set<Class<?>> classSet = new Reflections(PKG_BASE).getTypesAnnotatedWith(RequestMapping.class);
        for (Class c : classSet) {
            if (Handler.class.isAssignableFrom(c)) {
                RequestMapping annotation = (RequestMapping) c.getAnnotation(RequestMapping.class);
                try {
                    contextMap.put(CONTEXT_PATH + annotation.name(), (HttpHandler) c.newInstance());
                } catch (Exception e) {
                    logger.error("Http server context error ", e);
                    System.exit(-1);
                }
            }
        }
    }

    public static HttpHandler getHandler(String key) {
        return contextMap.get(key);
    }

}