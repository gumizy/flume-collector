package com.datacloudsec.source.kafka;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelSelector;
import com.datacloudsec.core.SourceRunner;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.channel.MemoryChannel;
import com.datacloudsec.core.channel.ReplicatingChannelSelector;
import com.datacloudsec.core.conf.Configurables;
import com.datacloudsec.core.source.PollableSourceRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static com.datacloudsec.config.conf.BasicConfigurationConstants.CONFIG_HOST;
import static com.datacloudsec.source.kafka.KafkaSourceConstants.BOOTSTRAP_SERVERS_CONFIG;
import static com.datacloudsec.source.kafka.KafkaSourceConstants.TOPIC_CONFIG;

public class KafkaSourceTest {

    private static KafkaSource kafkaSource;

    private static Channel channel;

    @BeforeClass
    public static void setUpClass() throws Exception {

        kafkaSource = new KafkaSource();
        channel = new MemoryChannel();
        configureSourceAndChannel(kafkaSource, channel);
        channel.start();

    }

    private static void configureSourceAndChannel(KafkaSource source, Channel channel) {
        Context channelContext = new Context();
        channelContext.put("capacity", "100");
        Configurables.configure(channel, channelContext);

        Context context = new Context();
        context.put(TOPIC_CONFIG, "event");
        context.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.101.61:9092");
        context.put(CONFIG_HOST, "192.168.101.61");
        Configurables.configure(source, context);

        ChannelSelector rcs1 = new ReplicatingChannelSelector();
        rcs1.setChannels(Collections.singletonList(channel));

        source.setChannelProcessor(new ChannelProcessor(rcs1));
    }



    @Before
    public void setUp() {

    }

    @Test
    public void testSimple() throws IOException, InterruptedException {
        SourceRunner runner = new PollableSourceRunner();
        runner.setSource(kafkaSource);
        runner.start();

        Thread.sleep(50000);
        Event take = channel.take();
        System.out.println(take.valueMapToJSONString());

        kafkaSource.stop();
        channel.stop();
    }

}
