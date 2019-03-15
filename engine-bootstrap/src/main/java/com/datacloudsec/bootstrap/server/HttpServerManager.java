package com.datacloudsec.bootstrap.server;

import com.datacloudsec.bootstrap.server.core.Handler;
import com.datacloudsec.bootstrap.server.core.HttpRequest;
import com.datacloudsec.bootstrap.server.core.HttpResponse;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static com.datacloudsec.bootstrap.server.HttpServerConstants.CONTEXT_PATH;
import static com.datacloudsec.bootstrap.server.HttpServerConstants.PORT;

/**
 * HttpServer Manager
 */
public class HttpServerManager {

    private static Logger logger = LoggerFactory.getLogger(HttpServerManager.class);


    /**
     * 启动HttpServer
     *
     * @param serverPort
     */
    public static void start(Integer serverPort) {

        PORT = serverPort;
        logger.info("##########################  start http server port :" + PORT);

        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver = null;
        try {
            httpserver = provider.createHttpServer(new InetSocketAddress(PORT), 100);
        } catch (IOException e) {
            logger.error("HttpServerManager start error:", e);
            System.exit(-1);
        }
        try {
            httpserver.createContext(CONTEXT_PATH, httpExchange -> {
                // request & response
                final HttpRequest request = new HttpRequest(httpExchange);
                final HttpResponse response = new HttpResponse(httpExchange);
                Handler handler = HttpServerContext.getHandler(request.getRequestURI().getPath());
                if (handler != null) {
                    handler.doService(request, response);
                } else {
                    response.write(HttpURLConnection.HTTP_NOT_FOUND, "404 - Http route cannot be located!");
                }
            });

            httpserver.setExecutor(Executors.newCachedThreadPool());

            httpserver.start();

            logger.info("http com.datacloudsec.bootstrap.server [start] success!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
