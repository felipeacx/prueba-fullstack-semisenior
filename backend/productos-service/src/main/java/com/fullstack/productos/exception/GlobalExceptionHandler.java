package com.fullstack.productos.exception;

import com.fullstack.productos.dto.JsonApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<JsonApiResponse<?>> handleProductoNotFound(ProductoNotFoundException ex) {
        log.error("Producto no encontrado: {}", ex.getMessage());

        JsonApiResponse.JsonApiError error = JsonApiResponse.JsonApiError.builder()
                .status(404)
                .code("NOT_FOUND")
                .title("Producto No Encontrado")
                .detail(ex.getMessage())
                .build();

        JsonApiResponse<?> response = JsonApiResponse.builder()
                .errors(java.util.Arrays.asList(error))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<JsonApiResponse<?>> handleProductoDuplicado(ProductoDuplicadoException ex) {
        log.error("Producto duplicado: {}", ex.getMessage());

        JsonApiResponse.JsonApiError error = JsonApiResponse.JsonApiError.builder()
                .status(409)
                .code("CONFLICT")
                .title("Producto Duplicado")
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

