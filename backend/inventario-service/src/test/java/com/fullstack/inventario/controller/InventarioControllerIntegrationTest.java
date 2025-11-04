package com.fullstack.inventario.controller;

import com.fullstack.inventario.domain.Inventario;
import com.fullstack.inventario.dto.InventarioDTO;
import com.fullstack.inventario.repository.InventarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String apiKey = "secret-key-inventario";
    private InventarioDTO inventarioDTO;

    @BeforeEach
    void setUp() {
        inventarioRepository.deleteAll();

        inventarioDTO = InventarioDTO.builder()
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();
    }

    @Test
    void testCrearInventarioExitoso() throws Exception {
        mockMvc.perform(post("/api/v1/inventarios")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.productoId").value(1))
                .andExpect(jsonPath("$.data.cantidad").value(100));
    }

    @Test
    void testCrearInventarioSinApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/inventarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testObtenerInventarioPorIdExitoso() throws Exception {
        Inventario inventario = Inventario.builder()
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        mockMvc.perform(get("/api/v1/inventarios/" + inventarioGuardado.getId())
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.productoId").value(1));
    }

    @Test
    void testObtenerInventarioPorIdNoEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/inventarios/999")
                .header("X-API-Key", apiKey))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].code").value("NOT_FOUND"));
    }

    @Test
    void testRealizarCompraExitosa() throws Exception {
        Inventario inventario = Inventario.builder()
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        mockMvc.perform(post("/api/v1/inventarios/compra/1?cantidad=50")
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cantidad").value(50));
    }

    @Test
    void testRealizarCompraCantidadInsuficiente() throws Exception {
        Inventario inventario = Inventario.builder()
                .productoId(1L)
                .cantidad(30)
                .cantidadMinima(10)
                .build();
        inventarioRepository.save(inventario);

        mockMvc.perform(post("/api/v1/inventarios/compra/1?cantidad=50")
                .header("X-API-Key", apiKey))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("INSUFFICIENT_QUANTITY"));
    }

    @Test
    void testActualizarCantidad() throws Exception {
        Inventario inventario = Inventario.builder()
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();
        inventarioRepository.save(inventario);

        mockMvc.perform(put("/api/v1/inventarios/cantidad/1?cantidad=200")
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cantidad").value(200));
    }

    @Test
    void testListarInventarios() throws Exception {
        Inventario inventario1 = Inventario.builder()
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();

        Inventario inventario2 = Inventario.builder()
                .productoId(2L)
                .cantidad(50)
                .cantidadMinima(5)
                .build();

        inventarioRepository.save(inventario1);
        inventarioRepository.save(inventario2);

        mockMvc.perform(get("/api/v1/inventarios?page=0&size=10")
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.meta.total").value(2));
    }

    @Test
    void testObtenerInventarioPorProductoId() throws Exception {
        Inventario inventario = Inventario.builder()
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();
        inventarioRepository.save(inventario);

        mockMvc.perform(get("/api/v1/inventarios/producto/1")
                .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productoId").value(1))
                .andExpect(jsonPath("$.data.cantidad").value(100));
    }
}

