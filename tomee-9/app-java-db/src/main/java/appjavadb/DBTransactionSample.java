package appjavadb;

import java.sql.*;

import static appjavadb.DBUtils.*;

public class DBTransactionSample {

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
            readDB(connection, "TNAME");
            readDB(connection, "TVALUE");
        }
    }
}