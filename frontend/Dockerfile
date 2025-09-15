# Dockerfile (producción)
FROM node:18-alpine AS builder
WORKDIR /app

# Cache de dependencias
COPY package*.json ./
RUN npm ci

# Build de producción
COPY . .
RUN npm run build

# Servidor web nginx
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html

# Configuración personalizada de nginx (opcional)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]