package com.fullstack.inventario.service;

import com.fullstack.inventario.client.ProductoClient;
import com.fullstack.inventario.domain.Inventario;
import com.fullstack.inventario.dto.InventarioDTO;
import com.fullstack.inventario.event.InventarioEvent;
import com.fullstack.inventario.event.InventarioEventPublisher;
import com.fullstack.inventario.exception.CantidadInsuficienteException;
import com.fullstack.inventario.exception.InventarioNotFoundException;
import com.fullstack.inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoClient productoClient;
    private final InventarioEventPublisher eventPublisher;

    @Transactional
    public InventarioDTO crearInventario(InventarioDTO inventarioDTO) {
        log.info("Creando inventario para producto: {}", inventarioDTO.getProductoId());

        try {
            productoClient.obtenerProducto(inventarioDTO.getProductoId());
        } catch (Exception e) {
            log.error("No se puede crear inventario, producto no encontrado: {}", e.getMessage());
            throw new RuntimeException("Producto no encontrado en el servicio de productos");
        }

        Inventario inventario = Inventario.builder()
                .productoId(inventarioDTO.getProductoId())
                .cantidad(inventarioDTO.getCantidad())
                .cantidadMinima(inventarioDTO.getCantidadMinima())
                .build();

        Inventario inventarioGuardado = inventarioRepository.save(inventario);
        log.info("Inventario creado exitosamente con ID: {}", inventarioGuardado.getId());

        eventPublisher.publicarEvento(InventarioEvent.inventarioCreado(
                inventarioGuardado.getProductoId(),
                inventarioGuardado.getCantidad()
        ));

        return mapToDTO(inventarioGuardado);
    }

    @Transactional(readOnly = true)
    public InventarioDTO obtenerInventarioPorProductoId(Long productoId) {
        log.info("Obteniendo inventario para producto: {}", productoId);

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new InventarioNotFoundException("Inventario no encontrado para producto: " + productoId));

        return mapToDTO(inventario);
    }

    @Transactional(readOnly = true)
    public InventarioDTO obtenerInventarioPorId(Long id) {
        log.info("Obteniendo inventario con ID: {}", id);

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNotFoundException("Inventario no encontrado con ID: " + id));

        return mapToDTO(inventario);
    }

    @Transactional(rollbackFor = Exception.class)
    public InventarioDTO realizarCompra(Long productoId, Integer cantidad) {
        log.info("============ INICIO COMPRA ============");
        log.info("Realizando compra de {} unidades del producto {}", cantidad, productoId);

        if (cantidad == null || cantidad <= 0) {
            log.error("❌ Cantidad inválida: {}", cantidad);
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new InventarioNotFoundException("Inventario no encontrado para producto: " + productoId));

        log.info("📦 Inventario encontrado:");
        log.info("   - ID Inventario: {}", inventario.getId());
        log.info("   - Producto ID: {}", inventario.getProductoId());
        log.info("   - Cantidad actual: {}", inventario.getCantidad());
        log.info("   - Cantidad a restar: {}", cantidad);

        if (inventario.getCantidad() < cantidad) {
            log.error("❌ Cantidad insuficiente. Disponible: {}, Solicitado: {}", inventario.getCantidad(), cantidad);
            throw new CantidadInsuficienteException(
                    "Cantidad insuficiente. Disponible: " + inventario.getCantidad() + ", Solicitado: " + cantidad
            );
        }

        Integer cantidadAnterior = inventario.getCantidad();
        Integer nuevaCantidad = cantidadAnterior - cantidad;

        if (nuevaCantidad < 0) {
            log.error("❌ Cantidad resultante sería negativa: {} - {} = {}", cantidadAnterior, cantidad, nuevaCantidad);
            throw new CantidadInsuficienteException(
                    "Cantidad insuficiente. Disponible: " + cantidadAnterior + ", Solicitado: " + cantidad
            );
        }

        log.info("🧮 Cálculo: {} - {} = {}", cantidadAnterior, cantidad, nuevaCantidad);

        inventario.setCantidad(nuevaCantidad);
        log.info("✅ Cantidad establecida en objeto: {}", inventario.getCantidad());

        Inventario inventarioActualizado = inventarioRepository.save(inventario);

        log.info("💾 Guardada en BD - Cantidad final: {}", inventarioActualizado.getCantidad());
        log.info("✓ Compra realizada: {} → {}", cantidadAnterior, inventarioActualizado.getCantidad());

        inventarioRepository.flush();
        log.info("🔄 Cambios sincronizados con BD");
        log.info("============ FIN COMPRA ============");

        eventPublisher.publicarEvento(InventarioEvent.compraRealizada(
                productoId,
                cantidadAnterior,
                inventarioActualizado.getCantidad()
        ));

        return mapToDTO(inventarioActualizado);
    }

    @Transactional
    public InventarioDTO actualizarCantidad(Long productoId, Integer nuevaCantidad) {
        log.info("Actualizando cantidad de inventario para producto {}: {}", productoId, nuevaCantidad);

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new InventarioNotFoundException("Inventario no encontrado para producto: " + productoId));

        Integer cantidadAnterior = inventario.getCantidad();
        inventario.setCantidad(nuevaCantidad);

        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        log.info("Cantidad actualizada exitosamente");

        eventPublisher.publicarEvento(InventarioEvent.builder()
                .tipo("ACTUALIZADO")
                .productoId(productoId)
                .cantidadAnterior(cantidadAnterior)
                .cantidadNueva(nuevaCantidad)
                .razon("Actualización manual de cantidad")
                .timestamp(java.time.LocalDateTime.now())
                .build());

        return mapToDTO(inventarioActualizado);
    }

    @Transactional(readOnly = true)
    public Page<InventarioDTO> listarInventarios(Pageable pageable) {
        log.info("Listando inventarios con paginación");
        return inventarioRepository.findAll(pageable).map(this::mapToDTO);
    }

    private InventarioDTO mapToDTO(Inventario inventario) {
        return InventarioDTO.builder()
                .id(inventario.getId())
                .productoId(inventario.getProductoId())
                .cantidad(inventario.getCantidad())
                .cantidadMinima(inventario.getCantidadMinima())
                .build();
    }
}

