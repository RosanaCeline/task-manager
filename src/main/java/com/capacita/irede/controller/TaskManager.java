package com.capacita.irede.controller;

import com.capacita.irede.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TaskManager<T extends Task> {
    private final List<T> listaTasks = new ArrayList<>();

    public void adicionar(T task) {
        listaTasks.add(task);
        System.out.println("Tarefa '" + task.getTitulo() + "' adicionada com sucesso!");
    }

    public List<T> getTarefas() {
        return List.copyOf(listaTasks);
    }

    public void concluir(T task) {
        task.setConcluida(true);
    }

    public boolean remover(T task) {
        return listaTasks.remove(task);
    }

    private List<T> filtrar(Predicate<? super T> criterio) {
        List<T> resultado = new ArrayList<>();
        for (T task : listaTasks) {
            if (criterio.test(task)) {
                resultado.add(task);
            }
        }
        return resultado;
    }
}