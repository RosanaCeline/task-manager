package com.capacita.irede.model;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;

    /**
     * Inicializa uma tarefa padrão antes da execução de cada teste.
     */
    @BeforeEach
    void setUp() {
        task = new Task("Estudar Java", "Revisar orientação a objetos");
    }

    /**
     * Limpa a referência da tarefa após a execução de cada teste.
     */
    @AfterEach
    void tearDown() {
        task = null;
    }

    /**
     * Verifica se uma tarefa é criada corretamente com título e descrição.
     */
    @Test
    void deveCriarTarefaComSucesso() {
        assertNotNull(task);
        assertEquals("Estudar Java", task.getTitulo());
        assertEquals("Revisar orientação a objetos", task.getDescricao());
    }

    /**
     * Verifica se uma nova tarefa é inicializada com o estado pendente.
     */
    @Test
    void tarefaDeveIniciarComoPendente() {
        assertFalse(task.isConcluida());
    }

    /**
     * Verifica se uma tarefa pode ser marcada como concluída.
     */
    @Test
    void devePermitirConcluirTarefa() {
        task.setConcluida(true);

        assertTrue(task.isConcluida());
    }

    /**
     * Verifica se uma tarefa concluída pode voltar ao estado pendente.
     */
    @Test
    void devePermitirReabrirTarefa() {
        task.setConcluida(true);
        task.setConcluida(false);

        assertFalse(task.isConcluida());
    }

    /**
     * Verifica se espaços extras no início e no final do título são removidos.
     */
    @Test
    void deveRemoverEspacosExtrasDoTitulo() {
        Task novaTask = new Task("  estudar java  ", "Descrição");

        assertEquals("Estudar java", novaTask.getTitulo());
    }

    /**
     * Verifica se a criação de uma tarefa com título nulo gera uma exceção.
     */
    @Test
    void deveRejeitarTituloNulo() {
        assertThrows(
                TitleEmptyOrNullException.class,
                () -> new Task(null, "Descrição")
        );
    }

    /**
     * Verifica se a criação de uma tarefa com título vazio gera uma exceção.
     */
    @Test
    void deveRejeitarTituloVazio() {
        assertThrows(
                TitleEmptyOrNullException.class,
                () -> new Task("", "Descrição")
        );
    }

    /**
     * Verifica se títulos contendo apenas espaços são rejeitados.
     */
    @Test
    void deveRejeitarTituloComApenasEspacos() {
        assertThrows(
                TitleEmptyOrNullException.class,
                () -> new Task("    ", "Descrição")
        );
    }

    /**
     * Verifica se uma descrição nula é aceita e armazenada como string vazia.
     */
    @Test
    void devePermitirDescricaoNula() {
        Task novaTask = new Task("Estudar Java", null);

        assertEquals("", novaTask.getDescricao());
    }

    /**
     * Verifica se uma subtarefa é adicionada corretamente e mantém
     * o vínculo com sua tarefa principal.
     */
    @Test
    void deveAdicionarSubtarefa() {
        TaskPrioritaria subtarefa = new TaskPrioritaria(
                "Estudar heranca",
                "Revisar conceitos",
                task
        );

        task.adicionarSubtarefa(subtarefa);

        assertEquals(1, task.getSubtarefas().size());
        assertSame(subtarefa, task.getSubtarefas().get(0));
        assertSame(task, subtarefa.getTarefaPai());
    }
}