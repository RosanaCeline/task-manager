package com.capacita.irede.model;

/**
 * Representa uma subtarefa vinculada a uma tarefa principal,
 * herdando seus atributos e comportamentos.
 */
public class TaskPrioritaria extends Task {
    private final Task taskPai;

    /**
     * Cria uma subtarefa associada a uma tarefa principal.
     *
     * @param titulo título da subtarefa
     * @param descricao descrição da subtarefa
     * @param taskPai tarefa principal à qual está vinculada
     */
    public TaskPrioritaria(String titulo, String descricao, Task taskPai){
        super(titulo, descricao);
        this.taskPai = taskPai;
    };

    /**
     * Retorna a tarefa principal à qual esta subtarefa pertence.
     *
     * @return tarefa principal vinculada
     */
    public Task getTarefaPai() {
        return taskPai;
    }

    /**
     * Retorna a representação textual da subtarefa,
     * utilizando o formato definido pela classe Task.
     *
     * @param indice posição da subtarefa na listagem
     * @return representação textual da subtarefa
     */
    @Override
    public String exibirComoLinha(int indice) {
        return super.exibirComoLinha(indice);
    }
}
