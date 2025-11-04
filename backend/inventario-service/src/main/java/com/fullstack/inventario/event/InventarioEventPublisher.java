package com.fullstack.inventario.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventarioEventPublisher {

    public void publicarEvento(InventarioEvent evento) {
        log.info("=== EVENTO DE INVENTARIO ===");
        log.info("Tipo: {}", evento.getTipo());
        log.info("Producto ID: {}", evento.getProductoId());
        log.info("Cantidad Anterior: {}", evento.getCantidadAnterior());
        log.info("Cantidad Nueva: {}", evento.getCantidadNueva());
        log.info("Razón: {}", evento.getRazon());
        log.info("Timestamp: {}", evento.getTimestamp());
        log.info("=============================");
    }
}

