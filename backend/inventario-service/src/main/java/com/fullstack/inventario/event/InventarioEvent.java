package com.fullstack.inventario.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioEvent {
    private String tipo;
    private Long productoId;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private String razon;
    private LocalDateTime timestamp;

    public static InventarioEvent compraRealizada(Long productoId, Integer cantidadAnterior, Integer cantidadNueva) {
        return InventarioEvent.builder()
                .tipo("COMPRA_REALIZADA")
                .productoId(productoId)
                .cantidadAnterior(cantidadAnterior)
                .cantidadNueva(cantidadNueva)
                .razon("Compra realizada")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static InventarioEvent inventarioCreado(Long productoId, Integer cantidad) {
        return InventarioEvent.builder()
                .tipo("CREADO")
                .productoId(productoId)
                .cantidadAnterior(0)
                .cantidadNueva(cantidad)
                .razon("Inventario creado")
                .timestamp(LocalDateTime.now())
                .build();
    }
}

