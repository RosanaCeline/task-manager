package com.capacita.irede.model;

public class TaskPrioritaria extends Task {
    private final Task taskPai;

    public TaskPrioritaria(String titulo, String descricao, Task taskPai){
        super(titulo, descricao);
        this.taskPai = taskPai;
    };

    public Task getTarefaPai() {
        return taskPai;
    }

    @Override
    public String exibirComoLinha(int indice) {
        return super.exibirComoLinha(indice);
    }
}
