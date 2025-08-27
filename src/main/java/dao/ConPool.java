// src/main/java/dao/ConPool.java
package dao;

import org.apache.tomcat.jdbc.pool.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class ConPool {
    private static volatile DataSource datasource;

    private ConPool() { }

    /** Chiamato una sola volta dal listener all’avvio. */
    public static void configure(DataSource ds) {
        ConPool.datasource = ds;
    }

    public static Connection getConnection() throws SQLException {
        if (datasource == null) {
            throw new IllegalStateException("ConPool non inizializzato: DataSource mancante.");
        }
        return datasource.getConnection();
    }

    /** Facoltativo (test, shutdown elegante). */
    public static void shutdown() {
        if (datasource != null) {
            datasource.close();
            datasource = null;
        }
    }
}
