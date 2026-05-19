package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {
    private final String jdbcUrl;
    private final String user;
    private final String password;

    public DbManager(String host, String dbName, String user, String password) {
        this.jdbcUrl = "jdbc:postgresql://" + host + ":5432/" + dbName;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, password);
    }
}