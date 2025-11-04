package com.fullstack.productos.controller;

import com.fullstack.productos.domain.Producto;
import com.fullstack.productos.dto.ProductoDTO;
import com.fullstack.productos.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String apiKey = "secret-key-productos";
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

        productoDTO = ProductoDTO.builder()
                .codigo("PROD001")
                .nombre("Laptop")
                .descripcion("Laptop de prueba")
                .precio(BigDecimal.valueOf(999.99))
                .categoria("Electrónica")
                .build();
    }

    @Test
    void testCrearProductoExitoso() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.codigo").value("PROD001"))
                .andExpect(jsonPath("$.data.nombre").value("Laptop"));
    }

    @Test
    void testCrearProductoSinApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testObtenerProductoExitoso() throws Exception {
        Producto producto = Producto.builder()
                .codigo("PROD001")
                .nombre("Laptop")
                .descripcion("Laptop de prueba")
                .precio(BigDecimal.valueOf(999.99))
                .categoria("Electrónica")
                .build();
        Producto productoGuardado = productoRepository.save(producto);

        mockMvc.perform(get("/api/v1/productos/" + productoGuardado.getId())
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.codigo").value("PROD001"));
    }

    @Test
    void testObtenerProductoNoEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/productos/999")
                .header("X-API-Key", apiKey))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].code").value("NOT_FOUND"));
    }

    @Test
    void testActualizarProductoExitoso() throws Exception {
        Producto producto = Producto.builder()
                .codigo("PROD001")
                .nombre("Laptop")
                .descripcion("Laptop de prueba")
                .precio(BigDecimal.valueOf(999.99))
                .categoria("Electrónica")
                .build();
        Producto productoGuardado = productoRepository.save(producto);

        ProductoDTO actualizado = ProductoDTO.builder()
                .codigo("PROD001")
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripción")
                .precio(BigDecimal.valueOf(1199.99))
                .categoria("Electrónica")
                .build();

        mockMvc.perform(put("/api/v1/productos/" + productoGuardado.getId())
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Laptop Actualizada"));
    }

    @Test
    void testEliminarProductoExitoso() throws Exception {
        Producto producto = Producto.builder()
                .codigo("PROD001")
                .nombre("Laptop")
                .descripcion("Laptop de prueba")
                .precio(BigDecimal.valueOf(999.99))
                .categoria("Electrónica")
                .build();
        Producto productoGuardado = productoRepository.save(producto);

        mockMvc.perform(delete("/api/v1/productos/" + productoGuardado.getId())
                .header("X-API-Key", apiKey))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/productos/" + productoGuardado.getId())
                .header("X-API-Key", apiKey))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListarProductos() throws Exception {
        Producto producto1 = Producto.builder()
                .codigo("PROD001")
                .nombre("Laptop")
                .descripcion("Laptop de prueba")
                .precio(BigDecimal.valueOf(999.99))
                .categoria("Electrónica")
                .build();

        Producto producto2 = Producto.builder()
                .codigo("PROD002")
                .nombre("Mouse")
                .descripcion("Mouse de prueba")
                .precio(BigDecimal.valueOf(29.99))
                .categoria("Accesorios")
                .build();

        productoRepository.save(producto1);
        productoRepository.save(producto2);

        mockMvc.perform(get("/api/v1/productos?page=0&size=10")
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.meta.total").value(2));
    }

    @Test
    void testObtenerProductoPorCodigo() throws Exception {
        Producto producto = Producto.builder()
                .codigo("PROD001")
                .nombre("Laptop")
                .descripcion("Laptop de prueba")
                .precio(BigDecimal.valueOf(999.99))
                .categoria("Electrónica")
                .build();
        productoRepository.save(producto);

        mockMvc.perform(get("/api/v1/productos/codigo/PROD001")
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.codigo").value("PROD001"))
                .andExpect(jsonPath("$.data.nombre").value("Laptop"));
    }

    @Test
    void testCrearProductoDuplicado() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/productos")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("CONFLICT"));
    }
}

