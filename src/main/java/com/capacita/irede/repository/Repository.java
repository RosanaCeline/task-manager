package com.capacita.irede.repository;

import com.capacita.irede.exceptions.TaskTitleTooLongException;

import java.sql.SQLException;
import java.util.List;

public interface Repository<T> {

    void adicionar(T task) throws SQLException, TaskTitleTooLongException;

    List<T> getTarefas() throws SQLException;

    void concluir(T task) throws SQLException;

    boolean remover(T task) throws SQLException;
}
