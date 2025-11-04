package com.fullstack.inventario.exception;

import com.fullstack.inventario.dto.JsonApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InventarioNotFoundException.class)
    public ResponseEntity<JsonApiResponse<?>> handleInventarioNotFound(InventarioNotFoundException ex) {
        log.error("Inventario no encontrado: {}", ex.getMessage());

        JsonApiResponse.JsonApiError error = JsonApiResponse.JsonApiError.builder()
                .status(404)
                .code("NOT_FOUND")
                .title("Inventario No Encontrado")
                .detail(ex.getMessage())
                .build();

        JsonApiResponse<?> response = JsonApiResponse.builder()
                .errors(java.util.Arrays.asList(error))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CantidadInsuficienteException.class)
    public ResponseEntity<JsonApiResponse<?>> handleCantidadInsuficiente(CantidadInsuficienteException ex) {
        log.error("Cantidad insuficiente: {}", ex.getMessage());

        JsonApiResponse.JsonApiError error = JsonApiResponse.JsonApiError.builder()
                .status(409)
                .code("INSUFFICIENT_QUANTITY")
                .title("Cantidad Insuficiente")
                .detail(ex.getMessage())
                .build();

        JsonApiResponse<?> response = JsonApiResponse.builder()
                .errors(java.util.Arrays.asList(error))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonApiResponse<?>> handleGeneralException(Exception ex) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);

        JsonApiResponse.JsonApiError error = JsonApiResponse.JsonApiError.builder()
                .status(500)
                .code("INTERNAL_ERROR")
                .title("Error Interno")
                .detail("Ocurrió un error al procesar la solicitud")
                .build();

        JsonApiResponse<?> response = JsonApiResponse.builder()
                .errors(java.util.Arrays.asList(error))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

