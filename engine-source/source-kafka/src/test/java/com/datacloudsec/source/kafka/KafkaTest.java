package com.datacloudsec.source.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @Author gumizy
 * @Date 2018/11/19 9:25
 */
public class KafkaTest {

    @Test
    public void productorTest() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.101.61:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = null;

        try {
            producer = new KafkaProducer(properties);
            int i = 0;
            while (true) {
                String msg = "<406>Jul 27 09:43:39 dpi SESSION: SerialNum=\"0023210123456789123\" GenTime=\"2018-07-27 09:43:39\" Category=SESSION STIME=1536580369 ETIME=1536580399 IN=eth3 OUT=eth2 SMAC=d4:ae:52:bd:a5:d3 DMAC=50:7b:9d:e9:d6:8d SIP=192.168.101.92 DIP=192.168.101.145 SPORT=36986 DPORT=80 Proto=http TransProto=tcp RX=720 TX=596 RXPKT=11 TXPKT=11 Method=GET Url=/aaa.html Host=192.168.101.145 RetCode=404 AppName=\"广发证券\" ClassName=\"证券软件\" CharacterName=\"PING响应数据包\" CharacterLevel=\"2\" FileName=\"dl_exe.pdf\" File=\"test.txt\" EventName=\"疑似PHP远程文件包含攻击\" SecName=\"系统应用攻击\" Content=\"Host=www.mafengwo.cn;URL=http:/www.mafengwo.cn/ajax/ajax_msg.php?a=msgdisplay;URL长度=52;Http协议头长度=1963;访问文件=ajax_msg.php;请求体内容=;\" Log_Count=\"1\"";
                producer.send(new ProducerRecord<>("event","dcsec", msg));
                System.out.println("Sent:" + msg);
                i++;
                Thread.sleep(1000);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }
    }

    @Test
    public void consumerTest() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", "192.168.101.61:9092");
        properties.put("group.id", "event-group-2");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        org.apache.kafka.clients.consumer.KafkaConsumer<String, String> kafkaConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList("event"));
        while (true) {
            Duration duration = Duration.ofMillis(1000);
            ConsumerRecords<String, String> records = kafkaConsumer.poll(duration);
            for (ConsumerRecord<String, String> record : records) {
//                System.out.printf("offset = %d, value = %s", record.offset(), record.value());
                System.out.println(record.value());
            }
        }
    }
}
