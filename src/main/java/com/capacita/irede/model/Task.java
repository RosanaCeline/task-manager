package com.capacita.irede.model;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;

import java.util.ArrayList;

public class Task {
    private Long id;
    private String titulo;
    private String descricao;
    private boolean concluida;
    private final ArrayList<TaskPrioritaria> subtarefas = new ArrayList<>();

    public Task(String titulo, String descricao) {
        setTitulo(titulo);
        setDescricao(descricao);
        this.concluida = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new TitleEmptyOrNullException();
        }

        String tratado = titulo.trim();
        this.titulo = tratado.substring(0, 1).toUpperCase() + tratado.substring(1);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao != null && !descricao.isBlank()) {
            String tratado = descricao.trim();

            this.descricao = tratado.substring(0, 1).toUpperCase() + tratado.substring(1);
        } else {
            this.descricao = "";
        }
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public String exibirComoLinha(int indice) {
        return exibirComoLinha(indice, String.valueOf(indice), 0);
    }

    public String exibirComoLinha(int indice, String prefixo, int nivel) {
        String status = isConcluida() ? "[X] Concluída" : "[ ] Pendente";

        StringBuilder resultado = new StringBuilder();

        resultado.append(prefixo)
                .append(". ")
                .append(status)
                .append(" | ")
                .append(getTitulo())
                .append(" - Descrição: ")
                .append(getDescricao());

        for (int i = 0; i < subtarefas.size(); i++) {
            TaskPrioritaria subtarefa = subtarefas.get(i);

            resultado.append("\n")
                    .append("    ".repeat(nivel + 1))
                    .append("└── ")
                    .append(subtarefa.exibirComoLinha(
                            i + 1,
                            prefixo + "." + (i + 1),
                            nivel + 1
                    ));
        }

        return resultado.toString();
    }

    public void adicionarSubtarefa(TaskPrioritaria subtarefa) {
        subtarefas.add(subtarefa);
    }

    public ArrayList<TaskPrioritaria> getSubtarefas() {
        return subtarefas;
    }
}
