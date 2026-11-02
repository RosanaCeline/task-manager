package com.capacita.irede.repository;

import com.capacita.irede.exceptions.TaskTitleTooLongException;

import java.sql.SQLException;
import java.util.List;

/**
 * Define as operações básicas de persistência para diferentes tipos de entidades,
 * utilizando Generics para permitir a reutilização do contrato.
 *
 * @param <T> tipo de entidade manipulada pelo repositório
 */
public interface Repository<T> {

    /**
     * Adiciona uma entidade ao banco de dados.
     *
     * @param task entidade a ser cadastrada
     * @throws SQLException caso ocorra um erro na operação com o banco
     * @throws TaskTitleTooLongException caso o título ultrapasse o limite permitido
     */
    void adicionar(T task) throws SQLException, TaskTitleTooLongException;

    /**
     * Recupera a lista de entidades armazenadas no banco de dados.
     *
     * @return lista de entidades cadastradas
     * @throws SQLException caso ocorra um erro na consulta
     */
    List<T> getTarefas() throws SQLException;

    /**
     * Atualiza o estado de conclusão de uma entidade no banco de dados.
     *
     * @param task entidade que terá seu estado atualizado
     * @throws SQLException caso ocorra um erro na operação
     */
    void concluir(T task) throws SQLException;

    /**
     * Remove uma entidade do banco de dados.
     *
     * @param task entidade a ser removida
     * @return true se a entidade foi removida; false caso contrário
     * @throws SQLException caso ocorra um erro na operação
     */
    boolean remover(T task) throws SQLException;
}