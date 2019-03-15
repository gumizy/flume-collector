package com.datacloudsec.source.jdbc.reader;

import com.datacloudsec.config.CollectorEngineException;
import com.datacloudsec.config.Context;
import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.config.conf.ConfigurationException;
import com.datacloudsec.config.conf.source.JsonSourceMessage;
import com.datacloudsec.core.conf.Configurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.datacloudsec.config.conf.source.SourceMessageConstants.*;
import static com.datacloudsec.source.jdbc.JdbcSourceConstants.*;

public class ReliableJdbcEventReader implements Configurable {

    private static final Logger LOG = LoggerFactory.getLogger(ReliableJdbcEventReader.class);
    private State state;
    private String connection_driver = CONNECTION_DRIVER_DEFAULT;
    private String connection_url = CONNECTION_URL_DEFAULT;
    private String connection_user = USERNAME_DEFAULT;
    private String connection_password = PASSWORD_DEFAULT;
    private String tableName = null;
    private String columnToCommit = null;
    protected String committed_value = null;
    private ColumnType type_column_to_commit = TYPE_COLUMN_TO_COMMIT_DEFUALT;
    private String committed_value_to_load = null;
    private String configuredQuery = null;
    private String committing_file_path = COMMITTING_FILE_PATH_DEFAULT;
    private File committing_file = null;
    private boolean scaleAwareNumeric = false;
    private boolean expandBigFloats = false;
    private Connection connection = null;
    private ResultSet resultSet = null;
    private Statement statement = null;
    private int batch_size = BATCH_SIZE_DEFAULT;

    protected String last_value = null;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public ReliableJdbcEventReader() {
        initialize();
    }

    @Override
    public void configure(Context context) {
        initialize();
        try {
            String value = context.getString(BATCH_SIZE_PARAM);
            if (value != null) {
                batch_size = Integer.parseInt(value);
            }
        } catch (Exception e) {
            throw new CollectorEngineException("Configured value for " + BATCH_SIZE_PARAM + " is not a number", e);
        }

        tableName = context.getString(TABLE_NAME_PARAM);
        configuredQuery = getConfiguredQuery(context.getString(QUERY_PARAM), context.getString(QUERY_PATH_PARAM));
        if (configuredQuery == null && tableName == null)
            throw new ConfigurationException("Table name or query needs to be configured with " + TABLE_NAME_PARAM);

        columnToCommit = context.getString(COLUMN_TO_COMMIT_PARAM);

        String conf_type_column = context.getString(TYPE_COLUMN_TO_COMMIT_PARAM);
        if (conf_type_column != null) {
            try {
                type_column_to_commit = ColumnType.valueOf(conf_type_column.toUpperCase());
            } catch (Exception e) {
                throw new CollectorEngineException("Configuration value for " + TYPE_COLUMN_TO_COMMIT_PARAM + " is not valid, it must be one of: " + Arrays.asList(ColumnType.values()));
            }
        }

        connection_driver = context.getString(CONNECTION_DRIVER_PARAM, CONNECTION_DRIVER_DEFAULT);
        try {
            Class.forName(connection_driver);
        } catch (ClassNotFoundException e) {
            throw new CollectorEngineException("Configured class for JDBC driver (" + connection_driver + ") has not been found in the classpath");
        }

        connection_user = context.getString(USERNAME_PARAM, USERNAME_DEFAULT);
        connection_password = context.getString(PASSWORD_PARAM, PASSWORD_DEFAULT);
        connection_url = context.getString(CONNECTION_URL_PARAM, CONNECTION_URL_DEFAULT);

        committing_file_path = context.getString(COMMITTING_FILE_PATH_PARAM, COMMITTING_FILE_PATH_DEFAULT);
        committing_file = new File(committing_file_path);

        scaleAwareNumeric = context.getBoolean(SCALE_AWARE_NUMERIC_PARAM, false);
        expandBigFloats = context.getBoolean(EXPAND_BIG_FLOATS_PARAM, false);

        if (columnToCommit != null) {
            loadLastCommittedValueFromFile();
            if (committed_value == null) {
                committed_value_to_load = context.getString(COMMITTED_VALUE_TO_LOAD_PARAM);
                committed_value = committed_value_to_load;
            }
        }
        state = State.CONFIGURED;
    }

    private void initialize() {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }

        last_value = null;

        state = State.INITIALIZED;
    }

    private String getConfiguredQuery(String query_string, String path_to_query_file) {
        if (state != State.INITIALIZED)
            throw new ConfigurationException(getClass().getSimpleName() + " is not initialized");

        //From configuration parameter
        if (query_string != null) return query_string;

        //Else, from file if path is configured
        if (path_to_query_file == null) return null;

        File query_file = new File(path_to_query_file);
        if (query_file.exists()) {
            try {
                FileReader in = new FileReader(query_file);

                char[] in_chars = new char[(int) query_file.length()];
                in.read(in_chars);
                in.close();

                return new String(in_chars).trim();
            } catch (Exception e) {
                throw new CollectorEngineException(e);
            }
        } else {
            throw new CollectorEngineException("File configured with " + QUERY_PATH_PARAM + " parameter does not exist");
        }
    }

    private void loadLastCommittedValueFromFile() {
        if (state != State.INITIALIZED)
            throw new ConfigurationException(getClass().getSimpleName() + " is not initialized");

        try {
            if (committing_file.exists()) {
                FileReader in = new FileReader(committing_file);
                char[] in_chars = new char[(int) committing_file.length()];
                in.read(in_chars);
                in.close();
                String value_from_file = new String(in_chars).trim();

                if (value_from_file.length() > 0) {
                    committed_value = value_from_file;

                    LOG.info("Last value loaded from file: " + committed_value);
                } else {
                    LOG.info("File for loading last value is empty");
                }
            } else {
                committing_file.createNewFile();

                LOG.info("File for storing last commited value has been created: " + committing_file.getAbsolutePath());
            }
            LOG.info("File for storing last path  " + committing_file.getAbsolutePath());

        } catch (IOException e) {
            throw new CollectorEngineException(e);
        }
    }

    public JsonSourceMessage readMessage(String host) throws IOException {
        if (state != State.CONFIGURED) {
            throw new ConfigurationException(getClass().getSimpleName() + " is not configured");
        }
        try {
            if (resultSet == null) {
                runQuery();
            }
            if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
                ResultSetMetaData metadata = resultSet.getMetaData();
                int columnCount = metadata.getColumnCount();

                JsonSourceMessage message = new JsonSourceMessage(host);
                message.setHeaders(PARSER_IDENTIFICATION, host + COLLECT_TYPE_SEPARATOR + COLLECT_TYPE_JDBC);

                for (int i = 1; i <= columnCount; i++) {
                    String name = metadata.getColumnName(i);
                    switch (metadata.getColumnType(i)) {
                        case Types.SMALLINT:
                        case Types.TINYINT:
                        case Types.INTEGER:
                            int resultInt = resultSet.getInt(i);
                            if (!resultSet.wasNull()) {
                                message.addProperty(name, resultInt);
                            } else {
                                message.addProperty(name, null);
                            }
                            break;
                        case Types.BIGINT:
                            long resultLong = resultSet.getLong(i);
                            if (!resultSet.wasNull()) {
                                message.addProperty(name, resultLong);
                            } else {
                                message.addProperty(name, null);
                            }
                            break;
                        case Types.BOOLEAN:
                            boolean resultBool = resultSet.getBoolean(i);
                            if (!resultSet.wasNull()) {
                                message.addProperty(name, resultBool);
                            } else {
                                message.addProperty(name, null);
                            }
                            break;
                        case Types.NUMERIC:
                            if (scaleAwareNumeric) {
                                if (metadata.getScale(i) == 0) {
                                    double resultDouble = resultSet.getDouble(i);
                                    if (!resultSet.wasNull()) {
                                        Number test = Math.round(resultDouble);
                                        message.addProperty(name, test);
                                    } else {
                                        message.addProperty(name, null);
                                    }
                                    break;
                                }
                            }
                            // No break!
                        case Types.DOUBLE:
                        case Types.FLOAT:
                            if (expandBigFloats) {
                                //如果使用getDouble（），则会丢失精度。例子：
                                //原始数字：100.1
                                //使用getDouble（）-->100.099999999924
                                //所以在输出中也会出现这种情况。
                                //使用bigdecimal的字符串构造函数时，不会丢失精度。
                                String floatString = resultSet.getString(i);
                                if (!resultSet.wasNull()) {
                                    message.addProperty(name, new BigDecimal(floatString));
                                } else {
                                    message.addProperty(name, floatString);
                                }
                            } else {
                                double resultDouble = resultSet.getDouble(i);
                                if (!resultSet.wasNull()) {
                                    message.addProperty(name, resultDouble);
                                } else {
                                    message.addProperty(name, null);
                                }
                            }
                            break;
                        case Types.TIMESTAMP:
                        case -101: //TIMESTAMP(3) WITH TIME ZONE
                        case -102: //TIMESTAMP(6) WITH LOCAL TIME ZONE
                            Timestamp timestamp = resultSet.getTimestamp(i);
                            if (!resultSet.wasNull()) {
                                message.addProperty(name, dateFormat.format(timestamp));
                            } else {
                                message.addProperty(name, null);
                            }
                            break;
                        default:
                            message.addProperty(name, resultSet.getString(i));
                            break;
                    }

                    if (columnToCommit != null && name.equalsIgnoreCase(columnToCommit)) {
                        last_value = resultSet.getString(i);
                    }
                }
                return message;
            } else {
                resultSet = null;

                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private void runQuery() throws SQLException {
        if (state != State.CONFIGURED)
            throw new ConfigurationException(getClass().getSimpleName() + " is not configured");

        if (statement != null) {
            statement.close();
        }
        connect();
        statement = connection.createStatement();
        String query = createQuery(committed_value);
        LOG.debug("Executing query: " + query);
        resultSet = statement.executeQuery(query);
    }

    public String createQuery(String committed_value) {
        if (state != State.CONFIGURED) {
            throw new ConfigurationException(getClass().getSimpleName() + " is not configured");
        }
        String value = "#{value}";
        String query;
        if (configuredQuery != null && columnToCommit != null && committed_value != null) {
            query = configuredQuery;
            if (query.contains(value)) {
                switch (type_column_to_commit) {
                    case NUMERIC:
                        query = query.replace(value, committed_value);
                        break;
                    case TIMESTAMP:
                        query = query.replace(value, "TIMESTAMP \'" + committed_value + "\'");
                        break;
                    default: //String
                        query = query.replace(value, "\'" + committed_value + "\'");
                        break;
                }
            }
        } else {
            query = "SELECT * FROM " + tableName;

            if (columnToCommit != null && committed_value != null) {
                query = query.concat(" WHERE " + columnToCommit + " > ");

                switch (type_column_to_commit) {
                    case NUMERIC:
                        query = query.concat(committed_value);
                        break;
                    case TIMESTAMP:
                        query = query.concat("TIMESTAMP \'" + committed_value + "\'");
                        break;
                    default: //String
                        query = query.concat("\'" + committed_value + "\'");
                        break;
                }
            }

            if (columnToCommit != null) {
                query = query.concat(" ORDER BY " + columnToCommit);
            }
        }

        return query;
    }

    private void connect() throws SQLException {
        if (state != State.CONFIGURED) {
            throw new ConfigurationException(getClass().getSimpleName() + " is not configured");
        }
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(connection_url, connection_user, connection_password);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    public List<SourceMessage> readMessages(String host) throws IOException {
        if (state != State.CONFIGURED) {
            throw new ConfigurationException(getClass().getSimpleName() + " is not configured");
        }
        int numberOfEventToRead = batch_size;
        LinkedList<SourceMessage> messages = new LinkedList<>();
        for (int i = 0; i < numberOfEventToRead; i++) {
            JsonSourceMessage event = readMessage(host);
            if (event != null) {
                LOG.trace("New event: " + event);
                messages.add(event);
            } else {
                LOG.debug("Number of messages returned: " + messages.size());
                return messages;
            }
        }
        LOG.debug("Number of messages returned: " + messages.size());
        return messages;
    }

    public void commit() throws IOException {
        if (state != State.CONFIGURED)
            throw new ConfigurationException(getClass().getSimpleName() + " is not configured");

        if (last_value == null) return;

        FileWriter out = new FileWriter(committing_file, false);
        out.write(last_value);
        out.close();

        committed_value = last_value;

        last_value = null;
    }

    public void rollback() {
        LOG.warn("Rolling back...");

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
            }
        }
        resultSet = null;
    }

    public void close() throws IOException {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Throwable e) {
        }
    }

}
