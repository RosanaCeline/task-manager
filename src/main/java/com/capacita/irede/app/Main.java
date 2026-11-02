package com.capacita.irede.app;

import com.capacita.irede.controller.TaskManager;
import com.capacita.irede.model.Tarefa;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskManager<Tarefa> taskManager = new TaskManager<>();

        try {
            int opcao = 0;

            do {
                System.out.println("===== GERENCIADOR DE TAREFAS =====");
                System.out.println("1. Criar nova tarefa");
                System.out.println("2. Listar tarefas");
                System.out.println("3. Marcar tarefa como concluída");
                System.out.println("4. Remover tarefa");
                System.out.println("5. Sair");
                System.out.println("Escolha uma opção:");
                opcao = sc.nextInt();

                switch(opcao) {
                    case 1:
                        taskManager.adicionarTarefa(sc);
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
                        break;
                    default:
                        System.out.println("Opção inválida!\n");
                        break;
                }
                System.out.println();
            } while (opcao != 5);
        } catch (InputMismatchException e) {
            System.out.println("Erro: digite um número inteiro válido.");
            sc.nextLine();
        } finally {
            sc.close();
        }
    }
}