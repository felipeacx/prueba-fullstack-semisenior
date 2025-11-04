package com.fullstack.inventario.controller;

import com.fullstack.inventario.dto.JsonApiResponse;
import com.fullstack.inventario.dto.InventarioDTO;
import com.fullstack.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventarios", description = "API para gestionar inventario")
@SecurityRequirement(name = "api-key")
public class InventarioController {

        private final InventarioService inventarioService;

        @PostMapping
        @Operation(
                summary = "Crear un nuevo inventario",
                description = "Crea un nuevo registro de inventario para un producto. La cantidad debe ser mayor a 0 y mayor que la cantidad mínima. " +
                        "REQUERIDO: Header 'X-API-Key: secret-key-inventario'",
                operationId = "crearInventario"
        )
        @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente")
        @ApiResponse(responseCode = "400", description = "Datos inválidos o producto ID duplicado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<InventarioDTO>> crearInventario(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                description = "Datos del nuevo registro de inventario",
                                required = true
                        ) @RequestBody InventarioDTO inventarioDTO) {
                log.info("POST /api/v1/inventarios - Creando nuevo inventario");
                InventarioDTO inventarioCreado = inventarioService.crearInventario(inventarioDTO);

                JsonApiResponse<InventarioDTO> response = JsonApiResponse.<InventarioDTO>builder()
                                .data(inventarioCreado)
                                .links(new JsonApiResponse.Links("/api/v1/inventarios/" + inventarioCreado.getId()))
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener inventario por ID", description = "Obtiene el inventario de un producto específico. REQUERIDO: Header 'X-API-Key: secret-key-inventario'")
        @ApiResponse(responseCode = "200", description = "Inventario encontrado")
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<InventarioDTO>> obtenerInventario(
                        @PathVariable @Parameter(description = "ID del inventario") Long id) {
                log.info("GET /api/v1/inventarios/{} - Obteniendo inventario", id);
                InventarioDTO inventario = inventarioService.obtenerInventarioPorId(id);

                JsonApiResponse<InventarioDTO> response = JsonApiResponse.<InventarioDTO>builder()
                                .data(inventario)
                                .links(new JsonApiResponse.Links("/api/v1/inventarios/" + id))
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/producto/{productoId}")
        @Operation(summary = "Obtener inventario por ID de producto")
        @ApiResponse(responseCode = "200", description = "Inventario encontrado")
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
        public ResponseEntity<JsonApiResponse<InventarioDTO>> obtenerInventarioPorProducto(
                        @PathVariable @Parameter(description = "ID del producto") Long productoId) {
                log.info("GET /api/v1/inventarios/producto/{} - Obteniendo inventario", productoId);
                InventarioDTO inventario = inventarioService.obtenerInventarioPorProductoId(productoId);

                JsonApiResponse<InventarioDTO> response = JsonApiResponse.<InventarioDTO>builder()
                                .data(inventario)
                                .links(new JsonApiResponse.Links("/api/v1/inventarios/producto/" + productoId))
                                .build();

                return ResponseEntity.ok(response);
        }

        @PostMapping("/compra/{productoId}")
        @Operation(summary = "Realizar una compra (decrementar cantidad)")
        @ApiResponse(responseCode = "200", description = "Compra realizada exitosamente")
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
        @ApiResponse(responseCode = "409", description = "Cantidad insuficiente")
        public ResponseEntity<JsonApiResponse<InventarioDTO>> realizarCompra(
                        @PathVariable @Parameter(description = "ID del producto") Long productoId,
                        @RequestParam(required = true) @Parameter(description = "Cantidad a comprar") Integer cantidad) {
                log.info("POST /api/v1/inventarios/compra/{} - Realizando compra de {} unidades", productoId, cantidad);
                if (cantidad == null || cantidad <= 0) {
                        log.error("Cantidad inválida: {}", cantidad);
                        throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
                }
                InventarioDTO inventario = inventarioService.realizarCompra(productoId, cantidad);

                JsonApiResponse<InventarioDTO> response = JsonApiResponse.<InventarioDTO>builder()
                                .data(inventario)
                                .links(new JsonApiResponse.Links("/api/v1/inventarios/producto/" + productoId))
                                .build();

                return ResponseEntity.ok(response);
        }

        @PutMapping("/cantidad/{productoId}")
        @Operation(summary = "Actualizar cantidad de inventario")
        @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente")
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
        public ResponseEntity<JsonApiResponse<InventarioDTO>> actualizarCantidad(
                        @PathVariable @Parameter(description = "ID del producto") Long productoId,
                        @RequestParam @Parameter(description = "Nueva cantidad") Integer cantidad) {
                log.info("PUT /api/v1/inventarios/cantidad/{} - Actualizando cantidad a {}", productoId, cantidad);
                InventarioDTO inventario = inventarioService.actualizarCantidad(productoId, cantidad);

                JsonApiResponse<InventarioDTO> response = JsonApiResponse.<InventarioDTO>builder()
                                .data(inventario)
                                .links(new JsonApiResponse.Links("/api/v1/inventarios/producto/" + productoId))
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping
        @Operation(
                summary = "Listar todos los inventarios con paginación",
                description = "Retorna una lista paginada de registros de inventario. REQUERIDO: Header 'X-API-Key: secret-key-inventario'",
                operationId = "listarInventarios"
        )
        @ApiResponse(responseCode = "200", description = "Lista de inventarios obtenida exitosamente")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<java.util.List<InventarioDTO>>> listarInventarios(
                        @RequestParam(defaultValue = "1")
                        @Parameter(
                                description = "Número de página (comienza en 1)",
                                example = "1"
                        ) int page,
                        @RequestParam(defaultValue = "10")
                        @Parameter(
                                description = "Cantidad de registros por página (máximo: 100)",
                                example = "10"
                        ) int size) {
                log.info("GET /api/v1/inventarios - Listando inventarios (página: {}, tamaño: {})", page, size);

                int pageIndex = Math.max(0, page - 1);
                Pageable pageable = PageRequest.of(pageIndex, size);
                Page<InventarioDTO> inventariosPage = inventarioService.listarInventarios(pageable);

                Map<String, Object> meta = new HashMap<>();
                meta.put("total", inventariosPage.getTotalElements());
                meta.put("pages", inventariosPage.getTotalPages());
                meta.put("current_page", page);
                meta.put("page_size", size);

                JsonApiResponse<java.util.List<InventarioDTO>> response = JsonApiResponse.<java.util.List<InventarioDTO>>builder()
                                .data(inventariosPage.getContent())
                                .meta(meta)
                                .links(new JsonApiResponse.Links("/api/v1/inventarios"))
                                .build();

                return ResponseEntity.ok(response);
        }
}
