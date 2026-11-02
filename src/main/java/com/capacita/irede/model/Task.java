package com.capacita.irede.model;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;

import java.util.ArrayList;

public class Task {
    private Long id;
    private String titulo;
    private String descricao;
    private boolean concluida;
    private final ArrayList<TaskPrioritaria> subtarefas = new ArrayList<>();

    /**
     * Cria uma tarefa com título e descrição, inicializando-a como pendente.
     *
     * @param titulo título da tarefa
     * @param descricao descrição da tarefa
     */
    public Task(String titulo, String descricao) {
        setTitulo(titulo);
        setDescricao(descricao);
        this.concluida = false;
    }

    /**
     * Retorna o identificador da tarefa.
     *
     * @return identificador da tarefa
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador da tarefa.
     *
     * @param id identificador da tarefa
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o título da tarefa.
     *
     * @return título da tarefa
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Define o título da tarefa, removendo espaços extras e
     * aplicando inicial maiúscula. Rejeita valores nulos ou vazios.
     *
     * @param titulo título a ser definido
     * @throws TitleEmptyOrNullException caso o título seja nulo ou vazio
     */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new TitleEmptyOrNullException();
        }

        String tratado = titulo.trim();
        this.titulo = tratado.substring(0, 1).toUpperCase() + tratado.substring(1);
    }

    /**
     * Retorna a descrição da tarefa.
     *
     * @return descrição da tarefa
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição da tarefa, removendo espaços extras
     * e aplicando inicial maiúscula. Valores nulos ou vazios são
     * armazenados como uma string vazia.
     *
     * @param descricao descrição a ser definida
     */
    public void setDescricao(String descricao) {
        if (descricao != null && !descricao.isBlank()) {
            String tratado = descricao.trim();

            this.descricao = tratado.substring(0, 1).toUpperCase() + tratado.substring(1);
        } else {
            this.descricao = "";
        }
    }

    /**
     * Verifica se a tarefa está concluída.
     *
     * @return true se estiver concluída; false caso contrário
     */
    public boolean isConcluida() {
        return concluida;
    }

    /**
     * Define o estado de conclusão da tarefa.
     *
     * @param concluida novo estado de conclusão
     */
    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    /**
     * Gera a representação textual da tarefa e de suas subtarefas,
     * utilizando seu índice como identificador de exibição.
     *
     * @param indice posição da tarefa na listagem
     * @return representação textual da tarefa
     */
    public String exibirComoLinha(int indice) {
        return exibirComoLinha(indice, String.valueOf(indice), 0);
    }

    /**
     * Gera recursivamente a representação textual da tarefa e de suas
     * subtarefas, indicando status, identificação hierárquica e descrição.
     *
     * @param indice posição da tarefa
     * @param prefixo identificador hierárquico da tarefa
     * @param nivel nível de profundidade na hierarquia
     * @return representação textual formatada
     */
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

    /**
     * Adiciona uma subtarefa à lista de subtarefas da tarefa principal.
     *
     * @param subtarefa subtarefa a ser adicionada
     */
    public void adicionarSubtarefa(TaskPrioritaria subtarefa) {
        subtarefas.add(subtarefa);
    }

    /**
     * Retorna a lista de subtarefas vinculadas à tarefa.
     *
     * @return lista de subtarefas
     */
    public ArrayList<TaskPrioritaria> getSubtarefas() {
        return subtarefas;
    }
}