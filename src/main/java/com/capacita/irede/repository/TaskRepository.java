package com.capacita.irede.repository;

import com.capacita.irede.dao.Conexao;
import com.capacita.irede.dao.FabricaConexao;
import com.capacita.irede.exceptions.TaskTitleTooLongException;
import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementa as operações de persistência de tarefas no banco de dados MySQL,
 * incluindo o gerenciamento de tarefas principais e subtarefas.
 */
public class TaskRepository implements Repository<Task> {
    private final FabricaConexao fabricaConexao;

    /**
     * Inicializa o repositório utilizando a fábrica de conexão padrão da aplicação.
     */
    public TaskRepository() {
        this(Conexao::conectar);
    }

    /**
     * Inicializa o repositório recebendo uma fábrica de conexões,
     * permitindo utilizar diferentes bancos de dados.
     *
     * @param fabricaConexao responsável por fornecer conexões com o banco
     */
    public TaskRepository(FabricaConexao fabricaConexao) {
        this.fabricaConexao = fabricaConexao;
    }

    /**
     * Insere uma tarefa no banco de dados, recupera seu identificador
     * gerado e, quando necessário, estabelece seu vínculo com a tarefa principal.
     *
     * @param task tarefa a ser cadastrada
     * @throws SQLException caso ocorra um erro na operação com o banco
     * @throws TaskTitleTooLongException caso o título ultrapasse o limite permitido
     */
    @Override
    public void adicionar(Task task) throws SQLException, TaskTitleTooLongException {

        String sql = """
            INSERT INTO tarefas (titulo, descricao, concluida, tarefa_pai_id)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = fabricaConexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, task.getTitulo());
            stmt.setString(2, task.getDescricao());
            stmt.setBoolean(3, task.isConcluida());

            if (task instanceof TaskPrioritaria subtarefa) {
                stmt.setLong(4, subtarefa.getTarefaPai().getId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }

            stmt.executeUpdate();

            try (ResultSet resultado = stmt.getGeneratedKeys()) {
                if (resultado.next()) {
                    task.setId(resultado.getLong(1));
                }
            }

            if (task instanceof TaskPrioritaria subtarefa) {
                subtarefa.getTarefaPai().adicionarSubtarefa(subtarefa);
            }

        } catch (MysqlDataTruncation e) {
            if ("titulo".equals(e.getMessage().contains("'titulo'")
                    ? "titulo" : "")) {
                throw new TaskTitleTooLongException();
            }

            throw e;
        }
    }

    /**
     * Consulta as tarefas armazenadas no banco e reconstrói sua hierarquia,
     * associando cada subtarefa à respectiva tarefa principal.
     *
     * @return lista de tarefas principais com suas subtarefas
     * @throws SQLException caso ocorra um erro na consulta
     */
    @Override
    public List<Task> getTarefas() throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, concluida, tarefa_pai_id
                FROM tarefas
                ORDER BY tarefa_pai_id IS NOT NULL, id
                """;

        List<Task> tarefas = new ArrayList<>();
        Map<Long, Task> tarefasPorId = new HashMap<>();

        try (Connection conexao = fabricaConexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            List<TaskRow> registros = new ArrayList<>();

            while (resultado.next()) {
                long id = resultado.getLong("id");
                long paiId = resultado.getLong("tarefa_pai_id");
                Long tarefaPaiId = resultado.wasNull() ? null : paiId;

                registros.add(new TaskRow(
                        id,
                        resultado.getString("titulo"),
                        resultado.getString("descricao"),
                        resultado.getBoolean("concluida"),
                        tarefaPaiId
                ));
            }

            // Primeiro, recupera as tarefas principais
            for (TaskRow registro : registros) {
                if (registro.tarefaPaiId() == null) {
                    Task task = criarTask(registro);

                    tarefas.add(task);
                    tarefasPorId.put(registro.id(), task);
                }
            }

            // Depois, recupera as subtarefas
            for (TaskRow registro : registros) {
                if (registro.tarefaPaiId() != null) {
                    Task pai = tarefasPorId.get(registro.tarefaPaiId());

                    if (pai != null) {
                        TaskPrioritaria subtarefa =
                                new TaskPrioritaria(
                                        registro.titulo(),
                                        registro.descricao(),
                                        pai
                                );

                        subtarefa.setId(registro.id());
                        subtarefa.setConcluida(registro.concluida());

                        pai.adicionarSubtarefa(subtarefa);
                    }
                }
            }
        }

        return tarefas;
    }

    /**
     * Atualiza o estado de conclusão da tarefa no banco de dados
     * e atualiza o objeto correspondente em memória.
     *
     * @param task tarefa que será marcada como concluída
     * @throws SQLException caso ocorra um erro na atualização
     */
    @Override
    public void concluir(Task task) throws SQLException {
        String sql = """
                UPDATE tarefas
                SET concluida = ?
                WHERE id = ?
                """;

        try (Connection conexao = fabricaConexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setBoolean(1, true);
            stmt.setLong(2, task.getId());

            stmt.executeUpdate();

            task.setConcluida(true);
        }
    }

    /**
     * Remove uma tarefa do banco de dados utilizando seu identificador.
     *
     * @param task tarefa a ser removida
     * @return true se alguma linha foi removida; false caso contrário
     * @throws SQLException caso ocorra um erro na exclusão
     */
    @Override
    public boolean remover(Task task) throws SQLException {

        if (task == null || task.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM tarefas WHERE id = ?";

        try (Connection conexao = fabricaConexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, task.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Constrói um objeto Task a partir dos dados recuperados do banco,
     * restaurando seu identificador e estado de conclusão.
     *
     * @param registro registro contendo os dados da tarefa
     * @return tarefa reconstruída
     * @throws SQLException caso os dados não permitam criar a tarefa
     */
    private Task criarTask(TaskRow registro) throws SQLException {
        try {
            Task task = new Task(registro.titulo(), registro.descricao());

            task.setId(registro.id());
            task.setConcluida(registro.concluida());

            return task;

        } catch (TitleEmptyOrNullException e) {
            throw new SQLException("Erro ao recuperar tarefa do banco.", e);
        }
    }

    /**
     * Representa os dados de uma tarefa recuperados do banco de dados,
     * incluindo o identificador da tarefa principal quando existente.
     */
    private record TaskRow(
            Long id,
            String titulo,
            String descricao,
            boolean concluida,
            Long tarefaPaiId
    ) {}
}