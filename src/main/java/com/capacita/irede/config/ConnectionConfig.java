package com.capacita.irede.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionConfig {
    private static final String URL = "jdbc:mysql:taskmanager.db";

    public static java.sql.Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
