# Dockerfile para la aplicación Frontend Angular

# Stage 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Construir aplicación
RUN npm run build

# Stage 2: Serve
FROM node:18-alpine

WORKDIR /app

# Instalar servidor simple para servir archivos estáticos
RUN npm install -g http-server

# Copiar archivos compilados del stage anterior
COPY --from=builder /app/dist/frontend ./dist

# Exponer puerto
EXPOSE 4200

# Comando para ejecutar
CMD ["http-server", "./dist", "-p", "4200"]
