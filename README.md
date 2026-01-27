# 🎭 SentimAI Platform

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green?style=for-the-badge&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-red?style=for-the-badge&logo=redis)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker)

---

## 📄 Contenidos

1. [🖌️ Front-End](#visión-general)
2. [🖥️ Back-End](#tabla-de-contenidos)
3. [🧠 Data Science & Modelo](#-data-science--modelo)
4. [🙏 Agradecimientos](#-agradecimientos) 

---

## 🖌️ SentimAI — Frontend

### Visión General

**SentimAI Frontend** es una *Single Page Application (SPA)* orientada a analítica avanzada de sentimiento, con una interfaz tipo **AI Control Panel**.
El dashboard traduce las respuestas del motor de IA en **insights visuales inmediatos**, combinando diseño futurista, animaciones suaves y feedback en tiempo real.

La aplicación está pensada para **usuarios técnicos y analistas**, con roles administrativos y acceso a módulos experimentales (*Labs*).

### Integrantes (Team)
- **Desarrollador Frontend Principal:** Jhonatan Osorio
- **Apoyo en Integración Backend / IA:**
  - Equipo Data-science:

💻 Yohan Sebastian Ospina Gonzalez

💻 Julio Alejandro Serrepe Ramírez

  - Equipo Backend:

💻 Víctor Hugo Bardales Pérez

💻 Mario Fernando Perez Martinez

💻 Bladimir Antonio Ventura Paniagua

💻 Jhonatan Osorio

### Módulos de la Interfaz

#### 🔐 Login (Tokenizado)

![Login del sistema](https://raw.githubusercontent.com/JhonatanO24/sentiment-api-equipo39/refs/heads/backend/backend/assets/dash_login.jpg)

#### 🔮 Analyzer (Análisis de Sentimiento)


![Vista principal del sistema](https://raw.githubusercontent.com/JhonatanO24/sentiment-api-equipo39/refs/heads/backend/backend/assets/dash_texting.jpg)

- Campo de entrada para texto libre con *placeholder dinámico*.
- Botón **Analizar ⚡** con estado reactivo.
- Indicadores de estado:
  - `READY`
  - `ENGINE: ONLINE`
- Sugerencias rápidas de prueba (*Happy / Sad / Neutral*).
- Feedback visual del procesamiento (UX orientada a inmediatez).
- Comunicación cifrada **End-to-End Encrypted**.

#### 📂 Batch CSV

![Procesamiento de lotes con archivo csv](https://raw.githubusercontent.com/JhonatanO24/sentiment-api-equipo39/refs/heads/backend/backend/assets/dash_batch.jpg)

Módulo especializado para análisis masivo.

- Carga de archivos `.csv`.
- Procesamiento por lotes usando el motor de IA.
- Pensado para análisis de grandes volúmenes (reviews, tickets, encuestas).
- Preparado para futuras exportaciones de resultados.

#### 📊 Global Stats

![Vista de estadísticas](https://raw.githubusercontent.com/JhonatanO24/sentiment-api-equipo39/refs/heads/backend/backend/assets/dash_stats.jpg)

Dashboard estadístico agregado.

- Visualización de distribución de sentimientos.
- Gráficos interactivos (pastel / barras).
- Enfoque en métricas globales del sistema.
- Ideal para monitoreo y toma de decisiones estratégicas.

#### 🧪 SentimAI Labs

Sección experimental del sistema.

- Acceso exclusivo para usuarios **ADMIN**.
- Pruebas de nuevas funcionalidades.
- Iteración rápida de features basadas en IA.
- Entorno controlado para innovación.

### Gestión de Estado y Seguridad

- Manejo de sesión autenticada.
- Indicador visual de:
  - Usuario activo (`ADMIN`)
  - Estado del motor (`ONLINE`)
- Contextos globales para:
  - Autenticación
  - Idioma (ES / EN)
- Comunicación segura con backend mediante **Axios**.

### Stack Tecnológico

| Tecnología       | Uso en la Interfaz                         | Versión |
|------------------|--------------------------------------------|---------|
| React            | UI principal (SPA)                         | 19.2.0  |
| Vite             | Dev server y build ultra rápido            | 7.2.4   |
| Tailwind CSS     | Estilos utilitarios + tema oscuro          | 4.1.18  |
| Recharts         | Gráficos estadísticos interactivos         | 3.6.0   |
| Framer Motion    | Animaciones y micro-interacciones          | 12.24.10|
| Axios            | Cliente HTTP                               | 1.13.2  |
| i18next          | Internacionalización dinámica              | 25.8.0  |
| Lucide React     | Iconografía minimalista vectorial          | 0.562.0 |

### Arquitectura del Proyecto (Frontend)

Estructura modular basada en **Hooks + Context API**:

```txt
src/
 ├── api/
 │   ├── sentimentApi.js
 │   └── authApi.js
 ├── components/
 │   ├── UI
 │   ├── Cards
 │   └── Modals
 ├── context/
 │   ├── AuthContext.jsx
 │   └── LanguageContext.jsx
 ├── pages/
 │   ├── Analyzer.jsx
 │   ├── BatchAnalysis.jsx
 │   └── GlobalStats.jsx
```

**Separación clara entre:**
- Lógica de negocio
- Presentación visual
- Consumo de IA

**Integración con el Modelo de IA**
El frontend consume el endpoint:

`POST /sentiment-explain`

Input:
```json
{
  "text": "El servicio fue excelente"
}
```

**Renderizado en UI:**
- Label: Positivo / Neutral / Negativo
- Probabilidad: Indicador porcentual visual.
- Keywords: Tags resaltados para explicabilidad.
- Colores: Verde, amarillo o rojo según sentimiento.

**Roadmap:**
- [x] Dashboard principal con análisis en tiempo real.
- [x] Batch CSV para análisis masivo.
- [x] Internacionalización ES / EN / PT.

---

## 🖥️ Sentiment Analysis Backend API

**API REST de análisis de sentimientos con Machine Learning**

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

### Tabla de Contenidos

- [🎯 Overview](#-overview)
- [✨ Características](#-características)
- [🏗️ Arquitectura](#️-arquitectura)
- [🚀 Quick Start](#-quick-start)
- [📚 Documentación de la API](#-documentación-de-la-api)
- [🔧 Configuración](#-configuración)
- [🐳 Docker](#-docker)
- [🧪 Testing](#-testing)
- [📊 Endpoints](#-endpoints)
- [🔐 Seguridad](#-seguridad)
- [🌐 Internacionalización](#-internacionalización)
- [📈 Performance](#-performance)
- [📚 Documentación Adicional](#-documentación-adicional)

### 🎯 Overview

El **Sentiment Analysis Backend** es una API REST robusta desarrollada con Spring Boot 4.0 y Java 21 que proporciona servicios de análisis de sentimientos mediante Machine Learning. Esta API es el componente central del proyecto Hackathon Oracle ONE, diseñada para procesar texto y determinar el sentimiento emocional (positivo/negativo) con alta precisión.

#### ¿Qué hace?

- **Análisis de sentimientos** en tiempo real usando modelos de Machine Learning
- **Traducción automática** con LibreTranslate para soporte multiidioma
- **Sistema de caché** con Redis para optimizar rendimiento
- **Autenticación JWT** para gestión segura de usuarios
- **Internacionalización** completa (ES, EN, PT)
- **Estadísticas avanzadas** y analytics de datos

### ✨ Características

#### 🧠 Machine Learning Integration
- Conexión con microservicio Python para análisis de sentimientos
- Procesamiento de texto en tiempo real
- Predicciones con probabilidades de confianza

#### 🌍 Soporte Multiidioma
- Traducción automática con LibreTranslate
- Internacionalización completa (ES, EN, PT)
- Detección automática de idioma

#### 🔐 Seguridad Avanzada
- Autenticación JWT (JSON Web Tokens)
- Roles de usuario (USER, ADMIN)
- Encriptación de contraseñas con BCrypt
- CORS configurado para frontend

#### ⚡ Alto Rendimiento
- Sistema de caché con Redis
- Pool de conexiones con HikariCP
- Procesamiento concurrente con WebFlux
- Optimización de consultas JPA

#### 📊 Analytics & Estadísticas
- Estadísticas globales de sentimientos
- Historial de análisis por usuario
- Métricas agregadas en tiempo real

### 🏗️ Arquitectura

![Arquitectura de la API](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/arquitectura_api.png?raw=true)

#### 📁 Estructura del Proyecto

```
sentiment-backend/
├── 📁 src/main/java/com/hackaton/sentiment/
│   ├── 🎯 SentimentBackendApplication.java
│   ├── 📁 controller/      # Endpoints REST
│   ├── 📁 service/         # Lógica de negocio
│   ├── 📁 repository/      # Acceso a datos
│   ├── 📁 entity/          # Modelos JPA
│   ├── 📁 dto/             # Data Transfer Objects
│   ├── 📁 config/          # Configuración Spring
│   ├── 📁 security/        # JWT y seguridad
│   └── 📁 util/            # Utilidades
├── 📁 src/main/resources/
│   ├── 📄 application.yml  # Configuración principal
│   ├── 📁 i18n/            # Traducciones
│   ├── 📁 db/migration/    # Flyway migrations
│   └── 📁 init-scripts/    # Scripts SQL iniciales
├── 📁 src/test/            # Tests unitarios e integración
├── 🐳 docker-compose.yml   # Orquestación de servicios
├── 🐳 Dockerfile           # Imagen Docker
└── 📄 pom.xml              # Dependencias Maven
```

### 🚀 Quick Start

#### 📋 Prerrequisitos

- **Java 21+** - [Download JDK](https://adoptium.net/)
- **Maven 3.8+** - [Install Maven](https://maven.apache.org/install.html)
- **MySQL 8.0+** - [Download MySQL](https://dev.mysql.com/downloads/mysql/)
- **Redis 6.0+** - [Install Redis](https://redis.io/download)
- **Docker & Docker Compose** (opcional) - [Get Docker](https://www.docker.com/)

#### ⚡ Instalación Rápida

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd sentiment-backend
   ```

2. **Configurar variables de entorno**
   ```bash
   cp .env.example .env
   # Editar .env con tus credenciales
   ```

3. **Iniciar con Docker (Recomendado)**
   ```bash
   docker-compose up -d
   ```

4. **O iniciar localmente**
   ```bash
   # Crear base de datos
   mysql -u root -p -e "CREATE DATABASE sentimentdb;"

   # Ejecutar aplicación
   ./mvnw spring-boot:run
   ```

5. **Verificar instalación**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

🎉 **¡Listo!** La API está disponible en `http://localhost:8080`

### 📚 Documentación de la API

#### 🌐 Swagger UI
Accede a la documentación interactiva de la API:
`http://localhost:8080/swagger-ui.html`

#### 📖 OpenAPI Specification
`http://localhost:8080/v3/api-docs`

### 🔧 Configuración

#### 📄 Perfiles Disponibles
![Perfiles de Usuario](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/perfiles.png?raw=true)

#### 🔑 Variables de Entorno

```bash
# Base de Datos
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/sentimentdb
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=tu_password

# JWT
JWT_SECRET=8jLc0f8Tz/b3CEIIu5u5o7W6KbFc3cWWnmlQMULdSqA=
JWT_EXPIRATION=86400000

# Servicios Externos
LIBRETRANSLATE_URL=http://localhost:5000
ML_SERVICE_URL=http://localhost:8000

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

### 🐳 Docker

#### 🎯 Docker Compose Completo
El proyecto incluye un entorno Docker completo con todos los servicios:

```yaml
services:
  🐬 mysql:       # Base de datos
  🔴 redis:       # Caché
  🌐 libretranslate: # Traducción automática
  ☕ backend:     # API Spring Boot
```

#### 🚀 Ejecutar con Docker

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend

# Detener servicios
docker-compose down
```

#### 📊 Estado de los Servicios

```bash
# Ver todos los contenedores
docker ps

# Ver salud de los servicios
docker-compose ps
```

### 🧪 Testing

#### 🎯 Tipos de Tests
- **Unit Tests**: Tests de servicios y componentes individuales
- **Integration Tests**: Tests de integración con base de datos
- **API Tests**: Tests de endpoints REST
- **Testcontainers**: Tests con contenedores Docker reales

#### 🚀 Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=SentimentControllerTest

# Tests con cobertura
./mvnw jacoco:report
```

#### 📊 Reportes de Tests
Los reportes se generan en:
- **Surefire**: `target/surefire-reports/`
- **JaCoCo**: `target/site/jacoco/`

### 📊 Endpoints

#### 🎭 Análisis de Sentimientos
![Análisis de Sentimiento](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/analisis_sentimiento.png?raw=true)

#### 👤 Gestión de Usuarios
![Gestión de Usuarios](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/gestion_usuarios.png?raw=true)

#### 🌐 Internacionalización
![Internacionalización](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/internacionalizacion.png?raw=true)

#### 💊 Health Check
![Health Check](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/healthcheck.png?raw=true)

### 🔐 Seguridad

#### 🛡️ JWT Authentication
El sistema usa JSON Web Tokens para autenticación:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

#### 📋 Headers de Autenticación

```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

#### 🎭 Roles de Usuario
![Roles de Usuario](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/rol_usuario.png?raw=true)

### 🌐 Internacionalización

#### 🗣️ Idiomas Soportados
![Internalization](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/internalization.png?raw=true)

#### 📁 Archivos de Traducción
```
src/main/resources/i18n/
├── 📄 messages_es.properties
├── 📄 messages_en.properties
└── 📄 messages_pt.properties
```

#### 🔄 Traducción Automática
La API integra LibreTranslate para traducción automática:

```bash
# Ejemplo de traducción
curl -X POST "http://localhost:8080/i18n/translate" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Hello world",
    "from": "en",
    "to": "es"
  }'
```

### 📈 Performance

#### ⚡ Optimizaciones
- **Redis Cache**: Cache de traducciones con TTL de 1 hora
- **Connection Pooling**: HikariCP con pool de 10 conexiones
- **Concurrent Processing**: WebFlux para operaciones I/O
- **JPA Optimizations**: Queries optimizadas y fetch strategies

#### 📊 Métricas
![Métricas del Sistema](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/backend/backend/sentiment-backend/src/main/resources/assets/metricas.png?raw=true)

### 🤝 Contribuir

#### 📋 Guía de Contribución
1. **Fork** el repositorio
2. **Crear** rama feature (`git checkout -b feature/amazing-feature`)
3. **Commit** cambios (`git commit -m 'Add amazing feature'`)
4. **Push** a la rama (`git push origin feature/amazing-feature`)
5. **Abrir** Pull Request

#### 🎯 Convenciones de Código
- **Java**: Seguir Google Java Style Guide
- **Commits**: Usar [Conventional Commits](https://conventionalcommits.org/)
- **Tests**: Mantener >80% de cobertura
- **Docs**: Actualizar Swagger para nuevos endpoints

#### 🐛 Reportar Issues
Usar las plantillas de GitHub Issues:
- 🐛 **Bug Report**: Para errores
- ✨ **Feature Request**: Para nuevas funcionalidades
- 📚 **Documentation**: Para mejoras en docs

### 📚 Documentación Adicional
- **[API Documentation](http://localhost:8080/swagger-ui.html)**: Swagger UI interactiva
- **[Javadoc](target/site/apidocs/index.html)**: Documentación de código
- **[JaCoCo Report](target/site/jacoco/index.html)**: Cobertura de tests
- **[JWT Setup](JWT_SETUP.md)**: Configuración detallada de JWT

---

## 🧠 Data Science & Modelo

### Tabla de Contenidos

* [🎯 Objetivo](#objetivo-del-modelo)
* [📜 Datasets](#datasets)
* [🅾️ 2 Modelos](#modelo-binario)
* [🧹 Limpieza](#limpieza-y-preprocesamiento) 
* [↘️ Vectorización (TF-IDF)](#vectorización-de-texto-tf-idf)
* [🔰 Modelo de Clasificación](#modelo-de-clasificación)
* [📏 Métricas de evaluación](#métricas-de-evaluación)
* [❌ Matriz de confusión sin umbral](#matriz-de-confusión-sin-umbral)
* [✅ Matriz de confusión con umbral](#matriz-de-confusión-con-umbral-051)
* [💾 Serialización del modelo](#serialización-del-modelo)
* [🛜 Integración con la API](#integración-con-la-api)
* [🧠 Ejemplos de predicción](#ejemplos-de-predicción)
* [🚫 Limitaciones](#limitaciones)
* [✨ Posibles mejoras futuras](#posibles-mejoras-futuras)



### 🎯Objetivo del modelo

El objetivo de este modelo es clasificar automáticamente textos de reseñas y comentarios en función de su sentimiento, permitiendo identificar si un mensaje expresa una opinión positiva o negativa. Dentro de las necesidades del cliente se encuentran dar atención a los usuarios, priorizando a los comentarios negativos.

Inicialmente se intentó construir un modelo de clasificación ternaria (Positivo / Neutro / Negativo) utilizando reseñas de productos de Amazon, donde las clases se definieron a partir del número de estrellas (Con 3 estrellas como neutro). Sin embargo, se observó que muchos usuarios asignaban tres estrellas a comentarios claramente negativos, lo que generaba ruido en las etiquetas y afectaba la calidad de las predicciones, especialmente para la clase “Neutro”.

Debido a esta inconsistencia en el etiquetado, se decidió trabajar con un enfoque binario (Positivo / Negativo), utilizando reseñas de películas del dataset de IMDB en español, donde las clases están mejor definidas. Este cambio permitió obtener un modelo más confiable y con mejor desempeño.

El modelo es consumido por una API desarrollada en FastAPI, la cual recibe un JSON con texto de usuarios (reseñas, comentarios o mensajes) y devuelve automáticamente:

- La predicción de sentimiento (Positivo o Negativo)
- La probabilidad asociada a dicha predicción
- Las palabras que dieron peso a la predicción

Esta solución está orientada a empresas de atención al cliente, marketing y operaciones que necesitan analizar grandes volúmenes de opiniones para:

- Detectar rápidamente quejas o problemas
- Priorizar respuestas a comentarios negativos
- Medir la satisfacción de los clientes a lo largo del tiempo

### 📜DataSets

Para el desarrollo del proyecto se trabajó con dos enfoques: un modelo binario y un intento de modelo ternario.

#### Modelo binario
- Datos obtenidos desde Kaggle
- Se utilizaron aproximadamente 50.000 registros
- Idioma original: inglés y español
- Se filtraron para eliminar los comentarios en inglés
- Columnas principales: text y sentiment

#### Modelo ternario
- Datos obtenidos desde Hugging Face
- Se utilizaron aproximadamente 210.000 registros
- Idioma: español
- Columnas principales: text y label

El modelo elegido para el MVP fue el binario, ya que presentó un comportamiento más estable y con mayor precisión que el ternario.

### Distribución del tamaño del texto según el sentimiento

Distribución del tamaño del texto según el sentimiento

![Distribución del tamaño del texto según el sentimiento](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/distribucion_tama%C3%B1o_texto_segun_sentimiento.png?raw=true)
![Longitud de textos por tipo de sentimiento](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/longitud_textos_por_tipo_sentimiento.png?raw=true)

### 🧹Limpieza y preprocesamiento

En ambos notebooks se realizaron los siguientes pasos:

- Revisión de valores nulos
- Conversión de todos los textos a minúsculas
- Eliminación de signos de puntuación y caracteres especiales
- Eliminación de stopwords
- Eliminación de reseñas duplicadas

En el modelo binario se utilizaron stopwords personalizadas.

**Filtrado de idioma en el modelo binario.**
Durante el análisis se observó que algunas reseñas muy largas (más de 1000 palabras) aparecían como “español” en el dataset, pero en realidad estaban en inglés, por lo que se aplicó un filtro adicional por idioma para garantizar la coherencia del conjunto de datos.

### Vectorización de texto (TF-IDF)

Durante las iteraciones del proyecto se identificó una limitación común en modelos basados únicamente en palabras: las negaciones (ej. “no fue una buena experiencia”) tendían a ser mal interpretadas cuando se eliminaban stopwords o cuando el modelo dependía solo de tokens completos.

Para mitigar este problema, se adoptó una estrategia de vectorización híbrida, combinando dos enfoques complementarios:

- **TF-IDF a nivel de palabras**: Captura el significado semántico global del texto.
- Utiliza un vectorizador con unigramas y bigramas, también otro con trigramas hasta 5 combinaciones de palabras, permitiendo representar expresiones como “muy bueno” o “nada recomendable”.
- Se aplican stopwords personalizadas para reducir ruido, manteniendo términos relevantes.
- La frecuencia se suaviza para evitar que palabras muy repetidas dominen el modelo.

### 🔰Modelo de Clasificación

Se utilizó Regresión Logística como modelo de clasificación, configurada con los siguientes parámetros:

- `random_state = 42`
- `max_iter = 1000`
- `class_weight = 'balanced'`
- `solver = 'liblinear'`
- `C = 20`

Este modelo fue elegido porque:
- Presenta un buen desempeño en tareas de clasificación de texto cuando se combina con representaciones TF-IDF.
- Es estable, eficiente y fácilmente interpretable, lo que lo hace adecuado para un entorno de producción.
- Permite obtener probabilidades de predicción, requisito clave para la API desarrollada.

El uso de `class_weight='balanced'` ayuda a compensar posibles desbalances entre clases, mientras que `max_iter=1000` asegura la convergencia del modelo.

El parámetro `C` se incrementó para reducir la regularización, permitiendo al modelo aprender patrones más complejos derivados de la combinación de vectorización entre palabras y caracteres, lo cual contribuyó a una mejor interpretación de frases con negación.

### 📏Métricas de evaluación

```
              precision    recall  f1-score    support

negativo       0.88       0.86       0.87       7155
positivo       0.86       0.88       0.87       7155

accuracy                           0.87      14310
macro avg      0.87       0.87       0.87      14310
weighted avg   0.87       0.87       0.87      14310
```

#### ❌Matriz de confusión sin umbral
![Matriz de confusión sin umbral](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/matriz_de_confucion_no-umbral.png?raw=true)

#### ✅Matriz de confusión con umbral 0.51
![Matriz de confusión con umbral](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/matriz_de%20confucion_con_umbral.png?raw=true)

### Matriz de Confusión

En el modelo binario, después del entrenamiento se obtienen 1.646 predicciones erróneas de 14.310 predicciones totales, lo que representa tan solo un 11,51% de error.

Los errores se ven reflejados de mejor manera en la matriz de confusión.
En la matriz se observan los errores divididos en los falsos positivos (comentarios negativos clasificados como positivos) y los falsos negativos (comentarios positivos clasificados como negativos).

### Selección del umbral

Al probar diferentes umbrales, se llegó a la conclusión de que el umbral más conveniente era 0.51 porque así disminuían los falsos positivos sin aumentar de manera tan exagerada los falsos negativos.

```
Umbral     Falsos Positivos (Riesgo) Falsos Negativos (Revisión extra)
----------------------------------------------------------------------
0.4        967                       517
0.45       875                       591
0.5        769                       670
0.51       746                       688
0.55       681                       764
0.58       633                       824
0.6        605                       879
0.61       592                       899
0.62       580                       919
0.63       559                       939
0.635      550                       952
0.64       544                       959
0.645      533                       976
0.65       527                       994
0.655      517                       1007
0.66       505                       1016
0.665      501                       1031
0.67       494                       1051
0.675      482                       1066
0.68       478                       1075
0.69       461                       1104
0.7        441                       1144
0.75       378                       1303
0.8        312                       1502
```

### Selección del modelo final

De los dos modelos desarrollados se seleccionó el modelo binario porque el modelo ternario presentó problemas para identificar correctamente la clase neutra. Por esta razón, se eligió el modelo binario como versión final para el MVP.

### 💾Serialización del modelo

Para la integración con el Back-End se serializó el pipeline completo ((vectorizador1 + vectorizador2) + modelo) usando joblib.

#### Pipeline final
- TF-IDF con los parámetros definidos
- Regresión Logística entrenada

El pipeline completo se guarda en un solo archivo para que el Back-End pueda cargarlo y hacer predicciones con el modelo ya entrenado.

### Ejecución del modelo

Existen dos notebooks:
- `Modelo_hackaton_binario.ipynb`
- `Modelo_hackaton_ternario.ipynb`

El notebook principal utilizado para el MVP es: `Modelo_hackaton_binario.ipynb`

Para entrenar el modelo y obtener el modelo serializado:
1. Abrir el notebook
2. Ejecutar todas las celdas en orden, desde el inicio hasta el final
3. Al finalizar, se genera el archivo serializado del modelo

### 🛜Integración con la API

El modelo se carga y expone a través de un endpoint con FastAPI.

- **POST /sentiment**: Recibe el texto y hace la predicción retornando el sentimiento y la probabilidad.
- **POST /sentiment-explain**: Recibe el texto, hace la predicción con el modelo, además utilizando un umbral y retorna el sentimiento, la probabilidad y las 3 palabras más significativas para la predicción. Este fue el endpoint utilizado por el backend en nuestro MVP por las palabras y el uso del umbral.

El umbral se definió en 0.51, obtenido a través de iteraciones en el notebook buscando el mejor rendimiento al priorizar las reseñas negativas.

Para las palabras se utiliza la función `explicar_prediccion()` que utiliza el vectorizador del modelo para transformar el texto de entrada y buscar esas palabras para posteriormente ordenarlas según el impacto que tiene, en orden descendente para las predicciones 'Positivo' y en orden ascendente para las predicciones 'Negativo', al final retorna las 3 palabras más significativas para la predicción.

#### Formato de entrada (JSON)
```json
{
  "text": "Comentario enviado desde el backend"
}
```

#### Formato de salida (JSON)
```json
{
    "original_text": "Comentario enviado desde el backend",
    "prevision": "Negativo/Positivo",
    "probabilidad": 0.999,
    "palabras_clave": [
        "Lista de máximo",
        "3 palabras con más",
        "peso para la predicción"
    ]
}
```

### 🧠Ejemplos de predicción

```json
{
    "original_text": "Hasta ahora me han funcionado perfectamente, compatibles con los perfiles amp y xmp, recomendadas, y gracias a su tamaño pude usar un enfriamiento mas grande.",
    "prevision": "Positivo",
    "probabilidad": 0.787,
    "palabras_clave": [
        "perfectamente",
        "gracias", 
        "grande"
    ]
}
```

```json
{
    "original_text": "Teléfono basura, no funciona como debería, se cortan las llamadas o simplemente no enlaza, además de que el audio del auricular es deficiente.",
    "prevision": "Negativo",
    "probabilidad": 0.955,
    "palabras_clave": [
        "basura",
        "simplemente",
        "deficiente"
    ]
}
```

```json
{
    "original_text": "No me gusto, el pantalon segun talla chico- mediano, parece grande-extra grande y muy largo, la sudadera esta super chica, tela muy delgada.",
    "prevision": "Negativo",
    "probabilidad": 0.82,
    "palabras_clave": [
        "no gusto",
        "delgada",
        "parece"
    ]
}
```

Ejemplos con reseñas de productos varios de "Amazon" y respuestas del modelo:

```json
{
  "original_text": "Nunca volvería a comprar en esta empresa. La tarjeta se convirtió en humo y no la cubrieron, lo que provocó daños físicos a bordo. A la que no puedes acceder a menos que te abran. Mis hijos de 11 años ahorraron todo el verano para que esto fuera basura en 2 meses",
  "prevision": "Negativo",
  "probabilidad": 0.72,
  "palabras_clave": ["basura", "menos", "bordo"]
}
```

```json
{
    "original_text": "El título dice 'Mancuernas' no especifica que solo es una.. Me siento estafado",
    "prevision": "Negativo",
    "probabilidad": 0.908,
    "palabras_clave": [
        "siento", 
        "título", 
        "solo"
    ]
}
```

```json
{
    "original_text": "Está descuadrada. No todos los tornillos entran bien y es difícil de armar",
    "prevision": "Negativo",
    "probabilidad": 0.553,
    "palabras_clave": [
        "entran",
        "difícil", 
        "bien"
    ]
}
```

### 🚫Limitaciones

- Actualmente, el modelo solo predice a **'Positivo'** o **'Negativo'**.
- El modelo funciona para el idioma español, aunque fue compensado en el backend usando traducciones.

### ✨Posibles mejoras futuras

- Mejorar los falsos negativos para reducir los tiempos necesarios para la atención y aumentar la calidad de predicciones del modelo.
- Implementar un rango más amplio de etiquetas, empezar con un modelo ternario y posiblemente expandirlo hasta abarcar **"Muy positivo"**, **"Positivo"**, **"Neutro"**, **"Negativo"** y **"Muy negativo"**.
- Entrenar modelos especializados a los idiomas disponibles en el frontend y que son traducidos en el backend.
- Mejorar cómo el modelo entienda las negaciones de las palabras positivas.


---

## ☁️ Infraestructura OCI & DevOps

**Despliegue escalable y contenerizado en Oracle Cloud Infrastructure**

![Oracle Cloud](https://img.shields.io/badge/Oracle_Cloud-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Bash](https://img.shields.io/badge/Bash-4EAA25?style=for-the-badge&logo=gnu-bash&logoColor=white)

### Visión General

La plataforma **SentimAI** se encuentra desplegada en una instancia de computación de alto rendimiento en **Oracle Cloud Infrastructure (OCI)**. Se optó por una arquitectura **Cloud-Native** basada 100% en contenedores Docker, lo que garantiza la consistencia entre los entornos de desarrollo y producción, facilitando la escalabilidad horizontal y el mantenimiento.

### 🏗️ Especificaciones del Servidor

El entorno productivo corre sobre una máquina virtual (VM) optimizada con las siguientes características:

| Recurso | Especificación | Propósito |
| :--- | :--- | :--- |
| **Proveedor** | Oracle Cloud Infrastructure (OCI) | Infraestructura nube |
| **S.O.** | Ubuntu 22.04 LTS (Jammy Jellyfish) | Sistema base estable y seguro |
| **Container Engine** | Docker 27.x + Docker Compose | Orquestación de microservicios |
| **Almacenamiento** | Block Volume (Boot Volume) | Persistencia de datos (MySQL/Redis) |
| **Acceso** | SSH (Key-based Authentication) | Administración remota segura |

### 🔄 Flujo de Despliegue (CI/CD Strategy)

Implementamos una estrategia de despliegue basada en imágenes inmutables alojadas en **Docker Hub**. Esto elimina el problema de *"funciona en mi máquina"* y asegura que el servidor siempre ejecute la versión exacta aprobada.

1.  **Build (Local):** Se compila el código Java/Python y se construyen las imágenes Docker.
    ```bash
    docker build -t hamminghk/sentiment-backend:v10 .
    ```

2.  **Push (Registry):** Las imágenes se suben al registro público.
    ```bash
    docker push hamminghk/sentiment-backend:v10
    ```

3.  **Deploy (OCI):** El servidor descarga la última versión y recrea los contenedores sin tiempo de inactividad perceptible.
    ```bash
    docker compose up -d --force-recreate
    ```

### 🛡️ Seguridad y Redes (VCN)

La seguridad perimetral se gestiona mediante las **Listas de Seguridad (Security Lists)** de la VCN (Virtual Cloud Network) de Oracle, permitiendo solo el tráfico estrictamente necesario.

#### Configuración de Puertos (Ingress Rules)

| Puerto | Protocolo | Servicio | Descripción |
| :--- | :--- | :--- | :--- |
| **22** | TCP | SSH | Acceso administrativo (restringido) |
| **8080** | TCP | Backend API | Acceso público a la API Spring Boot |
| **8000** | TCP | ML Service | Comunicación interna (Backend <-> ML) |
| **5000** | TCP | LibreTranslate | Servicio de traducción (Internal/Public) |
| **3306** | TCP | MySQL | Base de datos (Solo localhost/Docker network) |

> **Nota de Seguridad:** La base de datos y Redis no están expuestos a internet; solo son accesibles por los otros contenedores dentro de la red privada de Docker.

### 🐳 Orquestación de Servicios

Utilizamos `docker-compose` para definir la infraestructura como código (IaC). Esto permite levantar todo el ecosistema con un solo comando.

**Servicios activos en producción:**

* `sentiment-backend`: El núcleo de la aplicación (Spring Boot).
* `sentiment-ml`: Microservicio de Inteligencia Artificial (Python/FastAPI).
* `libretranslate`: Motor de traducción offline.
* `mysql-db`: Base de datos relacional para usuarios y logs.
* `redis-cache`: Caché de alto rendimiento para traducciones.

### 📊 Monitoreo y Mantenimiento

Para asegurar la salud del sistema en vivo, utilizamos comandos de monitoreo en tiempo real directamente en la instancia OCI:

**Ver logs en tiempo real:**
```bash
docker logs -f --tail 100 sentiment-backend

docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"

---

---

## 📄 Licencia

Este proyecto está licenciado bajo la **MIT License** - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 🙏 Agradecimientos

**Gracias a todos los contribuidores**

💻 Mario Fernando Perez Martinez

💻 Bladimir Antonio Ventura Paniagua

💻 Jhonatan Osorio

💻 Víctor Hugo Bardales Pérez

💻 Yohan Sebastian Ospina Gonzalez

💻 Julio Alejandro Serrepe Ramírez


**y a la comunidad Oracle ONE**

[![Made with ❤️](https://img.shields.io/badge/Made%20with%20❤️-red.svg)]()

**Desarrollado con ☕ y 🎵 durante el Hackathon Oracle ONE 2026**
