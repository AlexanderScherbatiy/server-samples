package appjavadb;

import java.sql.*;

public class DBTransactionSample {

    private static final String DB_URL = System.getProperty("db.url");

    private static final String DB_USER = System.getProperty("db.user");

    private static final String DB_PASS = System.getProperty("db.pass");

    public static void main(String[] args) throws Exception {

        debug("DB url: %s, user: %s%n", DB_URL, DB_USER);

        debug("Init:%n");
        init();
        read();

        debug("Transaction disabled:%n");
        insert(true);
        read();

        init();
        debug("Transaction enabled:%n");
        insert(false);
        read();
    }

    private static void init() throws SQLException {

        try (Connection connection = createConnection()) {
            try (Statement stmt = connection.createStatement()) {

                stmt.execute("DROP TABLE IF EXISTS TNAME");
                stmt.execute("DROP TABLE IF EXISTS TVALUE");

                stmt.execute("CREATE TABLE TNAME (ID BIGINT IDENTITY NOT NULL PRIMARY KEY, name VARCHAR(255))");
                stmt.execute("CREATE TABLE TVALUE (ID BIGINT IDENTITY NOT NULL PRIMARY KEY, value BIGINT)");

                stmt.execute("INSERT INTO TNAME VALUES (1, 'name_1')");
                stmt.execute("INSERT INTO TNAME VALUES (2, 'name_2')");
                stmt.execute("INSERT INTO TNAME VALUES (3, 'name_3')");

                stmt.execute("INSERT INTO TVALUE VALUES (1, 1)");
                stmt.execute("INSERT INTO TVALUE VALUES (2, 2)");
                stmt.execute("INSERT INTO TVALUE VALUES (3, 3)");
            }
        }
    }

    private static void insert(boolean autoCommit) throws SQLException {
        try (Connection connection = createConnection()) {
            connection.setAutoCommit(autoCommit);

            String update1 = "update TNAME set name=? where id=?";
            String update2 = "update TVALUE set value=? where id=?";

            try (PreparedStatement preparedStatement1 = connection.prepareStatement(update1);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(update2)) {

                preparedStatement1.setString(1, "name_111");
                preparedStatement1.setLong(2, 1);
                preparedStatement2.setLong(1, 111);
                preparedStatement2.setLong(2, 1);

                preparedStatement1.executeUpdate();
                preparedStatement2.executeUpdate();

                preparedStatement1.setString(1, "name_222");
                preparedStatement1.setLong(2, 2);
                preparedStatement2.setLong(1, 222);
                preparedStatement2.setLong(2, 2);

                preparedStatement1.executeUpdate();
                preparedStatement2.executeUpdate();

                preparedStatement1.setString(1, "name_333");
                preparedStatement1.setLong(2, 3);
                preparedStatement2.setLong(1, 333);
                preparedStatement2.setLong(2, 3);

                preparedStatement1.executeUpdate();
                preparedStatement2.executeUpdate();
            }

            connection.rollback();
        }
    }

    private static void read() throws SQLException {
        try (Connection connection = createConnection()) {
            readDB(connection, "TNAME", "name");
            readDB(connection, "TVALUE", "value");
        }
    }

    private static void readDB(Connection connection, String table, String column) throws SQLException {
        String select = "SELECT * from " + table;
        try (Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(select);
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                Object obj = resultSet.getObject(column);
                debug("read id: %d, %s: %s%n", id, column, obj);
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