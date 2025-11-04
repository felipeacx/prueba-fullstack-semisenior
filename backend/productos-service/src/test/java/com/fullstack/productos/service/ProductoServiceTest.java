package com.fullstack.productos.service;

import com.fullstack.productos.domain.Producto;
import com.fullstack.productos.dto.ProductoDTO;
import com.fullstack.productos.exception.ProductoDuplicadoException;
import com.fullstack.productos.exception.ProductoNotFoundException;
import com.fullstack.productos.repository.ProductoRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoDTO productoDTO;
    private Producto producto;

    @BeforeEach
    void setUp() {
        productoDTO = ProductoDTO.builder()
                .id(1L)
                .codigo("PROD001")
                .nombre("Producto Test")
                .descripcion("Descripción test")
                .precio(BigDecimal.valueOf(99.99))
                .categoria("Electrónica")
                .build();

        producto = Producto.builder()
                .id(1L)
                .codigo("PROD001")
                .nombre("Producto Test")
                .descripcion("Descripción test")
                .precio(BigDecimal.valueOf(99.99))
                .categoria("Electrónica")
                .build();
    }

    @Test
    void testCrearProductoExitoso() {
        when(productoRepository.findByCodigo("PROD001")).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO resultado = productoService.crearProducto(productoDTO);

        assertNotNull(resultado);
        assertEquals("PROD001", resultado.getCodigo());
        assertEquals("Producto Test", resultado.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testCrearProductoDuplicado() {
        when(productoRepository.findByCodigo("PROD001")).thenReturn(Optional.of(producto));

        assertThrows(ProductoDuplicadoException.class, () -> {
            productoService.crearProducto(productoDTO);
        });

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testObtenerProductoPorIdExitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoDTO resultado = productoService.obtenerProductoporId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PROD001", resultado.getCodigo());
    }

    @Test
    void testObtenerProductoPorIdNoEncontrado() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.obtenerProductoporId(1L);
        });
    }

    @Test
    void testObtenerProductoPorCodigoExitoso() {
        when(productoRepository.findByCodigo("PROD001")).thenReturn(Optional.of(producto));

        ProductoDTO resultado = productoService.obtenerProductoPorCodigo("PROD001");

        assertNotNull(resultado);
        assertEquals("PROD001", resultado.getCodigo());
    }

    @Test
    void testObtenerProductoPorCodigoNoEncontrado() {
        when(productoRepository.findByCodigo("PROD001")).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.obtenerProductoPorCodigo("PROD001");
        });
    }

    @Test
    void testActualizarProductoExitoso() {
        ProductoDTO actualizadoDTO = ProductoDTO.builder()
                .codigo("PROD001")
                .nombre("Producto Actualizado")
                .descripcion("Nueva descripción")
                .precio(BigDecimal.valueOf(149.99))
                .categoria("Electrónica")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO resultado = productoService.actualizarProducto(1L, actualizadoDTO);

        assertNotNull(resultado);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testActualizarProductoCodigoDuplicado() {
        Producto producto2 = Producto.builder()
                .id(2L)
                .codigo("PROD002")
                .nombre("Producto 2")
                .descripcion("Descripción 2")
                .precio(BigDecimal.valueOf(50.00))
                .categoria("Electrónica")
                .build();

        ProductoDTO actualizadoDTO = ProductoDTO.builder()
                .codigo("PROD002")
                .nombre("Producto Actualizado")
                .descripcion("Nueva descripción")
                .precio(BigDecimal.valueOf(149.99))
                .categoria("Electrónica")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.findByCodigo("PROD002")).thenReturn(Optional.of(producto2));

        assertThrows(ProductoDuplicadoException.class, () -> {
            productoService.actualizarProducto(1L, actualizadoDTO);
        });

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testActualizarProductoNoEncontrado() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.actualizarProducto(1L, productoDTO);
        });
    }

    @Test
    void testEliminarProductoExitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).delete(any(Producto.class));

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).delete(any(Producto.class));
    }

    @Test
    void testEliminarProductoNoEncontrado() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.eliminarProducto(1L);
        });
    }

    @Test
    void testListarProductos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> productosPage = new PageImpl<>(Arrays.asList(producto));

        when(productoRepository.findAll(pageable)).thenReturn(productosPage);

        Page<ProductoDTO> resultado = productoService.listarProductos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(productoRepository, times(1)).findAll(pageable);
    }
}

