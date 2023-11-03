package appjavadb;

import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static appjavadb.DBUtils.*;

public class DBReadWriteSample {

    private static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", DB_TABLE);

    private static final String CREATE_TABLE = String.format("CREATE TABLE %s (ID BIGINT IDENTITY NOT NULL PRIMARY KEY, prop_name VARCHAR(255), prop_value BIGINT)", DB_TABLE);

    private static final String TEMPLATE_INSERT_TABLE = String.format("INSERT INTO %s VALUES (?, ?, ?)", DB_TABLE);

    private static final String TEMPLATE_UPDATE_TABLE = String.format("update %s set prop_value=? where id=?", DB_TABLE);

    public static void main(String[] args) throws Exception {

        System.out.printf("DB url: %s, user: %s, records: %d%n",
                DB_URL, DB_USER, DB_RECORDS);

        init();

        int N = 10;
        for (int i = 1; i <= N; i++) {
            int updateIterations = i * DB_WRITE_ITERATIONS / N;
            updateReadDifferentConnections(DB_READ_ITERATIONS, updateIterations);
        }

        for (int i = 1; i <= N; i++) {
            int writeIterations = i * DB_WRITE_ITERATIONS / N;
            updateReadPoolDifferentConnections(DB_READ_ITERATIONS, writeIterations, DB_WRITERS);
        }
    }

    private static void init() throws SQLException {
        try (Connection connection = createConnection()) {
            initDB(connection);
        }
    }

    private static void initDB(Connection connection) throws SQLException {
        debug("init db%n");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(DROP_TABLE);
            stmt.execute(CREATE_TABLE);

            for (int i = 0; i < DB_RECORDS; i++) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(TEMPLATE_INSERT_TABLE)) {
                    preparedStatement.setLong(1, i);
                    preparedStatement.setString(2, "name_" + i);
                    preparedStatement.setLong(3, i);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    private static void updateReadDifferentConnections(
            final int readIterations,
            final int updateIterations) throws SQLException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Thread updateThread = new Thread(() -> {
            debug("Thread update begin, iterations: %d%n", updateIterations);
            try (Connection connection = createConnection()) {
                latch.await();
                for (int i = 0; i < updateIterations; i++) {
                    updateDB(connection, 0, DB_RECORDS, 7);
                }
            } catch (SQLException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                debug("Thread update end%n");
            }
        });


        Thread readThread = new Thread(() -> {
            debug("Thread read begin, iterations: %d%n", readIterations);
            long totalTime = 0;
            try (Connection connection = createConnection()) {
                latch.await();
                for (int i = 0; i < readIterations; i++) {
                    long time = System.nanoTime();
                    readDB(connection, DB_TABLE);
                    totalTime += System.nanoTime() - time;
                }
            } catch (SQLException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                debug("Thread read end%n");

                String msg = String.format("records: %d, updaters: %d, reads: %d, updates: %s",
                        DB_RECORDS, 1, readIterations, updateIterations);
                showTime(msg, totalTime);
            }
        });

        updateThread.start();
        readThread.start();

        latch.countDown();

        updateThread.join(3000);
        readThread.join(3000);
    }


    private static void updateReadPoolDifferentConnections(
            final int readIterations,
            final int writeIterations,
            final int writers) throws SQLException, InterruptedException {


        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(DB_THREADS);

        final CountDownLatch latchBegin = new CountDownLatch(1);

        final CountDownLatch latchEnd = new CountDownLatch(writers + 1);

        final int delta = DB_RECORDS / writers;

        final int records = DB_RECORDS;

        for (int writer = 0; writer < writers; writer++) {

            final int index = writer * delta;

            executor.execute(() -> {
                try (Connection connection = createConnection()) {
                    latchBegin.await();
                    debug("writer begin, index: %d%n", index);

                    int baseValue = index;
                    for (int iteration = 0; iteration < writeIterations; iteration++) {
                        updateDB(connection, index, delta, baseValue++);
                    }

                } catch (InterruptedException | SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    latchEnd.countDown();
                    debug("writer finished, index: %d", index);
                }
            });
        }

        executor.execute(() -> {
            long totalTime = 0;
            try (Connection connection = createConnection()) {
                latchBegin.await();
                debug("read, iterations: %d%n", readIterations);
                long time = 0;
                for (int iteration = 0; iteration < DB_READ_ITERATIONS; iteration++) {
                    time = System.nanoTime();
                    readDB(connection, DB_TABLE);
                    totalTime += (System.nanoTime() - time);
                }
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            } finally {
                latchEnd.countDown();
                String msg = String.format("records: %d, writers: %d, reads: %d, writes: %s",
                        records, writers, readIterations, writeIterations);
                showTime(msg, totalTime);
            }
        });

        latchBegin.countDown();
        latchEnd.await();
        debug("active threads: %d%n", executor.getActiveCount());
        executor.shutdown();
    }

    private static void updateDB(Connection connection, int startIndex, int interval, int baseValue) throws SQLException {

        try (Statement stmt = connection.createStatement()) {

            int value = baseValue;
            for (int index = startIndex; index < startIndex + interval; index++) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(TEMPLATE_UPDATE_TABLE)) {
                    preparedStatement.setLong(1, value++);
                    preparedStatement.setLong(2, index);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    private static void showTime(String message, long time) {
        double t = ((double) time) / 1_000_000;
        System.out.printf("%s, time: %fms%n", message, t);
    }
}
