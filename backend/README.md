# PROYECTO FULLSTACK SEMISENIOR

## DESCRIPCIÓN GENERAL

Sistema de microservicios para gestión de productos e inventario. Incluye:

- **Productos Service** (Puerto 8081) - Gestión de catálogo de productos
- **Inventario Service** (Puerto 8082) - Gestión de niveles de stock
- **PostgreSQL Database** (Puerto 5432) - Base de datos persistente
- **Arquitectura Escalable** - Comunicación inter-servicios
- **Autenticación con API Key** - Seguridad en endpoints

---

## INICIO RÁPIDO (3 PASOS)

### Paso 1: Reiniciar y Compilar Docker

**Ejecuta estos comandos:**
```powershell
cd "X:\Dev\Technical Tests\prueba-fullstack-semisenior\backend"

# 1. Detener servicios y limpiar volúmenes
docker-compose down -v

# 2. Compilar inventario-service
cd inventario-service
mvn clean package -f pom-docker.xml -DskipTests -q
cd ..

# 3. Compilar productos-service
cd productos-service
mvn clean package -f pom-docker.xml -DskipTests -q
cd ..

# 4. Reconstruir imágenes Docker sin cache
docker-compose build --no-cache

# 5. Iniciar todos los servicios
docker-compose up -d

# 6. Verificar estado
docker ps
```

### Paso 2: Cargar Datos Iniciales
```powershell
.\load-initial-data.ps1
```

### Paso 3: Acceder a la documentación de los Servicios
- **Productos:** http://localhost:8081/swagger-ui.html
- **Inventario:** http://localhost:8082/swagger-ui.html

---

## REQUISITOS

- Docker Desktop instalado
- PowerShell 5.0 o superior

---

## CONTENEDORES

El proyecto incluye 3 contenedores Docker:

1. **postgres-db** (Puerto 5432)
   - PostgreSQL 15 Alpine
   - Datos persistentes
   - Bases de datos: `productos_db`, `inventario_db`

2. **productos-service** (Puerto 8081)
   - API REST para gestión de productos
   - Swagger UI integrado
   - Autenticación API Key

3. **inventario-service** (Puerto 8082)
   - API REST para gestión de inventario
   - Swagger UI integrado
   - Comunicación con productos-service

---

## BASE DE DATOS: POSTGRESQL

### Credenciales
```
Host: localhost:5432
Usuario: postgres
Contraseña: postgres
```

### Bases de Datos
- `productos_db` - Datos de productos
- `inventario_db` - Datos de inventario

### Acceso a la Base de Datos

**Línea de comandos (psql)**
```powershell
docker exec -it postgres-db psql -U postgres -d productos_db
```

---

## DATOS INICIALES

El script `load-initial-data.ps1` carga **20 registros** automáticamente.

---

## AUTENTICACIÓN

### API Keys Requeridas

Incluir en todos los requests el header:

```
X-API-Key: secret-key-productos       (para Productos Service - puerto 8081)
X-API-Key: secret-key-inventario      (para Inventario Service - puerto 8082)
```

### Endpoints que NO Requieren API Key

- `/swagger-ui.html` - Documentación interactiva
- `/v3/api-docs` - Especificación OpenAPI JSON
- `/webjars/**` - Recursos de Swagger
- `/actuator/health` - Estado del servicio

---

## ENDPOINTS DISPONIBLES

### Productos Service (8081)

#### Obtener Productos
```powershell
GET http://localhost:8081/api/v1/productos?page=1&size=10
Header: X-API-Key: secret-key-productos
```

#### Crear Producto
```powershell
POST http://localhost:8081/api/v1/productos
Header: X-API-Key: secret-key-productos
Body: {
  "codigo": "PROD001",
  "nombre": "Mi Producto",
  "descripcion": "Descripción",
  "precio": 50000,
  "categoria": "Electrónica"
}
```

#### Obtener Producto por ID
```powershell
GET http://localhost:8081/api/v1/productos/{id}
Header: X-API-Key: secret-key-productos
```

#### Actualizar Producto
```powershell
PUT http://localhost:8081/api/v1/productos/{id}
Header: X-API-Key: secret-key-productos
Body: { producto actualizado }
```

#### Eliminar Producto
```powershell
DELETE http://localhost:8081/api/v1/productos/{id}
Header: X-API-Key: secret-key-productos
```

### Inventario Service (8082)

#### Obtener Inventario
```powershell
GET http://localhost:8082/api/v1/inventarios?page=1&size=10
Header: X-API-Key: secret-key-inventario
```

#### Crear Registro de Inventario
```powershell
POST http://localhost:8082/api/v1/inventarios
Header: X-API-Key: secret-key-inventario
Body: {
  "productoId": 1,
  "cantidad": 100,
  "cantidadMinima": 10
}
```

#### Obtener Inventario por ID
```powershell
GET http://localhost:8082/api/v1/inventarios/{id}
Header: X-API-Key: secret-key-inventario
```

#### Actualizar Inventario
```powershell
PUT http://localhost:8082/api/v1/inventarios/{id}
Header: X-API-Key: secret-key-inventario
Body: { inventario actualizado }
```

#### Eliminar Inventario
```powershell
DELETE http://localhost:8082/api/v1/inventarios/{id}
Header: X-API-Key: secret-key-inventario
```

---

## EJECUCIÓN LOCAL (SIN DOCKER)

### Terminal 1: Productos Service
```powershell
cd "productos-service"
mvn spring-boot:run
```

### Terminal 2: Inventario Service
```powershell
cd "inventario-service"
mvn spring-boot:run
```

### Nota
Para local development, necesitas PostgreSQL instalado localmente en puerto 5432.
