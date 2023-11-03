package appjavadb;

import java.sql.Connection;

import static appjavadb.DBUtils.*;

public class DBReadSample {

    public static void main(String[] args) throws Exception {
        try (Connection connection = createConnection()) {
            readDB(connection, DB_TABLE);
        }
    }
}
