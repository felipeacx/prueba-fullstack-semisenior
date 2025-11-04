package com.fullstack.productos.service;

import com.fullstack.productos.domain.Producto;
import com.fullstack.productos.dto.ProductoDTO;
import com.fullstack.productos.exception.ProductoDuplicadoException;
import com.fullstack.productos.exception.ProductoNotFoundException;
import com.fullstack.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        log.info("Creando producto con código: {}", productoDTO.getCodigo());

        if (productoRepository.findByCodigo(productoDTO.getCodigo()).isPresent()) {
            throw new ProductoDuplicadoException("Producto con código " + productoDTO.getCodigo() + " ya existe");
        }

        Producto producto = Producto.builder()
                .codigo(productoDTO.getCodigo())
                .nombre(productoDTO.getNombre())
                .descripcion(productoDTO.getDescripcion())
                .precio(productoDTO.getPrecio())
                .categoria(productoDTO.getCategoria())
                .build();

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());

        return mapToDTO(productoGuardado);
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoporId(Long id) {
        log.info("Obteniendo producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));
        return mapToDTO(producto);
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorCodigo(String codigo) {
        log.info("Obteniendo producto con código: {}", codigo);
        Producto producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con código: " + codigo));
        return mapToDTO(producto);
    }

    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        log.info("Actualizando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getCodigo().equals(productoDTO.getCodigo())) {
            if (productoRepository.findByCodigo(productoDTO.getCodigo()).isPresent()) {
                throw new ProductoDuplicadoException("Código de producto ya está en uso");
            }
        }

        producto.setCodigo(productoDTO.getCodigo());
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setCategoria(productoDTO.getCategoria());

        Producto productoActualizado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente");

        return mapToDTO(productoActualizado);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));

        productoRepository.delete(producto);
        log.info("Producto eliminado exitosamente");
    }

    @Transactional(readOnly = true)
    public Page<ProductoDTO> listarProductos(Pageable pageable) {
        log.info("Listando productos con paginación");
        return productoRepository.findAll(pageable).map(this::mapToDTO);
    }

    private ProductoDTO mapToDTO(Producto producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .categoria(producto.getCategoria())
                .build();
    }
}

