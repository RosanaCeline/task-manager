package com.capacita.irede.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Define um contrato funcional para fornecer conexões com o banco de dados,
 * permitindo diferentes implementações de conexão.
 */
@FunctionalInterface
public interface FabricaConexao {

    /**
     * Abre e retorna uma conexão com o banco de dados.
     *
     * @return conexão estabelecida
     * @throws SQLException caso ocorra um erro ao abrir a conexão
     */
    Connection abrir() throws SQLException;
}
