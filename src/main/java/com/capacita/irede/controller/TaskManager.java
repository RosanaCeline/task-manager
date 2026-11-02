package com.capacita.irede.controller;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskManager<T extends Task> {
    private final ArrayList<Task> listaTasks = new ArrayList<>();

    public void adicionarTarefa(Scanner sc) {
        try {
            System.out.println("\n===== CRIAR NOVA TAREFA =====");
            System.out.println("1. Tarefa principal");
            System.out.println("2. Subtarefa");
            System.out.print("Escolha uma opção: ");

            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao != 1 && opcao != 2) {
                System.out.println("Opção inválida!");
                return;
            }
            Task taskPai = null;

            if (opcao == 2) {
                if (listaTasks.isEmpty()) {
                    System.out.println("Não existem tarefas para associar como pai.");
                    return;
                }

                listarTarefas();

                System.out.print("\nDigite o número da tarefa principal: ");
                int idPai = sc.nextInt();
                sc.nextLine();

                if (idPai < 1 || idPai > listaTasks.size()) {
                    System.out.println("Tarefa principal não encontrada!");
                    return;
                }

                taskPai = listaTasks.get(idPai - 1);

                if (taskPai instanceof TaskPrioritaria) {
                    System.out.println("Escolha uma tarefa principal, não uma subtarefa.");
                    return;
                }
            }

            String titulo;

            System.out.println("Digite o título da tarefa (Obrigatório):");
            titulo = sc.nextLine();

            System.out.println("Digite a descrição da tarefa:");
            String descricao = sc.nextLine();

            if (opcao == 1) {
                Task task = new Task(titulo, descricao);
                listaTasks.add(task);

                System.out.println("Tarefa '" + task.getTitulo() + "' adicionada com sucesso!");
            } else {
                TaskPrioritaria subtarefa =
                        new TaskPrioritaria(titulo, descricao, taskPai);

                taskPai.adicionarSubtarefa(subtarefa);

                System.out.println("Subtarefa '" + subtarefa.getTitulo() + "' adicionada à tarefa '" + taskPai.getTitulo() + "' com sucesso!");
            }
        } catch (TitleEmptyOrNullException e) {
            System.err.println("Erro ao salvar tarefa: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Erro: digite um número inteiro válido.");
            sc.nextLine();
        }
    }

    public void listarTarefas() {
        System.out.println("\n===== LISTAR TAREFAS =====");

        if(listaTasks.isEmpty()) {
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
            System.out.println(
                    "Erro: digite um identificador válido, como 1 ou 1.1."
            );
        }
    }
}
