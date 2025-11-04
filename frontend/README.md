# Frontend - eCommerce Application

Interfaz web Angular para aplicación de ecommerce con gestión de productos e inventario.

## Características

- Listado de productos con paginación
- Vista detallada de productos
- Gestión de inventario en tiempo real
- Funcionalidad de compra
- Manejo robusto de errores
- Indicadores de carga
- Interfaz responsiva
- Pruebas unitarias completas

## Requisitos Previos

- Node.js (v18 o superior)
- npm (v9 o superior)
- Angular CLI 20

## Instalación

1. Navega al directorio del proyecto frontend:

```bash
cd frontend
```

2. Instala las dependencias:

```bash
npm install
```

## Ejecución

### Servidor de desarrollo

```bash
npm start
```

La aplicación estará disponible en `http://localhost:4200`

## Pruebas

### Ejecutar pruebas unitarias

```bash
npm test
```

### Ejecutar pruebas con cobertura de código

```bash
npm run test:coverage
```

Los reportes de cobertura estarán en `coverage/`

## Estructura del Proyecto

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── product-list/          # Listado de productos
│   │   │   ├── product-detail/        # Detalle de producto
│   │   │   └── error-display/         # Mostrador de errores
│   │   ├── services/
│   │   │   ├── product.service.ts     # Servicio de productos
│   │   │   └── error.service.ts       # Servicio de errores
│   │   ├── models/
│   │   │   └── product.model.ts       # Interfaces de datos
│   │   ├── interceptors/
│   │   │   └── error.interceptor.ts   # Interceptor HTTP
│   │   ├── app.component.*            # Componente raíz
│   │   └── app.routes.ts              # Rutas de la app
│   ├── main.ts                         # Punto de entrada
│   ├── index.html                      # HTML principal
│   └── styles.scss                     # Estilos globales
├── angular.json                        # Configuración Angular
├── tsconfig.json                       # Configuración TypeScript
├── karma.conf.js                       # Configuración Karma
└── package.json                        # Dependencias del proyecto
```

## Componentes Principales

### ProductListComponent

Muestra un listado paginado de productos disponibles en la tienda.

**Responsabilidades:**

- Obtener lista de productos del servicio
- Manejar paginación
- Mostrar estado de carga
- Navegar a detalle de producto

### ProductDetailComponent

Muestra los detalles completos de un producto y permite realizar compras.

**Responsabilidades:**

- Obtener detalles del producto
- Mostrar inventario disponible
- Manejar cantidad a comprar
- Procesar compra

### ErrorDisplayComponent

Componente reutilizable para mostrar mensajes de error.

**Responsabilidades:**

- Mostrar mensajes de error
- Permitir descartar errores
- Ser descartable o no según configuración

## Casos de Prueba Incluidos

### ProductService (65 tests totales)

#### getProducts()

- Obtiene productos con paginación usando URL correcta
- Incluye encabezado X-API-Key en la solicitud
- Guarda en caché productos después de la primera solicitud
- Usa caché para la segunda solicitud sin hacer nueva llamada HTTP

#### getProductById(id)

- Obtiene detalles del producto por ID
- Maneja errores cuando producto no existe
- Cachea resultado para consultas posteriores

#### getProductInventory(productId)

- Obtiene inventario del producto desde servicio de inventario
- Devuelve cantidad 0 si producto no se encuentra
- Usa caché de inventario en segunda solicitud
- Maneja errores del servicio de inventario

#### buyProduct(productId, quantity)

- Compra un producto exitosamente
- Maneja error de compra correctamente
- Valida cantidad antes de comprar
- Verifica disponibilidad de stock

#### clearCache()

- Limpia todos los cachés
- Invalida cache de productos
- Invalida cache de inventario

### ProductListComponent (14 tests totales)

- Componente se crea correctamente
- Carga productos al iniciar (ngOnInit)
- Inicializa con página indexada en 0
- Muestra todos los productos de la respuesta
- Navega a página siguiente con Signals
- No navega más allá de la última página
- Navega a página anterior con Signals
- No navega a página negativa
- Establece estado de carga mientras se obtienen datos
- Maneja errores del servicio correctamente
- Actualiza totalPages desde metadata
- Establece tamaño de página correctamente
- Limpia error cuando carga es exitosa
- Envía número de página correcto al servicio (indexado en 1)
- Navega a detalles del producto

### ProductDetailComponent (12 tests totales)

- Componente se crea correctamente
- Carga producto al iniciar
- Carga inventario al iniciar
- Aumenta cantidad con Signals
- No aumenta cantidad más allá del inventario disponible
- Disminuye cantidad con Signals
- No disminuye cantidad por debajo de 1
- Valida cantidad de compra
- Valida que cantidad de compra no exceda inventario
- Llama servicio de compra en validación exitosa
- Maneja éxito de compra correctamente
- Establece estado de carga durante compra

### ErrorDisplayComponent (6 tests totales)

- Componente se crea correctamente
- Propiedad dismissible tiene defecto verdadero
- Inicializa con error nulo
- Puede establecer error
- Descarta error correctamente
- No muestra nada cuando error es nulo
- Acepta entrada dismissible desde componente padre

### ErrorInterceptor (5 tests totales)

- Interceptor se crea correctamente
- Establece estado de carga durante la solicitud
- Maneja respuestas de error HTTP (4xx, 5xx)
- Maneja errores del lado del cliente (red, timeout)
- Limpia error después de solicitud exitosa

### ErrorService (8 tests totales)

Gestión de errores con Signals:

- Establece y obtiene error
- Limpia error correctamente
- Obtiene valor de error actual
- Devuelve null cuando no hay error establecido

Gestión de carga con Signals:

- Establece y obtiene estado de carga
- Limpia estado de carga
- Verifica si está cargando
- Tiene carga por defecto en false

### AppComponent (3 tests totales)

- Componente se crea correctamente
- Renderiza título principal
- Tiene enlace de navegación a productos

## Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```
API_URL=http://localhost:3000/api
API_KEY=your-api-key-here
```

## Dependencias Principales

- @angular/core - Framework principal
- @angular/common - Utilidades comunes
- @angular/forms - Manejo de formularios
- @angular/router - Enrutamiento
- rxjs - Programación reactiva
- typescript - Lenguaje de programación

## Dependencias de Desarrollo

- @angular/cli - CLI de Angular
- @angular/compiler-cli - Compilador
- karma - Test runner
- jasmine - Framework de pruebas
- typescript - Compilador TypeScript

## Comandos Disponibles

```bash
# Desarrollo
npm start              # Inicia servidor de desarrollo

# Testing
npm test              # Ejecuta pruebas unitarias
npm run test:coverage # Ejecuta pruebas con cobertura

# Build
npm run build         # Compila para producción
npm run build:prod    # Compila optimizado para producción

# Linting
npm run lint          # Ejecuta linter de código
```

## Estructura de Pruebas

Los tests están organizados por componente/servicio:

```
src/app/
├── components/
│   ├── product-list/
│   │   ├── product-list.component.ts
│   │   └── product-list.component.spec.ts
│   ├── product-detail/
│   │   ├── product-detail.component.ts
│   │   └── product-detail.component.spec.ts
│   └── error-display/
│       ├── error-display.component.ts
│       └── error-display.component.spec.ts
├── services/
│   ├── product.service.ts
│   ├── product.service.spec.ts
│   ├── error.service.ts
│   └── error.service.spec.ts
├── interceptors/
│   ├── error.interceptor.ts
│   └── error.interceptor.spec.ts
└── app.component.spec.ts
```

## Cobertura de Tests

La aplicación mantiene alta cobertura de código:

- ProductService
- ProductListComponent
- ProductDetailComponent
- ErrorDisplayComponent
- ErrorService
- ErrorInterceptor
- AppComponent

## Configuración de Pruebas

Las pruebas utilizan:

- Karma como Test Runner
- Jasmine como Framework de Pruebas
- Angular Testing Utilities
- HttpClientTestingModule para mocks HTTP

## Notas de Desarrollo

- Los tests usan Signals de Angular 17+ para estado reactivo
- Se implementa caching en ProductService
- ErrorInterceptor maneja errores HTTP automáticamente
- Todos los componentes son standalone
- Se utiliza Angular Router para navegación

## Autor

Aplicación de prueba técnica para posición Senior de Full Stack
