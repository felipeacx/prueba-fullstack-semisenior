package com.fullstack.inventario.client;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Map;

@Component
@Slf4j
public class ProductoClient {

    private final RestTemplate restTemplate;
    private final String productosServiceUrl;
    private final String apiKey;

    public ProductoClient(RestTemplate restTemplate,
                         @Value("${productos.service.url:http://localhost:8081}") String productosServiceUrl,
                         @Value("${app.api.key:secret-key-productos}") String apiKey) {
        this.restTemplate = restTemplate;
        this.productosServiceUrl = productosServiceUrl;
        this.apiKey = apiKey;
    }

    @Retry(name = "productoRetry", fallbackMethod = "fallbackObtenerProducto")
    @CircuitBreaker(name = "productoCircuitBreaker", fallbackMethod = "fallbackObtenerProducto")
    public Map<String, Object> obtenerProducto(Long productoId) {
        log.info("Obteniendo producto {} del microservicio de productos", productoId);
        try {
            String url = productosServiceUrl + "/api/v1/productos/" + productoId;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            log.error("Error al obtener producto {}: {}", productoId, e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> fallbackObtenerProducto(Long productoId, Exception e) {
        log.warn("Fallback: No se pudo obtener el producto {}. Error: {}", productoId, e.getMessage());
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("error", "No se pudo obtener el producto del servicio de productos");
        response.put("productoId", productoId);
        return response;
    }
}

