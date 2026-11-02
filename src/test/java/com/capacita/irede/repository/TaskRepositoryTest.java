package com.capacita.irede.repository;

import com.capacita.irede.dao.ConexaoTeste;
import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    private TaskRepository taskRepository;

    /**
     * Configura o repositório utilizando a conexão com o banco de testes.
     */
    @BeforeEach
    void configurar() throws Exception {
        taskRepository = new TaskRepository(ConexaoTeste::conectar);
    }

    /**
     * Limpa os registros do banco de testes após cada execução,
     * garantindo o isolamento entre os testes.
     */
    @AfterEach
    void finalizar() throws Exception {
        try (Connection conexao = ConexaoTeste.conectar();
             Statement stmt = conexao.createStatement()) {

            stmt.executeUpdate("DELETE FROM tarefas");
        }
    }

    /**
     * Verifica se a conexão com o banco de testes é estabelecida corretamente.
     */
    @Test
    void deveConectarAoBancoDeDados() throws Exception {
        try (Connection conexao = ConexaoTeste.conectar()) {
            assertNotNull(conexao);
            assertFalse(conexao.isClosed());
        }
    }

    /**
     * Verifica se uma tarefa é inserida no banco e recebe um identificador.
     */
    @Test
    void deveAdicionarTarefaAoBanco() throws Exception {

        Task tarefa = new Task(
                "Estudar Java",
                "Revisar conceitos de orientação a objetos"
        );

        taskRepository.adicionar(tarefa);

        assertNotNull(tarefa.getId());
        assertTrue(tarefa.getId() > 0);
    }

    /**
     * Verifica se as tarefas cadastradas são recuperadas corretamente,
     * preservando seus dados e estado de conclusão.
     */
    @Test
    void deveListarTarefasSalvasNoBanco() throws Exception {

        Task tarefa = new Task(
                "Estudar Java",
                "Revisar conceitos de orientação a objetos"
        );

        taskRepository.adicionar(tarefa);

        List<Task> tarefas = taskRepository.getTarefas();

        assertEquals(1, tarefas.size());

        Task tarefaRecuperada = tarefas.get(0);

        assertEquals(tarefa.getId(), tarefaRecuperada.getId());
        assertEquals("Estudar Java", tarefaRecuperada.getTitulo());
        assertEquals(
                "Revisar conceitos de orientação a objetos",
                tarefaRecuperada.getDescricao()
        );
        assertFalse(tarefaRecuperada.isConcluida());
    }

    /**
     * Verifica se a conclusão de uma tarefa é persistida corretamente
     * e recuperada com o estado atualizado.
     */
    @Test
    void deveConcluirTarefaNoBanco() throws Exception {

        Task tarefa = new Task(
                "Estudar Java",
                "Revisar conceitos de orientação a objetos"
        );

        taskRepository.adicionar(tarefa);

        taskRepository.concluir(tarefa);

        List<Task> tarefas = taskRepository.getTarefas();

        Task tarefaRecuperada = tarefas.get(0);

        assertTrue(tarefaRecuperada.isConcluida());
    }

    /**
     * Verifica se uma tarefa é removida do banco de dados com sucesso.
     */
    @Test
    void deveRemoverTarefaDoBanco() throws Exception {

        Task tarefa = new Task(
                "Estudar Java",
                "Revisar conceitos de orientação a objetos"
        );

        taskRepository.adicionar(tarefa);

        boolean removida = taskRepository.remover(tarefa);

        List<Task> tarefas = taskRepository.getTarefas();

        assertTrue(removida);
        assertTrue(tarefas.isEmpty());
    }

    /**
     * Verifica se uma subtarefa é cadastrada e recuperada com
     * seu vínculo corretamente associado à tarefa principal.
     */
    @Test
    void deveAdicionarSubtarefaVinculadaATarefaPrincipal() throws Exception {

        Task tarefaPrincipal = new Task(
                "Desenvolver projeto",
                "Implementar o gerenciador de tarefas"
        );

        taskRepository.adicionar(tarefaPrincipal);

        TaskPrioritaria subtarefa = new TaskPrioritaria(
                "Implementar persistência",
                "Criar operações JDBC",
                tarefaPrincipal
        );

        taskRepository.adicionar(subtarefa);

        List<Task> tarefas = taskRepository.getTarefas();

        assertEquals(1, tarefas.size());

        Task tarefaRecuperada = tarefas.get(0);

        assertEquals(1, tarefaRecuperada.getSubtarefas().size());

        TaskPrioritaria subtarefaRecuperada =
                tarefaRecuperada.getSubtarefas().get(0);

        assertEquals(subtarefa.getId(), subtarefaRecuperada.getId());
        assertEquals("Implementar persistência", subtarefaRecuperada.getTitulo());
        assertEquals(
                tarefaRecuperada.getId(),
                subtarefaRecuperada.getTarefaPai().getId()
        );
    }

    /**
     * Verifica se a remoção de uma tarefa principal também exclui
     * suas subtarefas vinculadas.
     */
    @Test
    void deveRemoverSubtarefasAoRemoverTarefaPrincipal() throws Exception {

        Task tarefaPrincipal = new Task(
                "Desenvolver projeto",
                "Implementar o gerenciador de tarefas"
        );

        taskRepository.adicionar(tarefaPrincipal);

        TaskPrioritaria subtarefa = new TaskPrioritaria(
                "Implementar persistência",
                "Criar operações JDBC",
                tarefaPrincipal
        );

        taskRepository.adicionar(subtarefa);

        boolean removida = taskRepository.remover(tarefaPrincipal);

        List<Task> tarefas = taskRepository.getTarefas();

        assertTrue(removida);
        assertTrue(tarefas.isEmpty());
    }

    /**
     * Verifica se uma subtarefa pode ser removida individualmente,
     * sem excluir sua tarefa principal.
     */
    @Test
    void deveRemoverSubtarefaSemRemoverTarefaPrincipal() throws Exception {

        Task tarefaPrincipal = new Task(
                "Desenvolver projeto",
                "Implementar o gerenciador de tarefas"
        );

        taskRepository.adicionar(tarefaPrincipal);

        TaskPrioritaria subtarefa = new TaskPrioritaria(
                "Implementar persistência",
                "Criar operações JDBC",
                tarefaPrincipal
        );

        taskRepository.adicionar(subtarefa);

        boolean removida = taskRepository.remover(subtarefa);

        List<Task> tarefas = taskRepository.getTarefas();

        assertTrue(removida);
        assertEquals(1, tarefas.size());
        assertTrue(tarefas.get(0).getSubtarefas().isEmpty());
    }

    /**
     * Verifica se a tentativa de remover uma tarefa não cadastrada
     * retorna false.
     */
    @Test
    void naoDeveRemoverTarefaInexistente() throws Exception {

        Task tarefa = new Task(
                "Tarefa inexistente",
                "Essa tarefa não foi cadastrada no banco"
        );

        boolean removida = taskRepository.remover(tarefa);

        assertFalse(removida);
    }

    /**
     * Verifica se a consulta retorna uma lista vazia
     * quando não existem tarefas cadastradas.
     */
    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremTarefas() throws Exception {

        List<Task> tarefas = taskRepository.getTarefas();

        assertNotNull(tarefas);
        assertTrue(tarefas.isEmpty());
    }
}