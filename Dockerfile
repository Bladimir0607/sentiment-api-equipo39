# Etapa 1: Construcción (Build)
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Etapa 2: Servidor de producción
FROM nginx:stable-alpine

# Copiamos los archivos compilados de Vite
COPY --from=build /app/dist /usr/share/nginx/html

# CONFIGURACIÓN CRÍTICA: Crea un archivo de config para Nginx in-situ
# Esto permite que React Router funcione correctamente en la nube
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]