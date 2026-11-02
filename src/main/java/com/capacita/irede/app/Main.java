package com.capacita.irede.app;

import com.capacita.irede.controller.TaskManager;
import com.capacita.irede.exceptions.TitleEmptyOrNullException;
import com.capacita.irede.model.Task;
import com.capacita.irede.model.TaskPrioritaria;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskManager<Task> taskManager = new TaskManager<>();

        try {
            int opcao = 0;

            do {
                System.out.println("===== GERENCIADOR DE TAREFAS =====");
                System.out.println("1. Criar nova tarefa");
                System.out.println("2. Listar tarefas");
                System.out.println("3. Marcar tarefa como concluída");
                System.out.println("4. Remover tarefa");
                System.out.println("5. Listar pendentes");
                System.out.println("6. Sair");
                System.out.println("Escolha uma opção:");
                opcao = sc.nextInt();

                switch (opcao) {
                    case 1:
                        try {
                            System.out.println("\n===== CRIAR NOVA TAREFA =====");
                            System.out.println("1. Tarefa principal");
                            System.out.println("2. Subtarefa");
                            System.out.print("Escolha uma opção: ");

                            int opcao1 = sc.nextInt();
                            sc.nextLine();

                            if (opcao1 != 1 && opcao1 != 2) {
                                System.out.println("Opção inválida!");
                                break;
                            }

                            Task taskPai = null;

                            // Se for subtarefa, seleciona a tarefa principal
                            if (opcao1 == 2) {
                                if (taskManager.getTarefas().isEmpty()) {
                                    System.out.println(
                                            "Não existem tarefas para associar como pai."
                                    );
                                    break;
                                }

                                taskManager.listarTarefas();

                                System.out.print("\nDigite o número da tarefa principal: ");
                                int idPai = sc.nextInt();
                                sc.nextLine();

                                taskPai = taskManager.obterTarefa(idPai - 1);

                                if (taskPai == null) {
                                    System.out.println("Tarefa principal não encontrada!");
                                    break;
                                }

                                if (taskPai instanceof TaskPrioritaria) {
                                    System.out.println(
                                            "Escolha uma tarefa principal, não uma subtarefa."
                                    );
                                    break;
                                }
                            }

                            System.out.print("Digite o título da tarefa (Obrigatório): ");
                            String titulo = sc.nextLine();

                            System.out.print("Digite a descrição da tarefa: ");
                            String descricao = sc.nextLine();

                            if (opcao1 == 1) {
                                Task task = new Task(titulo, descricao);

                                taskManager.adicionar(task);

                            } else {
                                TaskPrioritaria subtarefa =
                                        new TaskPrioritaria(titulo, descricao, taskPai);

                                taskPai.adicionarSubtarefa(subtarefa);

                                System.out.println(
                                        "Subtarefa '" + subtarefa.getTitulo()
                                                + "' adicionada à tarefa '"
                                                + taskPai.getTitulo() + "' com sucesso!"
                                );
                            }

                        } catch (TitleEmptyOrNullException e) {
                            System.err.println("Erro ao salvar tarefa: " + e.getMessage());

                        } catch (InputMismatchException e) {
                            System.out.println("Erro: digite um número inteiro válido.");
                            sc.nextLine();
                        }

                        break;
                    case 2:
                        taskManager.listarTarefas();
                        break;
                    case 3:
                        taskManager.concluirTarefa(sc);
                        break;
                    case 4:
                        taskManager.removerTarefa(sc);
                        break;
                    case 5:
                        taskManager.listarPendentes();
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("Opção inválida!\n");
                        break;
                }
                System.out.println();
            } while (opcao != 6);
        } catch (InputMismatchException e) {
            System.out.println("Erro: digite um número inteiro válido.");
            sc.nextLine();
        } finally {
            sc.close();
        }
    }
}