package com.capacita.irede.controller;

import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class TaskManager<T extends Task> {
    private final List<T> listaTasks = new ArrayList<>();

    public void adicionar(T task) {
        listaTasks.add(task);
        System.out.println("Tarefa '" + task.getTitulo() + "' adicionada com sucesso!");
    }

    public T obterTarefa(int indice) {
        if (indice < 0 || indice >= listaTasks.size()) {
            return null;
        }

        return listaTasks.get(indice);
    }

    public List<T> getTarefas() {
        return List.copyOf(listaTasks);
    }

    public void listarPendentes() {
        System.out.println("\n===== TAREFAS PENDENTES =====");

        List<T> pendentes = filtrar(task -> !task.isConcluida());

        if (pendentes.isEmpty()) {
            System.out.println("Nenhuma tarefa pendente!");
            return;
        }

        for (int i = 0; i < pendentes.size(); i++) {
            System.out.println(pendentes.get(i).exibirComoLinha(i + 1));
        }
    }

    public void listarTarefas() {
        System.out.println("\n===== LISTAR TAREFAS =====");

        if (listaTasks.isEmpty()) {
            System.out.println("Lista de Tarefas vazia!");
            return;
        }

        for (int i = 0; i < listaTasks.size(); i++) {
            System.out.println(listaTasks.get(i).exibirComoLinha(i + 1));
        }
    }

    public void concluirTarefa(Scanner sc) {
        System.out.println("\n===== CONCLUIR TAREFA =====");
        listarTarefas();

        System.out.print("Digite o número da tarefa que deseja concluir: ");
        String identificador = sc.nextLine().trim();

        while (identificador.isEmpty()) {
            identificador = sc.nextLine().trim();
        }

        try {
            String[] indices = identificador.split("\\.");

            int idPai = Integer.parseInt(indices[0]);

            if (idPai < 1 || idPai > listaTasks.size()) {
                System.out.println("Tarefa não encontrada!");
                return;
            }

            Task task = listaTasks.get(idPai - 1);

            if (indices.length == 1) {
                task.setConcluida(true);
                System.out.println("Tarefa concluída com sucesso!");
                return;
            }

            for (int i = 1; i < indices.length; i++) {
                int idSubtarefa = Integer.parseInt(indices[i]);

                if (idSubtarefa < 1 ||
                        idSubtarefa > task.getSubtarefas().size()) {
                    System.out.println("Subtarefa não encontrada!");
                    return;
                }

                TaskPrioritaria subtarefa =
                        task.getSubtarefas().get(idSubtarefa - 1);

                if (i == indices.length - 1) {
                    subtarefa.setConcluida(true);
                    System.out.println("Subtarefa concluída com sucesso!");
                    return;
                }

                task = subtarefa;
            }

        } catch (NumberFormatException e) {
            System.out.println("Erro: digite um identificador válido, como 1 ou 1.1.");
        }
    }

    public void removerTarefa(Scanner sc) {
        System.out.println("\n===== REMOVER TAREFA =====");
        listarTarefas();

        System.out.print("Digite o número da tarefa que deseja remover: ");
        String identificador = sc.nextLine().trim();

        while (identificador.isEmpty()) {
            identificador = sc.nextLine().trim();
        }

        try {
            String[] indices = identificador.split("\\.");

            int idPai = Integer.parseInt(indices[0]);

            if (idPai < 1 || idPai > listaTasks.size()) {
                System.out.println("Tarefa não encontrada!");
                return;
            }

            Task task = listaTasks.get(idPai - 1);

            if (indices.length == 1) {
                listaTasks.remove(task);
                System.out.println("Tarefa removida com sucesso!");
                return;
            }

            for (int i = 1; i < indices.length; i++) {
                int idSubtarefa = Integer.parseInt(indices[i]);

                if (idSubtarefa < 1 ||
                        idSubtarefa > task.getSubtarefas().size()) {
                    System.out.println("Subtarefa não encontrada!");
                    return;
                }

                TaskPrioritaria subtarefa =
                        task.getSubtarefas().get(idSubtarefa - 1);

                if (i == indices.length - 1) {
                    task.getSubtarefas().remove(subtarefa);
                    System.out.println("Subtarefa removida com sucesso!");
                    return;
                }

                task = subtarefa;
            }

        } catch (NumberFormatException e) {
            System.out.println("Erro: digite um identificador válido, como 1 ou 1.1.");
        }
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