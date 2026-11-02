package com.capacita.irede.model;

public class TarefaPrioritaria extends Tarefa {
    private final Tarefa tarefaPai;

    public TarefaPrioritaria(String titulo, String descricao, Tarefa tarefaPai){
        super(titulo, descricao);
        this.tarefaPai = tarefaPai;
    };

    public Tarefa getTarefaPai() {
        return tarefaPai;
    }

    @Override
    public String exibirComoLinha(int indice) {
        return super.exibirComoLinha(indice);
    }
}
