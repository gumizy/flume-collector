package com.datacloudsec.source.http;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.core.ChannelException;
import com.datacloudsec.core.EventDrivenSource;
import com.datacloudsec.core.conf.Configurable;
import com.datacloudsec.core.instrumentation.SourceCounter;
import com.datacloudsec.core.parser.PollableParserRunner;
import com.datacloudsec.core.source.AbstractSource;
import com.datacloudsec.core.tools.BeanConfigurator;
import com.datacloudsec.parser.EventParserProcessor;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.datacloudsec.source.http.HttpSourceConfigurationConstants.CONTEXT_PATH;
import static com.datacloudsec.source.http.HttpSourceConfigurationConstants.SERVELT_PATH;

public class HttpSource extends AbstractSource implements EventDrivenSource, Configurable {

    private static final Logger LOG = LoggerFactory.getLogger(HttpSource.class);
    private volatile Integer port;
    private volatile Server srv;
    private volatile String host;
    private HttpSourceHandler handler;
    private SourceCounter sourceCounter;

    private Context sourceContext;

    @Override
    public void configure(Context context) {
        sourceContext = context;
        try {
            port = context.getInteger(HttpSourceConfigurationConstants.CONFIG_PORT);
            host = context.getString(HttpSourceConfigurationConstants.CONFIG_BIND, HttpSourceConfigurationConstants.DEFAULT_BIND);

            Preconditions.checkState(host != null && !host.isEmpty(), "HttpSource hostname specified is empty");
            Preconditions.checkNotNull(port, "HttpSource requires a port number to be" + " specified");

            String handlerClassName = context.getString(HttpSourceConfigurationConstants.CONFIG_HANDLER, HttpSourceConfigurationConstants.DEFAULT_HANDLER).trim();

            @SuppressWarnings("unchecked") Class<? extends HttpSourceHandler> clazz = (Class<? extends HttpSourceHandler>) Class.forName(handlerClassName);
            handler = clazz.getDeclaredConstructor().newInstance();

            handler.configure(new Context());

            parserProcessor = new EventParserProcessor(this);
            parserProcessor.configure(context);
        } catch (ClassNotFoundException ex) {
            LOG.error("Error while configuring HttpSource. Exception follows.", ex);
            Throwables.propagate(ex);
        } catch (ClassCastException ex) {
            LOG.error("Deserializer is not an instance of HttpSourceHandler. Deserializer must implement HttpSourceHandler.");
            Throwables.propagate(ex);
        } catch (Exception ex) {
            LOG.error("Error configuring HttpSource!", ex);
            Throwables.propagate(ex);
        }
        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
    }

    @Override
    public void start() {
        LOG.info("##########################HttpSource listen at {}:{}", host, port);
        Preconditions.checkState(srv == null, "Running HTTP Server found in source: " + getName() + " before I started one." + "Will not attempt to start.");
        QueuedThreadPool threadPool = new QueuedThreadPool();
        if (sourceContext.getSubProperties("QueuedThreadPool.").size() > 0) {
            BeanConfigurator.setConfigurationFields(threadPool, sourceContext);
        }
        srv = new Server(threadPool);

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.addCustomizer(new SecureRequestCustomizer());

        BeanConfigurator.setConfigurationFields(httpConfiguration, sourceContext);
        ServerConnector connector = new ServerConnector(srv, new HttpConnectionFactory(httpConfiguration));

        connector.setPort(port);
        connector.setHost(host);
        connector.setReuseAddress(true);

        BeanConfigurator.setConfigurationFields(connector, sourceContext);

        srv.addConnector(connector);

        try {
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath(CONTEXT_PATH);
            srv.setHandler(context);

            context.addServlet(new ServletHolder(new HttpServlet()), SERVELT_PATH);
            srv.start();
        } catch (Exception ex) {
            LOG.error("Error while starting HttpSource. Exception follows.", ex);
            Throwables.propagate(ex);
        }
        Preconditions.checkArgument(srv.isRunning());

        parserRunner = new PollableParserRunner();
        parserRunner.setPolicy(parserProcessor);
        parserRunner.start();

        sourceCounter.start();
        super.start();
    }

    @Override
    public void stop() {
        try {
            srv.stop();
            srv.join();
            srv = null;
        } catch (Exception ex) {
            LOG.error("Error while stopping HttpSource. Exception follows.", ex);
        }
        sourceCounter.stop();
        parserRunner.stop();
        LOG.info("Http source {} stopped. Metrics: {}", getName(), sourceCounter);
    }

    @Override
    public SourceCounter getSourceCounter() {
        return sourceCounter;
    }

    private class HttpServlet extends javax.servlet.http.HttpServlet {

        private static final long serialVersionUID = 4891924863218790341L;

        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
            List<SourceMessage> messages;
            try {
                messages = handler.getMessage(request);
                sourceCounter.addToEventReceivedCount(messages.size());
            } catch (HttpBadRequestException ex) {
                LOG.warn("Received bad request from client. ", ex);
                sourceCounter.incrementEventReadFail();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request from client. " + ex.getMessage());
                return;
            } catch (Exception ex) {
                LOG.warn("Deserializer threw unexpected exception. ", ex);
                sourceCounter.incrementEventReadFail();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Deserializer threw unexpected exception. " + ex.getMessage());
                return;
            }
            sourceCounter.incrementAppendBatchReceivedCount();
            try {
                parserProcessor.puts(messages);
            } catch (ChannelException ex) {
                LOG.warn("Error appending event to channel. " + "Channel might be full. Consider increasing the channel " + "capacity or make sure the sinks perform faster.", ex);
                sourceCounter.incrementChannelWriteFail();
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Error appending event to channel. Channel might be full." + ex.getMessage());
                return;
            } catch (Exception ex) {
                LOG.warn("Unexpected error appending event to channel. ", ex);
                sourceCounter.incrementGenericProcessingFail();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error while appending event to channel. " + ex.getMessage());
                return;
            }
            response.setCharacterEncoding(request.getCharacterEncoding());
            response.setStatus(HttpServletResponse.SC_OK);
            response.flushBuffer();
            sourceCounter.addToEventAcceptedCount(messages.size());
        }

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            doPost(request, response);
        }
    }
}
