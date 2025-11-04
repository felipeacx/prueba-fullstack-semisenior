package com.fullstack.inventario.exception;

public class CantidadInsuficienteException extends RuntimeException {
    public CantidadInsuficienteException(String message) {
        super(message);
    }
}

