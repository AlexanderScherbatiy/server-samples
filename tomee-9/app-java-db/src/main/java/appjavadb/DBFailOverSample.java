package appjavadb;

import java.sql.*;

import static appjavadb.DBUtils.*;

public class DBFailOverSample {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.printf("Use options: [UPDATE, CHECK, PARALLEL]%n");
            System.exit(-1);
        }

        debug("DB url: %s, user: %s%n", DB_URL, DB_USER);

        String command = args[0];

        switch (command.toUpperCase()) {
            case "UPDATE":
                System.out.printf("Init...%n");
                init();
                update();
                break;
            case "CHECK":
                check(1);
                break;
            case "PARALLEL":
                init();
                parallel();
                break;
            default:
                throw new RuntimeException("Unknown command: " + command + ", use [UPDATE, CHECK, PARALLEL]");
        }
    }

    private static void update() throws SQLException {
        int iteration = 0;
        while (true) {
            int min = 2 * iteration + ((iteration % 2 == 0) ? 0 : 1);
            System.out.printf("Update start records: %d, min: %d, iteration: %d ...%n",
                    DB_RECORDS, min, iteration);
            update(iteration);
            System.out.printf("Update end%n");
            iteration++;
        }
    }

    private static void init() throws SQLException {

        final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", DB_TABLE);

        final String CREATE_TABLE = String.format("CREATE TABLE %s (" +
                        " ID BIGINT IDENTITY NOT NULL PRIMARY KEY," +
                        " name VARCHAR(255)," +
                        " value BIGINT," +
                        " flag boolean not null)",
                DB_TABLE);

        final String TEMPLATE_INSERT_TABLE = String.format("INSERT INTO %s VALUES (?, ?, ?, ?)", DB_TABLE);

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

        final String TEMPLATE_UPDATE_TABLE = String.format(
                "update %s set name=?, value=?, flag=? where id=?", DB_TABLE);


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

    private static void check() throws SQLException {
        int iteration = 0;
        while (true) {
            check(iteration++);
        }
    }

    private static void check(int iteration) throws SQLException {

        String DB_COUNT = String.format("SELECT COUNT(*) FROM %s", DB_TABLE);

        String DB_MIN =
                String.format("SELECT MIN(value) FROM %s", DB_TABLE);

        String DB_FLAG_TRUE =
                String.format("SELECT COUNT(value) FROM %s WHERE flag = true", DB_TABLE);

        String DB_FLAG_FALSE =
                String.format("SELECT COUNT(value) FROM %s WHERE flag = false", DB_TABLE);

        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);

            int rows = 0;
            int min = 0;
            int flagsTrue = 0;
            int flagsFalse = 0;

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_COUNT);
                rs.next();
                rows = rs.getInt(1);
            }

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_MIN);
                rs.next();
                min = rs.getInt(1);
            }

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_FLAG_TRUE);
                rs.next();
                flagsTrue = rs.getInt(1);
            }

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(DB_FLAG_FALSE);
                rs.next();
                flagsFalse = rs.getInt(1);
            }

            System.out.printf("iteration: %d, rows: %d, min: %d, even: %b, flagsTrue: %d, flagsFalse: %d%n",
                    iteration, rows, min, (min % 2 == 0), flagsTrue, flagsFalse);

            if (min % 2 == 0 && flagsTrue == rows && flagsFalse == 0) {
                // pass
            } else if (min % 2 == 1 && flagsTrue == 0 && flagsFalse == rows) {
                // pass
            } else {
                // throw new RuntimeException("Table is not consistent!");
            }

            connection.commit();
        }
    }

    private static void parallel() {
        new Thread(() -> {
            try {
                update();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                check();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }
}
