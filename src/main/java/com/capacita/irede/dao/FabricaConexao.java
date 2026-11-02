package com.capacita.irede.dao;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface FabricaConexao {

    Connection abrir() throws SQLException;
}
