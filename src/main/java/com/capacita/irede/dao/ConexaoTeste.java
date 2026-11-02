package com.capacita.irede.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoTeste {

    private static final String URL =
            "jdbc:mysql://localhost:3306/task_manager_test";

    private static final String USER = "taskmanager_user";
    private static final String PASSWORD = "taskmanager123";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}