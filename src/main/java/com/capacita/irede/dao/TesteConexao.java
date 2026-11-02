package com.capacita.irede.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = com.capacita.irede.dao.Conexao.conectar()) {

            System.out.println("Conexão estabelecida com sucesso!");

        } catch (SQLException e) {

            System.out.println("Erro ao conectar com o banco:");
            e.printStackTrace();

        }
    }
}
