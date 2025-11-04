package com.fullstack.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Objeto que representa un producto del catálogo")
public class ProductoDTO {
    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Schema(description = "Código único del producto", example = "LAPTOP001")
    private String codigo;

    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS 15")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Laptop de alta performance para desarrollo")
    private String descripcion;

    @Schema(description = "Precio en pesos colombianos (COP)", example = "5999990")
    private BigDecimal precio;

    @Schema(description = "Categoría del producto", example = "Electrónica")
    private String categoria;
}

