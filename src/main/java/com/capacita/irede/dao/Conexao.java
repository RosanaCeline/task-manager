package com.capacita.irede.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "taskmanager_user";
    private static final String PASSWORD = "taskmanager123";

    /**
     * Estabelece e retorna uma conexão com o banco de dados MySQL.
     *
     * @return conexão ativa com o banco de dados
     * @throws SQLException caso ocorra um erro ao estabelecer a conexão
     */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
