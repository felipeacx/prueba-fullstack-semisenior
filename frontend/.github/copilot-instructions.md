# Instrucciones del Proyecto Frontend

## Estado del Proyecto

Este documento describe el estado actual del proyecto Frontend Angular.

### ✅ Completado

- [x] Estructura del proyecto Angular configurada
- [x] Componentes principales creados (ProductList, ProductDetail, ErrorDisplay)
- [x] Servicios implementados (ProductService, ErrorService)
- [x] Interceptor HTTP para manejo de errores
- [x] Rutas de la aplicación configuradas
- [x] Modelos de datos (interfaces) definidos
- [x] Estilos responsivos implementados
- [x] Pruebas unitarias básicas añadidas
- [x] Documentación README completa
- [x] Configuración de TypeScript y Angular

### ⏳ Próximos Pasos

Después de instalar las dependencias con `npm install`:

1. **Instalar Dependencias**

   ```bash
   npm install
   ```

2. **Iniciar Servidor de Desarrollo**

   ```bash
   npm start
   ```

   La aplicación estará en http://localhost:4200

3. **Ejecutar Pruebas**

   ```bash
   npm test
   ```

4. **Generar Reporte de Cobertura**
   ```bash
   npm run test:coverage
   ```

## Estructura de Componentes

### ProductListComponent

- Muestra lista paginada de productos
- Navegación entre páginas
- Manejo de errores
- Estados de carga

### ProductDetailComponent

- Detalles completos del producto
- Información de inventario
- Selector de cantidad
- Funcionalidad de compra

### ErrorDisplayComponent

- Mostrador centralizado de errores
- Botón de cerrar
- Estilos de error

## Servicios

### ProductService

- Obtener lista de productos
- Obtener detalles de producto
- Obtener inventario
- Realizar compra

### ErrorService

- Gestionar errores globales
- Gestionar estado de carga
- Observables para suscripción

## Configuración Esperada

### Variables de Entorno

- API_URL: http://localhost:3000/api (por defecto en code)

### Dependencias Node

- Node 18+
- npm 9+

## Testing

- Tests de servicios con HttpClientTestingModule
- Tests de componentes con ComponentFixture
- Pruebas de interceptores
- Cobertura target: 60%

## Próxima Fase

Una vez instalado:

1. Verificar que el backend esté ejecutándose en puerto 3000
2. Ejecutar `npm start` para iniciar el servidor dev
3. Acceder a http://localhost:4200
4. Verificar que la lista de productos carga correctamente

## Recursos

- [Documentación Angular](https://angular.io/docs)
- [RxJS Documentation](https://rxjs.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
