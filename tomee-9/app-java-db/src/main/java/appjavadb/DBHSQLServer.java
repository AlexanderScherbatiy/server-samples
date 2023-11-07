package appjavadb;

import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

import static appjavadb.DBUtils.*;

public class DBHSQLServer {

    public static void main(String[] args) throws Exception {
        debug("DB url: %s, user: %s%n", DB_URL, DB_USER);

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
}
