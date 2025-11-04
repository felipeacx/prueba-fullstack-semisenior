package com.fullstack.inventario.service;

import com.fullstack.inventario.client.ProductoClient;
import com.fullstack.inventario.domain.Inventario;
import com.fullstack.inventario.dto.InventarioDTO;
import com.fullstack.inventario.event.InventarioEventPublisher;
import com.fullstack.inventario.exception.CantidadInsuficienteException;
import com.fullstack.inventario.exception.InventarioNotFoundException;
import com.fullstack.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioEventPublisher eventPublisher;

    @InjectMocks
    private InventarioService inventarioService;

    private InventarioDTO inventarioDTO;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventarioDTO = InventarioDTO.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();

        inventario = Inventario.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(100)
                .cantidadMinima(10)
                .build();
    }

    @Test
    void testCrearInventarioExitoso() {
        Map<String, Object> productoResponse = new HashMap<>();
        productoResponse.put("id", 1);

        when(productoClient.obtenerProducto(1L)).thenReturn(productoResponse);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        doNothing().when(eventPublisher).publicarEvento(any());

        InventarioDTO resultado = inventarioService.crearInventario(inventarioDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getProductoId());
        assertEquals(100, resultado.getCantidad());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
        verify(eventPublisher, times(1)).publicarEvento(any());
    }

    @Test
    void testCrearInventarioProductoNoEncontrado() {
        when(productoClient.obtenerProducto(1L)).thenThrow(new RuntimeException("Producto no encontrado"));

        assertThrows(RuntimeException.class, () -> {
            inventarioService.crearInventario(inventarioDTO);
        });

        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testObtenerInventarioPorProductoIdExitoso() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));

        InventarioDTO resultado = inventarioService.obtenerInventarioPorProductoId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getProductoId());
        assertEquals(100, resultado.getCantidad());
    }

    @Test
    void testObtenerInventarioPorProductoIdNoEncontrado() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        assertThrows(InventarioNotFoundException.class, () -> {
            inventarioService.obtenerInventarioPorProductoId(1L);
        });
    }

    @Test
    void testObtenerInventarioPorIdExitoso() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        InventarioDTO resultado = inventarioService.obtenerInventarioPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerInventarioPorIdNoEncontrado() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InventarioNotFoundException.class, () -> {
            inventarioService.obtenerInventarioPorId(1L);
        });
    }

    @Test
    void testRealizarCompraExitosa() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        doNothing().when(eventPublisher).publicarEvento(any());

        InventarioDTO resultado = inventarioService.realizarCompra(1L, 50);

        assertNotNull(resultado);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
        verify(eventPublisher, times(1)).publicarEvento(any());
    }

    @Test
    void testRealizarCompraCantidadInsuficiente() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));

        assertThrows(CantidadInsuficienteException.class, () -> {
            inventarioService.realizarCompra(1L, 150);
        });

        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testRealizarCompraInventarioNoEncontrado() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        assertThrows(InventarioNotFoundException.class, () -> {
            inventarioService.realizarCompra(1L, 50);
        });
    }

    @Test
    void testActualizarCantidad() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        doNothing().when(eventPublisher).publicarEvento(any());

        InventarioDTO resultado = inventarioService.actualizarCantidad(1L, 200);

        assertNotNull(resultado);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
        verify(eventPublisher, times(1)).publicarEvento(any());
    }

    @Test
    void testListarInventarios() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Inventario> inventariosPage = new PageImpl<>(Arrays.asList(inventario));

        when(inventarioRepository.findAll(pageable)).thenReturn(inventariosPage);

        Page<InventarioDTO> resultado = inventarioService.listarInventarios(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(inventarioRepository, times(1)).findAll(pageable);
    }
}

