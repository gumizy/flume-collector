package com.datacloudsec.source.http;

import com.alibaba.fastjson.JSON;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.source.SourceLogMessage;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelSelector;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.channel.MemoryChannel;
import com.datacloudsec.core.channel.ReplicatingChannelSelector;
import com.datacloudsec.core.conf.Configurables;
import com.google.common.collect.Maps;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.*;

public class HttpSourceTest {

    private static HttpSource httpSource;

    private static Channel httpChannel;
    private static int httpPort;
    private CloseableHttpClient httpClient;
    private HttpPost postRequest;

    private static int findFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    private static Context getDefaultNonSecureContext(int port) throws IOException {
        Context ctx = new Context();
        ctx.put(HttpSourceConfigurationConstants.CONFIG_BIND, "0.0.0.0");
        ctx.put(HttpSourceConfigurationConstants.CONFIG_PORT, String.valueOf(port));
        ctx.put("QueuedThreadPool.MaxThreads", "100");
        return ctx;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        httpSource = new HttpSource();
        httpChannel = new MemoryChannel();
        httpPort = findFreePort();
        configureSourceAndChannel(httpSource, httpChannel, getDefaultNonSecureContext(httpPort));
        httpChannel.start();
        httpSource.start();
    }

    private static void configureSourceAndChannel(HttpSource source, Channel channel, Context context) {
        Context channelContext = new Context();
        channelContext.put("capacity", "100");
        Configurables.configure(channel, channelContext);
        Configurables.configure(source, context);

        ChannelSelector rcs1 = new ReplicatingChannelSelector();
        rcs1.setChannels(Collections.singletonList(channel));

        source.setChannelProcessor(new ChannelProcessor(rcs1));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        httpSource.stop();
        httpChannel.stop();
    }

    @Before
    public void setUp() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        httpClient = builder.build();
        postRequest = new HttpPost("http://0.0.0.0:" + httpPort);
    }

    @Test
    public void testSimple() throws IOException, InterruptedException {

        StringEntity input = new StringEntity("[{\"headers\":{\"a\": \"b\"},\"body\": \"random_body\"}," + "{\"headers\":{\"e\": \"f\"},\"body\": \"random_body2\"}]");
        //if we do not set the content type to JSON, the client will use
        //ISO-8859-1 as the charset. JSON standard does not support this.
        input.setContentType("application/json");
        postRequest.setEntity(input);

        HttpResponse response = httpClient.execute(postRequest);

        System.out.println(response.getStatusLine().getStatusCode());
        Thread.sleep(5000);
        Event take = httpChannel.take();
        System.out.println(take.valueMapToJSONString());
    }

    @Test
    public void testSingleEvent() throws Exception {
        StringEntity input = new StringEntity("[{\"headers\" : {\"a\": \"b\"},\"body\":" + " \"random_body\"}]");
        input.setContentType("application/json");
        postRequest.setEntity(input);

        httpClient.execute(postRequest);
        Thread.sleep(5000);
        Event take = httpChannel.take();
        System.out.println(take.valueMapToJSONString());
    }

    @Test
    public void testHttpsSourceNonHttpsClient() throws Exception {
        List<SourceLogMessage> events = new ArrayList<SourceLogMessage>();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            Map<String, String> input = Maps.newHashMap();
            for (int j = 0; j < 10; j++) {
                input.put(String.valueOf(i) + String.valueOf(j), String.valueOf(i));
            }
            input.put("MsgNum", String.valueOf(i));
            SourceLogMessage e = new SourceLogMessage("");
            e.setHeaders(input);
            e.setBody(String.valueOf(rand.nextGaussian()).getBytes("UTF-8"));
            events.add(e);
        }
        String json = JSON.toJSONString(events);
        HttpURLConnection httpURLConnection = null;
        try {
//            URL url = new URL("http://0.0.0.0:" + httpPort);
            URL url = new URL("http://127.0.0.1:5144");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.getOutputStream().write(json.getBytes());
            httpURLConnection.getResponseCode();
            Thread.sleep(5000);
            Event take = httpChannel.take();
            System.out.println(take.valueMapToJSONString());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
    }

}
