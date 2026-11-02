package com.capacita.irede.exceptions;

public class TaskTitleTooLongException extends Exception {

    public TaskTitleTooLongException() {
        super("O título da tarefa ultrapassa o limite de 100 caracteres.");
    }
}