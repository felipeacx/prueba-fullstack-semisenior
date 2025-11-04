package com.fullstack.inventario.service;

import com.fullstack.inventario.client.ProductoClient;
import com.fullstack.inventario.domain.Inventario;
import com.fullstack.inventario.dto.InventarioDTO;
import com.fullstack.inventario.event.InventarioEventPublisher;
import com.fullstack.inventario.exception.CantidadInsuficienteException;
import com.fullstack.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class InventarioServiceIntegrationTest {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioRepository inventarioRepository;

    @MockBean
    private ProductoClient productoClient;

    @Autowired
    private InventarioEventPublisher eventPublisher;

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
    void testFlujoCmpletoCreacionYCompra() {
        Map<String, Object> productoResponse = new HashMap<>();
        productoResponse.put("id", 1);
        productoResponse.put("codigo", "PROD001");

        when(productoClient.obtenerProducto(1L)).thenReturn(productoResponse);

        InventarioDTO inventarioCreado = inventarioService.crearInventario(inventarioDTO);

        assertNotNull(inventarioCreado);
        assertEquals(1L, inventarioCreado.getProductoId());
        assertEquals(100, inventarioCreado.getCantidad());

        InventarioDTO inventarioConCompra = inventarioService.realizarCompra(1L, 30);

        assertNotNull(inventarioConCompra);
        assertEquals(70, inventarioConCompra.getCantidad());

        InventarioDTO inventarioActual = inventarioService.obtenerInventarioPorProductoId(1L);

        assertEquals(70, inventarioActual.getCantidad());
    }

    @Test
    void testMultiplesComprasSecuenciales() {
        Map<String, Object> productoResponse = new HashMap<>();
        when(productoClient.obtenerProducto(1L)).thenReturn(productoResponse);

        inventarioService.crearInventario(inventarioDTO);

        InventarioDTO despuesPrimeraCompra = inventarioService.realizarCompra(1L, 25);
        assertEquals(75, despuesPrimeraCompra.getCantidad());

        InventarioDTO despuesSegundaCompra = inventarioService.realizarCompra(1L, 25);
        assertEquals(50, despuesSegundaCompra.getCantidad());

        InventarioDTO despuesTerceraCompra = inventarioService.realizarCompra(1L, 25);
        assertEquals(25, despuesTerceraCompra.getCantidad());

        InventarioDTO inventarioFinal = inventarioService.obtenerInventarioPorProductoId(1L);
        assertEquals(25, inventarioFinal.getCantidad());
    }

    @Test
    void testComunicacionFallidaConProductoService() {
        when(productoClient.obtenerProducto(1L)).thenThrow(new RuntimeException("Servicio de productos no disponible"));

        assertThrows(RuntimeException.class, () -> {
            inventarioService.crearInventario(inventarioDTO);
        });

        assertFalse(inventarioRepository.findByProductoId(1L).isPresent());
    }

    @Test
    void testValidacionDeCantidadMinima() {
        Map<String, Object> productoResponse = new HashMap<>();
        when(productoClient.obtenerProducto(1L)).thenReturn(productoResponse);

        InventarioDTO inventarioMinimo = InventarioDTO.builder()
                .productoId(1L)
                .cantidad(20)
                .cantidadMinima(10)
                .build();

        inventarioService.crearInventario(inventarioMinimo);

        assertThrows(CantidadInsuficienteException.class, () -> {
            inventarioService.realizarCompra(1L, 25);
        });
    }

    @Test
    void testActualizacionDeCantidadDirecta() {
        
        Map<String, Object> productoResponse = new HashMap<>();
        when(productoClient.obtenerProducto(1L)).thenReturn(productoResponse);

        inventarioService.crearInventario(inventarioDTO);

        InventarioDTO inventarioActualizado = inventarioService.actualizarCantidad(1L, 250);

        assertEquals(250, inventarioActualizado.getCantidad());

        InventarioDTO verificacion = inventarioService.obtenerInventarioPorProductoId(1L);
        assertEquals(250, verificacion.getCantidad());
    }

    @Test
    void testCicloCompletoConMultiplosInventarios() {
        
        Map<String, Object> productoResponse1 = new HashMap<>();
        productoResponse1.put("id", 1);

        Map<String, Object> productoResponse2 = new HashMap<>();
        productoResponse2.put("id", 2);

        when(productoClient.obtenerProducto(1L)).thenReturn(productoResponse1);
        when(productoClient.obtenerProducto(2L)).thenReturn(productoResponse2);

        InventarioDTO inv1 = inventarioService.crearInventario(
                InventarioDTO.builder().productoId(1L).cantidad(100).cantidadMinima(10).build()
        );

        InventarioDTO inv2 = inventarioService.crearInventario(
                InventarioDTO.builder().productoId(2L).cantidad(50).cantidadMinima(5).build()
        );

        assertNotNull(inv1);
        assertNotNull(inv2);

        inventarioService.realizarCompra(1L, 20);
        inventarioService.realizarCompra(2L, 15);

        InventarioDTO estado1 = inventarioService.obtenerInventarioPorProductoId(1L);
        InventarioDTO estado2 = inventarioService.obtenerInventarioPorProductoId(2L);

        assertEquals(80, estado1.getCantidad());
        assertEquals(35, estado2.getCantidad());
    }
}

