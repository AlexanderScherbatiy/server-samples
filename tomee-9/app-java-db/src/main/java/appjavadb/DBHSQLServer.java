package appjavadb;

import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

import java.sql.Connection;
import java.sql.Statement;

import static appjavadb.DBUtils.*;

public class DBHSQLServer {

    public static void main(String[] args) throws Exception {
        debug("DB url: %s, user: %s%n", DB_URL, DB_USER);

        String command = "SERVER";

        if (args.length > 0) {
            command = args[0];
        }

        switch (command.toUpperCase()) {
            case "SERVER":
                runServer();
                break;
            case "BACKUP":
                backup();
                break;
            default:
                throw new RuntimeException("Unknown command " + command
                        + ", use [SERVER|BACKUP]");
        }
    }

    private static void runServer() throws Exception {

        String dbNamePath = getDBNamePath(DB_URL);
        System.out.printf("DB name path: %s%n", dbNamePath);

        String db = String.format("file:%s;user=%s;password=%s",
                dbNamePath, DB_USER, DB_PASS);

        HsqlProperties p = new HsqlProperties();
        p.setProperty("server.remote_open", "true");

        p.setProperty("server.database.0", db);
        // DB name in jdbc url after host:
        // jdbc:hsqldb:hsql://localhost/test
        p.setProperty("server.dbname.0", "test");

        debug("HSQLDB server properties: %s%n", p);

        debug("Start HSQLDB server%n");

        Server server = new Server();
        server.setProperties(p);
        server.start();
    }

    private static void backup() throws Exception {
        final String BACKUP = String.format("BACKUP DATABASE TO '%s' BLOCKING", DB_BACKUP);

        debug("sql backup: %s%n", BACKUP);

        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(BACKUP);
            }
            connection.commit();
        }
    }
}
