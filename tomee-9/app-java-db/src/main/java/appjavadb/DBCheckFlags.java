package appjavadb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static appjavadb.DBUtils.*;

public class DBCheckFlags {

    private static final String DB_COUNT = String.format("SELECT COUNT(*) FROM %s", DB_TABLE);

    private static final String DB_SELECT_FLAG_TRUE =
            String.format("SELECT COUNT(value) FROM %s WHERE flag = true", DB_TABLE);

    private static final String DB_SELECT_FLAG_FALSE =
            String.format("SELECT COUNT(value) FROM %s WHERE flag = false", DB_TABLE);

    public static void main(String[] args) throws Exception {

        System.out.printf("Table: %s%n", DB_TABLE);

        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_COUNT);
                rs.next();
                int count = rs.getInt(1);
                System.out.printf("Rows: %s%n", count);
            }

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_SELECT_FLAG_TRUE);
                rs.next();
                int count = rs.getInt(1);
                System.out.printf("Flag true : %s%n", count);
            }

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_SELECT_FLAG_FALSE);
                rs.next();
                int count = rs.getInt(1);
                System.out.printf("Flag false: %s%n", count);
            }

            connection.commit();
        }
    }
}
