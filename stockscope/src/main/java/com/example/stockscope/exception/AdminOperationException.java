package com.example.stockscope.exception;

public class AdminOperationException extends RuntimeException {
    public AdminOperationException(String message) {
        super(message);
    }

    public AdminOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}