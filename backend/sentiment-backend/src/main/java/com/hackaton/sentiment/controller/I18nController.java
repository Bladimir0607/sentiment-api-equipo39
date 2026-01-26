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
 * Controlador REST para la gestión de internacionalización (i18n).
 *
 * <p>Este controlador expone endpoints para traducir textos, gestionar idiomas
 * y verificar el estado del sistema de traducciones.</p>
 *
 * <h3>Endpoints disponibles</h3>
 * <ul>
 *   <li>GET  /api/i18n/translate – Traducir una clave individual</li>
 *   <li>POST /api/i18n/translate/batch – Traducir múltiples textos</li>
 *   <li>GET /api/i18n/translations/{lang} – Obtener todas las traducciones</li>
 *   <li>GET /api/i18n/status – Estado del sistema de traducción</li>
 *   <li>GET  /api/i18n/languages – Idiomas disponibles</li>
 *   <li>POST /api/i18n/translate/direct – Traducción directa con LibreTranslate</li>
 *   <li>GET  /api/i18n/hello – Endpoint de prueba simple</li>
 * </ul>
 *
 * <h3>Ejemplos de uso desde el frontend</h3>
 *
 * <pre>
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
 * </pre>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 *
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
     *
     * <p>Este endpoint debe ser utilizado por el frontend para todos los textos
     * de la aplicación.</p>
     *
     * <h3>Estrategia de traducción</h3>
     * <ol>
     *   <li>Archivos <code>.properties</code></li>
     *   <li>Base de datos</li>
     *   <li>LibreTranslate (IA)</li>
     *   <li>Fallback a inglés o texto original</li>
     * </ol>
     *
     * <h3>Ejemplo de uso</h3>
     * <pre>
     * GET /api/i18n/translate?key=error.text.required&lang=en
     * </pre>
     *
     * @param key clave de traducción (ej. {@code error.text.required})
     * @param lang código de idioma (es, en, pt)
     * @return traducción del texto solicitada
     */
    @GetMapping("/translate")
    @Operation(
            summary = "Obtener traducción",
            description = "Traduce una clave a un idioma específico usando la estrategia de 4 niveles"
    )
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
     * Endpoint para traducir múltiples textos en una sola llamada.
     *
     * <p>Permite al frontend cargar todas sus traducciones de forma eficiente,
     * reduciendo el número de peticiones HTTP.</p>
     *
     * <h3>Ejemplo de uso</h3>
     * <pre>
     * POST /api/i18n/translate/batch?lang=en
     * </pre>
     *
     * @param request mapa de textos a traducir
     * @param lang código de idioma destino
     * @return mapa de textos traducidos
     */
    @PostMapping("/translate/batch")
    @Operation(
            summary = "Traducir múltiples textos",
            description = "Traduce un conjunto de textos en una sola petición (batch)"
    )
    public ResponseEntity<Map<String, String>> translateBatch(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "es") String lang) {

        log.info("📦 Traducción en batch: {} textos → '{}'", request.size(), lang);

        Map<String, String> translations =
                translationService.translateBatch(request, lang);

        return ResponseEntity.ok(translations);
    }

    /**
     * Obtiene todas las traducciones disponibles para un idioma.
     *
     * <p>Uso recomendado para cargar las traducciones al inicio de la aplicación
     * y almacenarlas en el estado global del frontend.</p>
     *
     * @param lang código de idioma
     * @return diccionario completo de traducciones
     */
    @GetMapping("/translations/{lang}")
    @Operation(
            summary = "Obtener todas las traducciones de un idioma",
            description = "Devuelve el diccionario completo de traducciones para cargar en el frontend"
    )
    public ResponseEntity<Map<String, String>> getAllTranslations(
            @PathVariable String lang) {

        log.info("📚 Cargando todas las traducciones para '{}'", lang);

        Map<String, String> translations =
                translationService.getAllTranslationsForLanguage(lang);

        return ResponseEntity.ok(translations);
    }

    /**
     * Verifica el estado de los servicios de traducción.
     *
     * <p>Este endpoint es útil para monitoreo, debugging y health checks.</p>
     *
     * <p>Incluye información sobre:</p>
     * <ul>
     *   <li>Archivos de propiedades</li>
     *   <li>Base de datos</li>
     *   <li>LibreTranslate</li>
     *   <li>Caché</li>
     * </ul>
     *
     * @return estado de cada componente del sistema
     */
    @GetMapping("/status")
    @Operation(
            summary = "Estado del servicio de traducción",
            description = "Verifica la disponibilidad de cada nivel de traducción y servicios"
    )
    public ResponseEntity<Map<String, Object>> getStatus() {

        log.info("🔍 Verificando estado de servicios de traducción");

        Map<String, Object> status =
                translationService.getServiceStatus();

        boolean libreAvailable =
                libreTranslateClient.isServiceAvailable();

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
     *
     * <p>LibreTranslate soporta más de 100 idiomas, incluyendo español, inglés,
     * portugués, francés, alemán, japonés, chino y árabe.</p>
     *
     * @return lista de idiomas soportados
     */
    @GetMapping("/languages")
    @Operation(
            summary = "Idiomas disponibles",
            description = "Lista todos los idiomas soportados por LibreTranslate (100+)"
    )
    public ResponseEntity<List<LibreTranslateDTO.LanguageInfo>> getAvailableLanguages() {

        log.info("📋 Obteniendo idiomas disponibles");

        List<LibreTranslateDTO.LanguageInfo> languages =
                libreTranslateClient.getAvailableLanguages();

        return ResponseEntity.ok(languages);
    }

    /**
     * Endpoint de traducción directa usando LibreTranslate.
     *
     * <p>No utiliza caché ni base de datos. Recomendado únicamente para pruebas
     * y debugging.</p>
     *
     * @param text texto a traducir
     * @param source idioma origen (o {@code auto})
     * @param target idioma destino
     * @return traducción con metadatos
     */
    @PostMapping("/translate/direct")
    @Operation(
            summary = "Traducción directa con LibreTranslate",
            description = "Traduce directamente sin usar caché ni base de datos (solo para testing)"
    )
    public ResponseEntity<Map<String, Object>> translateDirect(
            @RequestParam String text,
            @RequestParam(defaultValue = "auto") String source,
            @RequestParam String target) {

        log.info("🌐 Traducción directa: '{}' | {} → {}", text, source, target);

        String translated =
                libreTranslateClient.translate(text, source, target);

        Map<String, Object> response = new HashMap<>();
        response.put("original", text);
        response.put("sourceLanguage", source);
        response.put("targetLanguage", target);
        response.put("translated", translated);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba simple para verificar el funcionamiento de i18n.
     *
     * <p>El idioma se detecta a partir del header HTTP
     * {@code Accept-Language}.</p>
     *
     * @param lang idioma solicitado desde el header
     * @return mensaje traducido
     */
    @GetMapping("/hello")
    @Operation(
            summary = "Prueba de traducción simple",
            description = "Devuelve un mensaje de bienvenida en el idioma especificado (header Accept-Language)"
    )
    public ResponseEntity<String> sayHello(
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {

        log.info("👋 Saludo solicitado en '{}'", lang);

        String message =
                translationService.translate("sentiment.analyze.success", lang);

        return ResponseEntity.ok(message);
    }
}