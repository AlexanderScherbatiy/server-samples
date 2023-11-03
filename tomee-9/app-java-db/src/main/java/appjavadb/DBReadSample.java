package appjavadb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBReadSample {

    private static final String DB_URL = System.getProperty("db.url");

    private static final String DB_USER = System.getProperty("db.user");

    private static final String DB_PASS = System.getProperty("db.pass");

    private static final String DB_TABLE = System.getProperty("db.table");

    public static void main(String[] args) throws Exception {
        try (Connection connection = createConnection()) {
            readDB(connection, DB_TABLE);
        }
    }

    private static void readDB(Connection connection, String table) throws SQLException {
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
                debug("read: %s%n", values);
            }
        }
    }

    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static void debug(String format, Object... args) {
        System.out.printf(format, args);
    }
}
