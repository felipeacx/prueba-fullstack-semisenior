package com.fullstack.productos.controller;

import com.fullstack.productos.dto.JsonApiResponse;
import com.fullstack.productos.dto.ProductoDTO;
import com.fullstack.productos.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Productos", description = "API para gestionar productos")
@SecurityRequirement(name = "api-key")
public class ProductoController {

        private final ProductoService productoService;

        @PostMapping
        @Operation(
                summary = "Crear un nuevo producto",
                description = "Crea un nuevo producto en el catálogo. Los precios deben estar en pesos colombianos (COP). " +
                        "REQUERIDO: Header 'X-API-Key: secret-key-productos'",
                operationId = "crearProducto"
        )
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
        @ApiResponse(responseCode = "400", description = "Datos inválidos o código de producto duplicado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<ProductoDTO>> crearProducto(
                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Datos del nuevo producto",
                        required = true
                ) @RequestBody ProductoDTO productoDTO) {
                log.info("POST /api/v1/productos - Creando nuevo producto");
                ProductoDTO productoCreado = productoService.crearProducto(productoDTO);

                JsonApiResponse<ProductoDTO> response = JsonApiResponse.<ProductoDTO>builder()
                                .data(productoCreado)
                                .links(new JsonApiResponse.Links("/api/v1/productos/" + productoCreado.getId()))
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener un producto por ID", description = "Obtiene un producto específico por su ID. REQUERIDO: Header 'X-API-Key: secret-key-productos'")
        @ApiResponse(responseCode = "200", description = "Producto encontrado")
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<ProductoDTO>> obtenerProducto(
                        @PathVariable @Parameter(description = "ID del producto") Long id) {
                log.info("GET /api/v1/productos/{} - Obteniendo producto", id);
                ProductoDTO producto = productoService.obtenerProductoporId(id);

                JsonApiResponse<ProductoDTO> response = JsonApiResponse.<ProductoDTO>builder()
                                .data(producto)
                                .links(new JsonApiResponse.Links("/api/v1/productos/" + id))
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/codigo/{codigo}")
        @Operation(summary = "Obtener un producto por código", description = "Obtiene un producto específico por su código. REQUERIDO: Header 'X-API-Key: secret-key-productos'")
        @ApiResponse(responseCode = "200", description = "Producto encontrado")
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<ProductoDTO>> obtenerProductoPorCodigo(
                        @PathVariable @Parameter(description = "Código del producto") String codigo) {
                log.info("GET /api/v1/productos/codigo/{} - Obteniendo producto por código", codigo);
                ProductoDTO producto = productoService.obtenerProductoPorCodigo(codigo);

                JsonApiResponse<ProductoDTO> response = JsonApiResponse.<ProductoDTO>builder()
                                .data(producto)
                                .links(new JsonApiResponse.Links("/api/v1/productos/codigo/" + codigo))
                                .build();

                return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar un producto", description = "Actualiza la información de un producto existente. REQUERIDO: Header 'X-API-Key: secret-key-productos'")
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente")
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<ProductoDTO>> actualizarProducto(
                        @PathVariable @Parameter(description = "ID del producto") Long id,
                        @RequestBody ProductoDTO productoDTO) {
                log.info("PUT /api/v1/productos/{} - Actualizando producto", id);
                ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);

                JsonApiResponse<ProductoDTO> response = JsonApiResponse.<ProductoDTO>builder()
                                .data(productoActualizado)
                                .links(new JsonApiResponse.Links("/api/v1/productos/" + id))
                                .build();

                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar un producto", description = "Elimina un producto de la base de datos. REQUERIDO: Header 'X-API-Key: secret-key-productos'")
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<Void> eliminarProducto(
                        @PathVariable @Parameter(description = "ID del producto") Long id) {
                log.info("DELETE /api/v1/productos/{} - Eliminando producto", id);
                productoService.eliminarProducto(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping
        @Operation(
                summary = "Listar todos los productos con paginación",
                description = "Retorna una lista paginada de productos. Los precios están en pesos colombianos (COP). " +
                        "REQUERIDO: Header 'X-API-Key: secret-key-productos'",
                operationId = "listarProductos"
        )
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
        @ApiResponse(responseCode = "401", description = "API Key no válida o ausente")
        public ResponseEntity<JsonApiResponse<java.util.List<ProductoDTO>>> listarProductos(
                        @RequestParam(defaultValue = "1")
                        @Parameter(
                                description = "Número de página (comienza en 1)",
                                example = "1"
                        ) int page,
                        @RequestParam(defaultValue = "10")
                        @Parameter(
                                description = "Cantidad de productos por página (máximo: 100)",
                                example = "10"
                        ) int size) {
                log.info("GET /api/v1/productos - Listando productos (página: {}, tamaño: {})", page, size);

                int pageIndex = Math.max(0, page - 1);
                Pageable pageable = PageRequest.of(pageIndex, size);
                Page<ProductoDTO> productosPage = productoService.listarProductos(pageable);

                Map<String, Object> meta = new HashMap<>();
                meta.put("total", productosPage.getTotalElements());
                meta.put("pages", productosPage.getTotalPages());
                meta.put("current_page", page);
                meta.put("page_size", size);

                JsonApiResponse<java.util.List<ProductoDTO>> response = JsonApiResponse.<java.util.List<ProductoDTO>>builder()
                                .data(productosPage.getContent())
                                .meta(meta)
                                .links(new JsonApiResponse.Links("/api/v1/productos"))
                                .build();

                return ResponseEntity.ok(response);
        }
}
