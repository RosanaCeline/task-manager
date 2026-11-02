
package com.capacita.irede.model;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;

    // Executado antes de cada teste
    @BeforeEach
    void setUp() {
        task = new Task("Estudar Java", "Revisar orientação a objetos");
    }

    // Executado depois de cada teste
    @AfterEach
    void tearDown() {
        task = null;
    }

    @Test
    void deveCriarTarefaComSucesso() {
        assertNotNull(task);
        assertEquals("Estudar Java", task.getTitulo());
        assertEquals("Revisar orientação a objetos", task.getDescricao());
    }

    @Test
    void tarefaDeveIniciarComoPendente() {
        assertFalse(task.isConcluida());
    }

    @Test
    void devePermitirConcluirTarefa() {
        task.setConcluida(true);

        assertTrue(task.isConcluida());
    }

    @Test
    void devePermitirReabrirTarefa() {
        task.setConcluida(true);
        task.setConcluida(false);

        assertFalse(task.isConcluida());
    }

    @Test
    void deveRemoverEspacosExtrasDoTitulo() {
        Task novaTask = new Task("  estudar java  ", "Descrição");

        assertEquals("Estudar java", novaTask.getTitulo());
    }

    @Test
    void deveRejeitarTituloNulo() {
        assertThrows(
                TitleEmptyOrNullException.class,
                () -> new Task(null, "Descrição")
        );
    }

    @Test
    void deveRejeitarTituloVazio() {
        assertThrows(
                TitleEmptyOrNullException.class,
                () -> new Task("", "Descrição")
        );
    }

    @Test
    void deveRejeitarTituloComApenasEspacos() {
        assertThrows(
                TitleEmptyOrNullException.class,
                () -> new Task("    ", "Descrição")
        );
    }

    @Test
    void devePermitirDescricaoNula() {
        Task novaTask = new Task("Estudar Java", null);

        assertEquals("", novaTask.getDescricao());
    }

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
