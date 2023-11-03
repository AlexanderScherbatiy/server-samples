package appjavadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static appjavadb.DBUtils.*;

public class DBFailOverSample {

    private static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", DB_TABLE);

    private static final String CREATE_TABLE = String.format("CREATE TABLE %s (" +
                    " ID BIGINT IDENTITY NOT NULL PRIMARY KEY," +
                    " name VARCHAR(255)," +
                    " value BIGINT," +
                    " flag boolean not null)",
            DB_TABLE);

    private static final String TEMPLATE_INSERT_TABLE = String.format("INSERT INTO %s VALUES (?, ?, ?, ?)", DB_TABLE);

    private static final String TEMPLATE_UPDATE_TABLE = String.format(
            "update %s set name=?, value=?, flag=? where id=?", DB_TABLE);

    public static void main(String[] args) throws Exception {

        debug("DB url: %s, user: %s%n", DB_URL, DB_USER);
        System.out.printf("Records: %d%n", DB_RECORDS);

        System.out.printf("Init...%n");
        init();

        readDB(DB_TABLE);

        int count = 0;
        while (true) {
            System.out.printf("Update start records: %d, count: %d ...%n", DB_RECORDS, count);
            update(count);
            System.out.printf("Update end%n");
            readDB(DB_TABLE);
            count++;
        }
    }

    private static void init() throws SQLException {
        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(DROP_TABLE);
                stmt.execute(CREATE_TABLE);

                for (int i = 0; i < DB_RECORDS; i++) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(TEMPLATE_INSERT_TABLE)) {
                        int value = 2 * i;
                        preparedStatement.setLong(1, i);
                        preparedStatement.setString(2, "field-" + value);
                        preparedStatement.setLong(3, value);
                        preparedStatement.setBoolean(4, true);
                        preparedStatement.executeUpdate();
                    }
                }
            }
            connection.commit();
        }
    }

    private static void update(int count) throws SQLException {
        boolean isEven = (count % 2 == 0);
        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);
            for (int i = 0; i < DB_RECORDS; i++) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(TEMPLATE_UPDATE_TABLE)) {
                    int value = 2 * (i + count) + ((isEven) ? 0 : 1);
                    preparedStatement.setString(1, "field-" + value);
                    preparedStatement.setLong(2, value);
                    preparedStatement.setBoolean(3, isEven);
                    preparedStatement.setLong(4, i);
                    preparedStatement.executeUpdate();
                }
            }
            connection.commit();
        }
    }
}
