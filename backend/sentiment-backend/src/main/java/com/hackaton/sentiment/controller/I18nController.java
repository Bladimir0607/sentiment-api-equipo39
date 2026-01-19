package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.client.LibreTranslateClient;
import com.hackaton.sentiment.dto.LibreTranslateDTO;
import com.hackaton.sentiment.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de internacionalización (i18n).
 *
 * ENDPOINTS DISPONIBLES:
 *
 * 1. GET  /api/i18n/translate          - Traducir una clave individual
 * 2. POST /api/i18n/translate/batch    - Traducir múltiples textos
 * 3. GET  /api/i18n/translations/{lang} - Obtener todas las traducciones
 * 4. GET  /api/i18n/status             - Estado del sistema de traducción
 * 5. GET  /api/i18n/languages          - Idiomas disponibles (100+)
 * 6. POST /api/i18n/translate/direct   - Traducción directa con LibreTranslate
 * 7. GET  /api/i18n/hello              - Endpoint de prueba simple
 *
 * EJEMPLOS DE USO DESDE EL FRONTEND:
 *
 * // Traducir una clave
 * fetch('http://localhost:8080/api/i18n/translate?key=error.text.required&lang=en')
 *
 * // Cargar todas las traducciones al inicio
 * fetch('http://localhost:8080/api/i18n/translations/es')
 *
 * // Traducir múltiples textos
 * fetch('http://localhost:8080/api/i18n/translate/batch?lang=pt', {
 *   method: 'POST',
 *   body: JSON.stringify({ "button.analyze": "Analizar" })
 * })
 */
@Slf4j
@RestController
@RequestMapping("/api/i18n")
@RequiredArgsConstructor
@Tag(name = "Internationalization", description = "Gestión de traducciones e idiomas")
public class I18nController {

    private final TranslationService translationService;
    private final LibreTranslateClient libreTranslateClient;

    /**
     * Endpoint principal para obtener una traducción.
     * El frontend debe usar este endpoint para todos los textos.
     *
     * ESTRATEGIA DE TRADUCCIÓN:
     * 1. Busca en archivos.properties
     * 2. Busca en base de datos
     * 3. Traduce con LibreTranslate (IA)
     * 4. Fallback a inglés o texto original
     *
     * EJEMPLO DE USO:
     * GET /api/i18n/translate?Key=error.text.required&lang=en
     *
     * RESPUESTA:
     * {
     *   "key": "error.text.required",
     *   "language": "en",
     *   "translation": "Text cannot be empty",
     *   "success": "true"
     * }
     *
     * @param key Clave de traducción (ej: "error.text.required")
     * @param lang Código de idioma (es, en, pt)
     * @return Traducción del texto
     */
    @GetMapping("/translate")
    @Operation(summary = "Obtener traducción",
            description = "Traduce una clave a un idioma específico usando la estrategia de 4 niveles")
    public ResponseEntity<Map<String, String>> getTranslation(
            @RequestParam String key,
            @RequestParam(defaultValue = "es") String lang) {

        log.info("🔍 Solicitud de traducción: '{}' → '{}'", key, lang);

        String translated = translationService.translate(key, lang);

        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("language", lang);
        response.put("translation", translated);
        response.put("success", !key.equals(translated) ? "true" : "false");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para traducir múltiples textos de una vez.
     * El frontend puede cargar todas sus traducciones con una sola llamada.
     *
     * VENTAJA: Reduce el número de peticiones HTTP
     *
     * EJEMPLO DE USO:
     * POST /api/i18n/translate/batch?lang=en
     * Body: {
     *   "button.analyze": "Analizar",
     *   "button.clear": "Limpiar",
     *   "error.text.required": "El texto no puede estar vacío"
     * }
     *
     * RESPUESTA:
     * {
     *   "button.analyze": "Analyze",
     *   "button.clear": "Clear",
     *   "error.text.required": "Text cannot be empty"
     * }
     *
     * @param request Mapa de textos a traducir
     * @param lang Código de idioma destino
     * @return Mapa de textos traducidos
     */
    @PostMapping("/translate/batch")
    @Operation(summary = "Traducir múltiples textos",
            description = "Traduce un conjunto de textos en una sola petición (batch)")
    public ResponseEntity<Map<String, String>> translateBatch(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "es") String lang) {

        log.info("📦 Traducción en batch: {} textos → '{}'", request.size(), lang);

        Map<String, String> translations = translationService.translateBatch(request, lang);

        return ResponseEntity.ok(translations);
    }

    /**
     * Obtiene TODAS las traducciones para un idioma.
     * El frontend puede cargar esto al inicio de la aplicación.
     *
     * CASO DE USO RECOMENDADO:
     * - Cargar en el primer render de la app
     * - Guardar en contexto/store de React/Vue/Angular
     * - Evita múltiples llamadas HTTP durante la sesión
     *
     * EJEMPLO DE USO:
     * GET /api/i18n/translations/es
     *
     * RESPUESTA:
     * {
     *   "error.text.required": "El texto no puede estar vacío",
     *   "button.analyze": "Analizar",
     *   "sentiment.label.positive": "Positivo",
     *   ... (todas las traducciones)
     * }
     *
     * @param lang Código de idioma (es, en, pt)
     * @return Todas las traducciones disponibles
     */
    @GetMapping("/translations/{lang}")
    @Operation(summary = "Obtener todas las traducciones de un idioma",
            description = "Devuelve el diccionario completo de traducciones para cargar en el frontend")
    public ResponseEntity<Map<String, String>> getAllTranslations(
            @PathVariable String lang) {

        log.info("📚 Cargando todas las traducciones para '{}'", lang);

        Map<String, String> translations = translationService.getAllTranslationsForLanguage(lang);

        return ResponseEntity.ok(translations);
    }

    /**
     * Verifica el estado de los servicios de traducción.
     * Útil para debugging, monitoreo y health checks.
     *
     * INFORMACIÓN DEVUELTA:
     * - Estado de archivos .properties
     * - Conexión a base de datos
     * - Disponibilidad de LibreTranslate (Docker)
     * - Estado del caché Redis
     * - Cantidad de traducciones en DB
     *
     * EJEMPLO DE USO:
     * GET /api/i18n/status
     *
     * RESPUESTA:
     * {
     *   "messageSourceAvailable": true,
     *   "databaseAvailable": true,
     *   "libreTranslateEnabled": true,
     *   "libreTranslateAvailable": true,
     *   "libreTranslateConnected": true,
     *   "cacheEnabled": true,
     *   "databaseTranslationsCount": 45,
     *   "libreTranslateLanguagesCount": 118
     * }
     *
     * @return Estado de cada componente del sistema
     */
    @GetMapping("/status")
    @Operation(summary = "Estado del servicio de traducción",
            description = "Verifica la disponibilidad de cada nivel de traducción y servicios")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.info("🔍 Verificando estado de servicios de traducción");

        Map<String, Object> status = translationService.getServiceStatus();

        // Agregar información específica de LibreTranslate
        boolean libreAvailable = libreTranslateClient.isServiceAvailable();
        status.put("libreTranslateConnected", libreAvailable);

        if (libreAvailable) {
            List<LibreTranslateDTO.LanguageInfo> languages =
                    libreTranslateClient.getAvailableLanguages();
            status.put("libreTranslateLanguagesCount", languages.size());
        }

        return ResponseEntity.ok(status);
    }

    /**
     * Lista los idiomas disponibles en LibreTranslate.
     * Útil para ver qué idiomas se pueden usar en la aplicación.
     *
     * LibreTranslate soporta más de 100 idiomas:
     * - Español (es), Inglés (en), Portugués (pt)
     * - Francés (fr), Alemán (de), Italiano (it)
     * - Japonés (ja), Chino (zh), Árabe (ar)
     * - Y muchos más...
     *
     * EJEMPLO DE USO:
     * GET /api/i18n/languages
     *
     * RESPUESTA:
     * [
     *   { "code": "es", "name": "Spanish" },
     *   { "code": "en", "name": "English" },
     *   { "code": "pt", "name": "Portuguese" },
     *   ...
     * ]
     *
     * @return Lista de idiomas soportados con código y nombre
     */
    @GetMapping("/languages")
    @Operation(summary = "Idiomas disponibles",
            description = "Lista todos los idiomas soportados por LibreTranslate (100+)")
    public ResponseEntity<List<LibreTranslateDTO.LanguageInfo>> getAvailableLanguages() {
        log.info("📋 Obteniendo idiomas disponibles");

        List<LibreTranslateDTO.LanguageInfo> languages =
                libreTranslateClient.getAvailableLanguages();

        return ResponseEntity.ok(languages);
    }

    /**
     * Endpoint para probar traducción directa con LibreTranslate.
     * Útil para debugging y pruebas sin usar caché ni base de datos.
     *
     * DIFERENCIA CON /translate:
     * - /translate: usa estrategia de 4 niveles + caché
     * - /translate/direct: llama DIRECTAMENTE a LibreTranslate
     *
     * EJEMPLO DE USO:
     * POST /api/i18n/translate/direct?text=Hola mundo&source=es&target=en
     *
     * RESPUESTA:
     * {
     *   "original": "Hola mundo",
     *   "sourceLanguage": "es",
     *   "targetLanguage": "en",
     *   "translated": "Hello world",
     *   "timestamp": 1705234567890
     * }
     *
     * @param text Texto a traducir
     * @param source Idioma origen (o "auto" para detectar)
     * @param target Idioma destino
     * @return Texto traducido con metadatos
     */
    @PostMapping("/translate/direct")
    @Operation(summary = "Traducción directa con LibreTranslate",
            description = "Traduce directamente sin usar caché ni base de datos (solo para testing)")
    public ResponseEntity<Map<String, Object>> translateDirect(
            @RequestParam String text,
            @RequestParam(defaultValue = "auto") String source,
            @RequestParam String target) {

        log.info("🌐 Traducción directa: '{}' | {} → {}", text, source, target);

        String translated = libreTranslateClient.translate(text, source, target);

        Map<String, Object> response = new HashMap<>();
        response.put("original", text);
        response.put("sourceLanguage", source);
        response.put("targetLanguage", target);
        response.put("translated", translated);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba simple para verificar que el i18n funciona.
     * Detecta el idioma desde el header HTTP "Accept-Language".
     *
     * EJEMPLO DE USO DESDE EL NAVEGADOR:
     * GET /api/i18n/hello
     * Header: Accept-Language: pt
     *
     * RESPUESTA:
     * "Análise de sentimiento concluída"
     *
     * EJEMPLO DE USO CON CURL:
     * curl -H "Accept-Language: en" http://localhost:8080/api/i18n/hello
     *
     * RESPUESTA:
     * "Sentiment analysis completed"
     *
     * @param lang Código de idioma desde header (auto-detectado)
     * @return Mensaje de bienvenida traducido
     */
    @GetMapping("/hello")
    @Operation(summary = "Prueba de traducción simple",
            description = "Devuelve un mensaje de bienvenida en el idioma especificado (header Accept-Language)")
    public ResponseEntity<String> sayHello(
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {

        log.info("👋 Saludo solicitado en '{}'", lang);

        String message = translationService.translate("sentiment.analyze.success", lang);

        return ResponseEntity.ok(message);
    }
}