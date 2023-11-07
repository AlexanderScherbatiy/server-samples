package appjavadb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    static final String DB_URL = System.getProperty("db.url", "jdbc:hsqldb:file:testdb/test");

    static final String DB_USER = System.getProperty("db.user", "TEST");

    static final String DB_PASS = System.getProperty("db.pass", "TEST");

    static final String DB_TABLE = System.getProperty("db.table", "TEST_TABLE");

    static final int DB_RECORDS = Integer.parseInt(System.getProperty("db.records", "10"));

    static final int DB_THREADS = Integer.parseInt(System.getProperty("db.threads", "20"));

    static final int DB_WRITERS = Integer.parseInt(System.getProperty("db.writers", "10"));

    static final int DB_READ_ITERATIONS = Integer.parseInt(System.getProperty("db.reads", "10"));

    static final int DB_WRITE_ITERATIONS = Integer.parseInt(System.getProperty("db.writes", "10"));

    private static boolean DEBUG_LOG = Boolean.getBoolean("db.debug.log");

    private static boolean DEBUG_RECORDS = Boolean.getBoolean("db.debug.records");

    static void readDB(String table) throws SQLException {
        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);
            readDB(connection, table);
            connection.commit();
        }
    }

    static void readDB(Connection connection, String table) throws SQLException {
        debugRecords("read table: %s%n", table);
        String select = "SELECT * from " + table;
        try (Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(select);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();

            String[] columns = new String[count];

            for (int i = 0; i < count; i++) {
                columns[i] = metaData.getColumnName(i + 1);
            }

            while (resultSet.next()) {
                List<Object> values = new ArrayList<>();
                for (String column : columns) {
                    Object obj = resultSet.getObject(column);
                    values.add(String.format("%s: %s", column, obj));
                }
                debugRecords("read: %s%n", values);
            }
        }
    }

    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    static String getDBNamePath(String dbURL) {
        String prefix = "hsqldb:file:";
        int beginIndex = dbURL.indexOf(prefix);
        if (beginIndex == -1) {
            throw new RuntimeException("Unable to get db name path from url: %s%n" + dbURL);
        }
        beginIndex += prefix.length();

        int endIndex = dbURL.indexOf(';', beginIndex);
        if (endIndex == -1) {
            endIndex = dbURL.length();
        }
        return dbURL.substring(beginIndex, endIndex);
    }

    static void debug(String format, Object... args) {
        debug(DEBUG_LOG, format, args);
    }

    static void debugRecords(String format, Object... args) {
        debug(DEBUG_RECORDS, format, args);
    }

    static void debug(boolean debug, String format, Object... args) {
        if (debug) {
            System.out.printf(format, args);
        }
    }
}
