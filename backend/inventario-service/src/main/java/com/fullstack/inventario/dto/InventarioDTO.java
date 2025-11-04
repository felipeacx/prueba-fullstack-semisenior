package com.fullstack.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Objeto que representa el inventario de un producto")
public class InventarioDTO {
    @Schema(description = "ID único del registro de inventario", example = "1")
    private Long id;

    @Schema(description = "ID del producto al que pertenece este inventario", example = "1")
    private Long productoId;

    @Schema(description = "Cantidad actual disponible en inventario", example = "50")
    private Integer cantidad;

    @Schema(description = "Cantidad mínima permitida antes de alertar", example = "10")
    private Integer cantidadMinima;
}

