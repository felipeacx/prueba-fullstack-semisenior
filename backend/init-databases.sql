-- Create databases for microservices
CREATE DATABASE productos_db;
CREATE DATABASE inventario_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE productos_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE inventario_db TO postgres;

