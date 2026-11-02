package com.capacita.irede.exceptions;

public class TitleEmptyOrNullException extends IllegalArgumentException {
    public TitleEmptyOrNullException() {
        super("O título não pode ser vazio ou nulo.");
    }

    public TitleEmptyOrNullException(String message) {
        super(message);
    }
}
