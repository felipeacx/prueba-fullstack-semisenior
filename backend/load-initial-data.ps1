#Requires -Version 5.0
# Script para cargar datos iniciales en los microservicios
# Uso: .\load-initial-data.ps1

# Configurar encoding UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Variables
$PRODUCTOS_URL = "http://localhost:8081/api/v1/productos"
$INVENTARIO_URL = "http://localhost:8082/api/v1/inventarios"
$PRODUCTOS_KEY = "secret-key-productos"
$INVENTARIO_KEY = "secret-key-inventario"

Write-Host "🚀 Iniciando carga de datos iniciales..." -ForegroundColor Green
Write-Host ""

# Verificar que los servicios estén disponibles
Write-Host "🔍 Verificando disponibilidad de servicios..." -ForegroundColor Cyan
$servicios_ok = $true

try {
    $null = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -TimeoutSec 5 -SkipHttpErrorCheck
    Write-Host "  ✅ Productos Service: Disponible" -ForegroundColor Green
}
catch {
    Write-Host "  ❌ Productos Service: NO disponible en http://localhost:8081" -ForegroundColor Red
    $servicios_ok = $false
}

try {
    $null = Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -TimeoutSec 5 -SkipHttpErrorCheck
    Write-Host "  ✅ Inventario Service: Disponible" -ForegroundColor Green
}
catch {
    Write-Host "  ❌ Inventario Service: NO disponible en http://localhost:8082" -ForegroundColor Red
    $servicios_ok = $false
}

if (-not $servicios_ok) {
    Write-Host ""
    Write-Host "⚠️  Por favor, inicia los servicios primero:" -ForegroundColor Yellow
    Write-Host "    .\setup-and-load-data.ps1" -ForegroundColor Yellow
    Write-Host "    O: docker-compose up -d" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Array de productos con precios en pesos colombianos (COP)
$productos = @(
    @{ codigo = "LAPTOP001"; nombre = "Laptop Dell XPS 15"; descripcion = "Laptop de alta performance para desarrollo"; precio = 5999990; categoria = "Electrónica" },
    @{ codigo = "MOUSE001"; nombre = "Mouse Logitech MX Master 3"; descripcion = "Mouse inalámbrico de precisión"; precio = 399990; categoria = "Accesorios" },
    @{ codigo = "TECLADO001"; nombre = "Teclado Mecánico Corsair K95"; descripcion = "Teclado mecánico con RGB"; precio = 799990; categoria = "Accesorios" },
    @{ codigo = "MONITOR001"; nombre = "Monitor LG UltraWide 34"; descripcion = "Monitor ultraancho de 34 pulgadas"; precio = 3199990; categoria = "Electrónica" },
    @{ codigo = "AURICULAR001"; nombre = "Auriculares Sony WH-1000XM5"; descripcion = "Auriculares con cancelación de ruido"; precio = 1599990; categoria = "Audio" },
    @{ codigo = "WEBCAM001"; nombre = "Webcam Logitech C920"; descripcion = "Cámara web Full HD con micrófono"; precio = 319990; categoria = "Accesorios" },
    @{ codigo = "ALMACENAMIENTO001"; nombre = "SSD Samsung 1TB"; descripcion = "Unidad SSD NVMe de 1TB"; precio = 599990; categoria = "Almacenamiento" },
    @{ codigo = "RAM001"; nombre = "RAM Corsair 32GB DDR5"; descripcion = "Módulo de RAM DDR5 32GB"; precio = 759990; categoria = "Memoria" },
    @{ codigo = "CABLE001"; nombre = "Cable HDMI 2.1"; descripcion = "Cable HDMI de 3 metros"; precio = 99990; categoria = "Cables" },
    @{ codigo = "FUENTE001"; nombre = "Fuente Corsair 850W Gold"; descripcion = "Fuente de poder modular 850W"; precio = 759990; categoria = "Accesorios" },
    @{ codigo = "MONITOR002"; nombre = "Monitor Dell U2724D"; descripcion = "Monitor 4K de 27 pulgadas"; precio = 2299990; categoria = "Electrónica" },
    @{ codigo = "PROCESADOR001"; nombre = "Procesador Intel Core i9-14900K"; descripcion = "Procesador de alta performance"; precio = 4499990; categoria = "Componentes" },
    @{ codigo = "TARJETA001"; nombre = "Tarjeta Gráfica RTX 4090"; descripcion = "GPU NVIDIA RTX 4090 24GB"; precio = 8999990; categoria = "Componentes" },
    @{ codigo = "MOTHERBOARD001"; nombre = "Placa Madre ASUS ROG Maximus"; descripcion = "Motherboard para gaming Z890"; precio = 1899990; categoria = "Componentes" },
    @{ codigo = "FUENTE002"; nombre = "Fuente Seasonic Prime 1200W"; descripcion = "Fuente modular 1200W Platinum"; precio = 1599990; categoria = "Accesorios" },
    @{ codigo = "GABINETE001"; nombre = "Gabinete Lian Li O11 XL"; descripcion = "Case premium para overclocking"; precio = 899990; categoria = "Accesorios" },
    @{ codigo = "REFRIGERACION001"; nombre = "AIO Liquid Cooler 360mm"; descripcion = "Refrigeración líquida all-in-one"; precio = 599990; categoria = "Accesorios" },
    @{ codigo = "VENTILADOR001"; nombre = "Ventiladores Corsair ML120 Pro"; descripcion = "Pack de 3 ventiladores RGB"; precio = 399990; categoria = "Accesorios" },
    @{ codigo = "ALMACENAMIENTO002"; nombre = "SSD Samsung 980 Pro 2TB"; descripcion = "Unidad SSD NVMe Ultra Premium"; precio = 799990; categoria = "Almacenamiento" },
    @{ codigo = "RAM002"; nombre = "RAM Kingston Fury Beast 16GB"; descripcion = "Módulo de RAM DDR5 16GB"; precio = 429990; categoria = "Memoria" }
)

Write-Host "📦 Creando 20 productos..." -ForegroundColor Cyan
$contador_productos = 0

foreach ($producto in $productos) {
    try {
        $json_body = $producto | ConvertTo-Json -Compress

        $response = Invoke-WebRequest -Uri $PRODUCTOS_URL `
            -Method POST `
            -ContentType "application/json" `
            -Headers @{"X-API-Key" = $PRODUCTOS_KEY} `
            -Body $json_body `
            -SkipHttpErrorCheck `
            -TimeoutSec 10

        if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 201) {
            $contador_productos++
            Write-Host "  ✅ $contador_productos. $($producto.nombre)" -ForegroundColor Green
        }
        else {
            Write-Host "  ❌ Error: $($producto.nombre) (HTTP $($response.StatusCode))" -ForegroundColor Red
            Write-Host "     Respuesta: $($response.Content)" -ForegroundColor DarkRed
        }
    }
    catch {
        Write-Host "  ❌ Error: $($producto.nombre)" -ForegroundColor Red
        Write-Host "     Excepción: $($_.Exception.Message)" -ForegroundColor DarkRed
    }
}

Write-Host ""
Write-Host "📊 Creando 20 registros de inventario..." -ForegroundColor Cyan

# Array de inventarios
$inventarios = @(
    @{ productoId = 1; cantidad = 50; cantidadMinima = 10 },
    @{ productoId = 2; cantidad = 150; cantidadMinima = 20 },
    @{ productoId = 3; cantidad = 75; cantidadMinima = 15 },
    @{ productoId = 4; cantidad = 25; cantidadMinima = 5 },
    @{ productoId = 5; cantidad = 100; cantidadMinima = 10 },
    @{ productoId = 6; cantidad = 200; cantidadMinima = 25 },
    @{ productoId = 7; cantidad = 40; cantidadMinima = 8 },
    @{ productoId = 8; cantidad = 60; cantidadMinima = 12 },
    @{ productoId = 9; cantidad = 500; cantidadMinima = 50 },
    @{ productoId = 10; cantidad = 30; cantidadMinima = 6 },
    @{ productoId = 11; cantidad = 35; cantidadMinima = 7 },
    @{ productoId = 12; cantidad = 20; cantidadMinima = 4 },
    @{ productoId = 13; cantidad = 15; cantidadMinima = 3 },
    @{ productoId = 14; cantidad = 25; cantidadMinima = 5 },
    @{ productoId = 15; cantidad = 18; cantidadMinima = 4 },
    @{ productoId = 16; cantidad = 45; cantidadMinima = 9 },
    @{ productoId = 17; cantidad = 55; cantidadMinima = 11 },
    @{ productoId = 18; cantidad = 120; cantidadMinima = 24 },
    @{ productoId = 19; cantidad = 28; cantidadMinima = 6 },
    @{ productoId = 20; cantidad = 80; cantidadMinima = 16 }
)

$contador_inventario = 0

foreach ($inventario in $inventarios) {
    try {
        $json_body = $inventario | ConvertTo-Json -Compress

        $response = Invoke-WebRequest -Uri $INVENTARIO_URL `
            -Method POST `
            -ContentType "application/json" `
            -Headers @{"X-API-Key" = $INVENTARIO_KEY} `
            -Body $json_body `
            -SkipHttpErrorCheck `
            -TimeoutSec 10

        if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 201) {
            $contador_inventario++
            Write-Host "  ✅ $contador_inventario. Producto ID: $($inventario.productoId) - Cantidad: $($inventario.cantidad)" -ForegroundColor Green
        }
        else {
            Write-Host "  ❌ Error: Producto ID $($inventario.productoId) (HTTP $($response.StatusCode))" -ForegroundColor Red
            Write-Host "     Respuesta: $($response.Content)" -ForegroundColor DarkRed
        }
    }
    catch {
        Write-Host "  ❌ Error: Producto ID $($inventario.productoId)" -ForegroundColor Red
        Write-Host "     Excepción: $($_.Exception.Message)" -ForegroundColor DarkRed
    }
}

Write-Host ""
Write-Host "✨ ¡Carga de datos completada!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Resumen:" -ForegroundColor Yellow
Write-Host "  • Productos creados: $contador_productos/20"
Write-Host "  • Registros de inventario creados: $contador_inventario/20"
Write-Host ""

if ($contador_productos -eq 20 -and $contador_inventario -eq 20) {
    Write-Host "🎉 ¡ÉXITO! Todos los datos se cargaron correctamente." -ForegroundColor Green
}
else {
    Write-Host "⚠️  Algunos datos no se cargaron. Verifica los errores arriba." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🔗 Verifica los datos en:" -ForegroundColor Cyan
Write-Host "  • Productos: http://localhost:8081/swagger-ui.html"
Write-Host "  • Inventario: http://localhost:8082/swagger-ui.html"
Write-Host ""
Write-Host "🗄️  Base de datos PostgreSQL:" -ForegroundColor Cyan
Write-Host "  • Host: localhost:5432"
Write-Host "  • Usuario: postgres"
Write-Host "  • Contraseña: postgres"
Write-Host "  • Bases de datos: productos_db, inventario_db"
Write-Host ""
Write-Host "💡 Herramientas recomendadas para acceder a PostgreSQL:" -ForegroundColor Cyan
Write-Host "  • DBeaver (GUI)"
Write-Host "  • pgAdmin (Web)"
Write-Host "  • psql (CLI): docker exec -it postgres-db psql -U postgres -d productos_db"



