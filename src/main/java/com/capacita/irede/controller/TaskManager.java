package com.capacita.irede.controller;

import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import com.capacita.irede.model.Tarefa;
import com.capacita.irede.model.TarefaPrioritaria;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskManager<T extends Tarefa> {
    private final ArrayList<Tarefa> listaTarefas = new ArrayList<>();

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
            Tarefa tarefaPai = null;

            if (opcao == 2) {
                if (listaTarefas.isEmpty()) {
                    System.out.println("Não existem tarefas para associar como pai.");
                    return;
                }

                listarTarefas();

                System.out.print("\nDigite o número da tarefa principal: ");
                int idPai = sc.nextInt();
                sc.nextLine();

                if (idPai < 1 || idPai > listaTarefas.size()) {
                    System.out.println("Tarefa principal não encontrada!");
                    return;
                }

                tarefaPai = listaTarefas.get(idPai - 1);

                if (tarefaPai instanceof TarefaPrioritaria) {
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
                Tarefa tarefa = new Tarefa(titulo, descricao);
                listaTarefas.add(tarefa);

                System.out.println("Tarefa '" + tarefa.getTitulo() + "' adicionada com sucesso!");
            } else {
                TarefaPrioritaria subtarefa =
                        new TarefaPrioritaria(titulo, descricao, tarefaPai);

                tarefaPai.adicionarSubtarefa(subtarefa);

                System.out.println("Subtarefa '" + subtarefa.getTitulo() + "' adicionada à tarefa '" + tarefaPai.getTitulo() + "' com sucesso!");
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

        if(listaTarefas.isEmpty()) {
            System.out.println("Lista de Tarefas vazia!");
            return;
        }

        for (int i = 0; i < listaTarefas.size(); i++) {
            System.out.println(listaTarefas.get(i).exibirComoLinha(i + 1));
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

            if (idPai < 1 || idPai > listaTarefas.size()) {
                System.out.println("Tarefa não encontrada!");
                return;
            }

            Tarefa tarefa = listaTarefas.get(idPai - 1);

            if (indices.length == 1) {
                tarefa.setConcluida(true);
                System.out.println("Tarefa concluída com sucesso!");
                return;
            }

            for (int i = 1; i < indices.length; i++) {
                int idSubtarefa = Integer.parseInt(indices[i]);

                if (idSubtarefa < 1 ||
                        idSubtarefa > tarefa.getSubtarefas().size()) {
                    System.out.println("Subtarefa não encontrada!");
                    return;
                }

                TarefaPrioritaria subtarefa =
                        tarefa.getSubtarefas().get(idSubtarefa - 1);

                if (i == indices.length - 1) {
                    subtarefa.setConcluida(true);
                    System.out.println("Subtarefa concluída com sucesso!");
                    return;
                }

                tarefa = subtarefa;
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

            if (idPai < 1 || idPai > listaTarefas.size()) {
                System.out.println("Tarefa não encontrada!");
                return;
            }

            Tarefa tarefa = listaTarefas.get(idPai - 1);

            if (indices.length == 1) {
                listaTarefas.remove(tarefa);
                System.out.println("Tarefa removida com sucesso!");
                return;
            }

            for (int i = 1; i < indices.length; i++) {
                int idSubtarefa = Integer.parseInt(indices[i]);

                if (idSubtarefa < 1 ||
                        idSubtarefa > tarefa.getSubtarefas().size()) {
                    System.out.println("Subtarefa não encontrada!");
                    return;
                }

                TarefaPrioritaria subtarefa =
                        tarefa.getSubtarefas().get(idSubtarefa - 1);

                if (i == indices.length - 1) {
                    tarefa.getSubtarefas().remove(subtarefa);
                    System.out.println("Subtarefa removida com sucesso!");
                    return;
                }

                tarefa = subtarefa;
            }

        } catch (NumberFormatException e) {
            System.out.println(
                    "Erro: digite um identificador válido, como 1 ou 1.1."
            );
        }
    }
}
