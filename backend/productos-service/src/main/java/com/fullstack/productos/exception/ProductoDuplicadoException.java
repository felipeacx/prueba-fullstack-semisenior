package com.fullstack.productos.exception;

public class ProductoDuplicadoException extends RuntimeException {
    public ProductoDuplicadoException(String message) {
        super(message);
    }
}

