package com.capacita.irede.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoTeste {

    private static final String URL =
            "jdbc:mysql://localhost:3306/task_manager_test";

    private static final String USER = "taskmanager_user";
    private static final String PASSWORD = "taskmanager123";

    /**
     * Estabelece e retorna uma conexão com o banco de dados
     * utilizado exclusivamente nos testes.
     *
     * @return conexão com o banco de testes
     * @throws SQLException caso ocorra um erro ao estabelecer a conexão
     */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}