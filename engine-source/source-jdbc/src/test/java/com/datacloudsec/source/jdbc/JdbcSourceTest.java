package com.datacloudsec.source.jdbc;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.EventDeliveryException;
import com.datacloudsec.core.Channel;
import com.datacloudsec.core.ChannelSelector;
import com.datacloudsec.core.channel.ChannelProcessor;
import com.datacloudsec.core.channel.MemoryChannel;
import com.datacloudsec.core.channel.ReplicatingChannelSelector;
import com.datacloudsec.core.conf.Configurables;
import com.datacloudsec.core.source.PollableSourceRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.datacloudsec.source.jdbc.JdbcSourceConstants.*;

public class JdbcSourceTest extends Assert {

    String connection_url = "jdbc:mysql://127.0.0.1:3306/test";
    Connection connection = null;

    @Before
    public void setup() {
        try {
            connection = DriverManager.getConnection(connection_url, "root", "root");

            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS audit_data_table;");
            statement.execute("CREATE TABLE audit_data_table (id INTEGER, return_code BIGINT, name VARCHAR(50), time DATETIME);");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basic() throws SQLException, InterruptedException, EventDeliveryException, IOException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO audit_data_table VALUES (1, 48,  'name1','2017-03-02 15:22:22');");
        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:25');");
        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:26');");
        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:27');");
//        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:28');");
//        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:31');");
//        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:32');");
//        statement.execute("INSERT INTO audit_data_table VALUES (3, 48,  'name3','2017-03-02 15:22:38');");
        statement.close();

        Context context = new Context();
        context.put(CONNECTION_DRIVER_PARAM, "com.mysql.jdbc.Driver");
        context.put(CONNECTION_URL_PARAM, connection_url);
        context.put(USERNAME_PARAM, "root");
        context.put(PASSWORD_PARAM, "root");
        context.put(TABLE_NAME_PARAM, " audit_data_table");
//        context.put(COLUMN_TO_COMMIT_PARAM, "ID");
//        context.put(TYPE_COLUMN_TO_COMMIT_PARAM, "numeric");
//        context.put(COLUMN_TO_COMMIT_PARAM, "time");
//        context.put(TYPE_COLUMN_TO_COMMIT_PARAM, "TIMESTAMP");
        context.put(COLUMN_TO_COMMIT_PARAM, "name");
        context.put(TYPE_COLUMN_TO_COMMIT_PARAM, "string");

        JdbcSource source = new JdbcSource();
        source.configure(context);

        Map<String, String> channelContext = new HashMap<String, String>();
        channelContext.put("capacity", "100");
        channelContext.put("keep-alive", "0"); // for faster tests
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context(channelContext));

        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(Collections.singletonList(channel));
        ChannelProcessor chp = new ChannelProcessor(rcs);
        source.setChannelProcessor(chp);

        PollableSourceRunner runner = new PollableSourceRunner();
        runner.setSource(source);
        runner.start();

        Thread.sleep(10000000);

//        Event event = channel.take();
//
//        runner.stop();
//
//        //Check content of committing file
//        FileReader in = new FileReader(COMMITTING_FILE_PATH_DEFAULT);
//        char[] in_chars = new char[50];
//        in.read(in_chars);
//        in.close();
//        String committed_value_from_file = new String(in_chars).trim();
//        Assert.assertEquals("3", committed_value_from_file);

    }

    @Test
    public void sameDelta() throws SQLException, InterruptedException, EventDeliveryException, IOException {

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO audit_data_table VALUES (1, 48, 'name1');");
        statement.execute("INSERT INTO audit_data_table VALUES (2, 48, 'name2');");
        statement.close();

        Context context = new Context();
        context.put(MINIMUM_BATCH_TIME_PARAM, "100");
        context.put(CONNECTION_DRIVER_PARAM, "org.hsqldb.jdbc.JDBCDriver");
        context.put(CONNECTION_URL_PARAM, connection_url);
        context.put(USERNAME_PARAM, "SA");
        context.put(PASSWORD_PARAM, "");
        context.put(TABLE_NAME_PARAM, " audit_data_table");
        context.put(COLUMN_TO_COMMIT_PARAM, "ID");
        context.put(TYPE_COLUMN_TO_COMMIT_PARAM, "numeric");

        JdbcSource source = new JdbcSource();
        source.configure(context);

        Map<String, String> channelContext = new HashMap<String, String>();
        channelContext.put("capacity", "100");
        channelContext.put("keep-alive", "0"); // for faster tests
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context(channelContext));

        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(Collections.singletonList(channel));
        ChannelProcessor chp = new ChannelProcessor(rcs);
        source.setChannelProcessor(chp);

        PollableSourceRunner runner = new PollableSourceRunner();
        runner.setSource(source);
        runner.start();

        Thread.sleep(500);

        Event event = channel.take();

        assertEquals(2, source.getCounters().getEventReceivedCount());
        assertEquals(2, source.getCounters().getEventAcceptedCount());

        statement = connection.createStatement();
        statement.execute("INSERT INTO audit_data_table VALUES (1, 48, 'should not be loaded');");
        statement.execute("INSERT INTO audit_data_table VALUES (2, 48, 'same delta, anyway it should be loaded');");
        statement.execute("INSERT INTO audit_data_table VALUES (3, 48, 'greater delta, it should be loaded');");
        statement.close();

        Thread.sleep(500);

        assertEquals(4, source.getCounters().getEventReceivedCount());
        assertEquals(4, source.getCounters().getEventAcceptedCount());

        runner.stop();

        //Check content of committing file
        FileReader in = new FileReader(COMMITTING_FILE_PATH_DEFAULT);
        char[] in_chars = new char[50];
        in.read(in_chars);
        in.close();
        String committed_value_from_file = new String(in_chars).trim();
        Assert.assertEquals("3", committed_value_from_file);

    }

    @Test
    public void rollBack() throws SQLException, InterruptedException, EventDeliveryException, IOException {

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO audit_data_table VALUES (1, 48, 'name1');");
        statement.execute("INSERT INTO audit_data_table VALUES (2, 48, 'name2');");
        statement.execute("INSERT INTO audit_data_table VALUES (3, 48, 'name3');");
        statement.close();

        Context context = new Context();
        context.put(CONNECTION_DRIVER_PARAM, "org.hsqldb.jdbc.JDBCDriver");
        context.put(CONNECTION_URL_PARAM, connection_url);
        context.put(USERNAME_PARAM, "SA");
        context.put(PASSWORD_PARAM, "");
        context.put(TABLE_NAME_PARAM, " audit_data_table");
        context.put(COLUMN_TO_COMMIT_PARAM, "ID");
        context.put(TYPE_COLUMN_TO_COMMIT_PARAM, "numeric");
        context.put(BATCH_SIZE_PARAM, "2");
        context.put(MINIMUM_BATCH_TIME_PARAM, "100");

        JdbcSource source = new JdbcSource();
        source.configure(context);

        Map<String, String> channelContext = new HashMap<String, String>();
        channelContext.put("capacity", "2");
        channelContext.put("transactionCapacity", "2");
        channelContext.put("keep-alive", "0"); // for faster tests
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context(channelContext));

        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(Collections.singletonList(channel));
        ChannelProcessor chp = new ChannelProcessor(rcs);
        source.setChannelProcessor(chp);

        PollableSourceRunner runner = new PollableSourceRunner();
        runner.setSource(source);
        runner.start();

        Thread.sleep(500);

        // During first transaction source will try to put events but will get:
        // org.apache.flume.ChannelFullException: Space for commit to queue couldn't be acquired...
        // Because channel capacity is not enough
        // That will cause roll backs

        Event event = channel.take();

        Thread.sleep(500);

        runner.stop();

        //Check content of committing file
        FileReader in = new FileReader(COMMITTING_FILE_PATH_DEFAULT);
        char[] in_chars = new char[50];
        in.read(in_chars);
        in.close();
        String committed_value_from_file = new String(in_chars).trim();
        Assert.assertEquals("3", committed_value_from_file);

    }

    @After
    public void cleanUp() {
        new File(COMMITTING_FILE_PATH_DEFAULT).delete();

        try {
            connection.close();
        } catch (SQLException e) {
        }
    }
}
