
## 🖌️ SentimAI — Frontend

### Visión General

**SentimAI Frontend** es una *Single Page Application (SPA)* orientada a analítica avanzada de sentimiento, con una interfaz tipo **AI Control Panel**.
El dashboard traduce las respuestas del motor de IA en **insights visuales inmediatos**, combinando diseño futurista, animaciones suaves y feedback en tiempo real.

La aplicación está pensada para **usuarios técnicos y analistas**, con roles administrativos y acceso a módulos experimentales (*Labs*).

### Integrantes (Frontend Team)

- **Desarrollador Frontend Principal:** Jhonatan Osorio
- **Apoyo en Integración Backend / IA:**
  - Equipo Data-science
  - Equipo Backend

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
=======
# sentiment-api-equipo39
Proyecto Hackathon ONE – Análisis de Sentimientos

## 🚀 Backend desplegado en Oracle Cloud Infrastructure (OCI)

El backend del proyecto se encuentra desplegado y en ejecución sobre **Oracle Cloud Infrastructure (OCI)**.  
Desde esta dirección IP se puede acceder al servicio de análisis de sentimientos y probar sus endpoints directamente.

### 🌐 IP pública del backend (incluye documentación Swagger)
Permite validar y consumir los endpoints de análisis de sentimientos desde el navegador o herramientas como Postman.

👉 http://140.84.161.47/

### 📄 Documentación Swagger
La documentación interactiva de la API está disponible en el endpoint de Swagger, donde se pueden visualizar y probar todas las rutas expuestas por el backend.
