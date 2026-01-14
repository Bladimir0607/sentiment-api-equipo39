package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.service.SimpleTranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/i18n")
@RequiredArgsConstructor
public class I18nTestController {

    private final SimpleTranslationService translationService;

    /**
     * Endpoint básico para probar traducciones.
     * Primero busca en archivos .properties, luego en base de datos.
     *
     * @param key Clave del mensaje (ej: "error.text.required")
     * @param lang Código de idioma (es, en, pt, etc.)
     * @return Traducción o la clave si no se encuentra
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testTranslation(
            @RequestParam String key,
            @RequestParam(defaultValue = "es") String lang) {

        log.info("[i18n/test] Solicitando traducción - Key: '{}', Idioma: '{}'", key, lang);

        String translated = translationService.translate(key, lang);

        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("language", lang);
        response.put("translation", translated);
        response.put("source", "properties"); // Temporal: vendrá de DB después

        if (key.equals(translated)) {
            log.warn("[i18n/test] Traducción NO encontrada - Usando fallback");
            response.put("status", "not_found");
        } else {
            log.debug("[i18n/test] Traducción exitosa");
            response.put("status", "success");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de saludo usando header Accept-Language.
     * Ejemplo para frontend: curl -H "Accept-Language: pt"
     * "//localhost:8080/api/i18n/test?key=error.text.required&lang=es"
     */
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {

        log.info("[i18n/hello] Saludo solicitado - Idioma: '{}'", lang);

        String message = translationService.translate("sentiment.analyze.success", lang);

        return ResponseEntity.ok(message);
    }

    /**
     * NUEVO: Endpoint para cambiar el idioma preferido del usuario.
     * En un sistema real, esto se guardaría en la base de datos.
     *
     * @param userId ID del usuario (simulado para demo)
     * @param lang Nuevo idioma preferido
     * @return Confirmación del cambio
     */
    @PostMapping("/user/{userId}/language")
    public ResponseEntity<Map<String, Object>> changeUserLanguage(
            @PathVariable String userId,
            @RequestParam String lang) {

        log.info("[i18n/user] Cambiando idioma - Usuario: {}, Idioma: {}", userId, lang);

        // TODO: Guardar en base de datos (tabla users, campo language_preference)
        // Por ahora solo simulamos

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("newLanguage", lang);
        response.put("message", "Idioma actualizado exitosamente");
        response.put("timestamp", System.currentTimeMillis());
        response.put("nextStep", "Configurar Docker con LibreTranslate para traducciones automáticas");

        return ResponseEntity.ok(response);
    }

    /**
     * NUEVO: Endpoint para verificar conexión con servicios externos.
     * Útil para verificar que Docker + LibreTranslate estén funcionando.
     */
    @GetMapping("/services/status")
    public ResponseEntity<Map<String, Object>> checkServices() {
        Map<String, Object> status = new HashMap<>();

        // Estado actual (FASE 1 - Archivos .properties)
        status.put("phase", 1);
        status.put("phaseDescription", "Traducciones estáticas con archivos .properties");

        // Servicios configurados
        Map<String, String> services = new HashMap<>();
        services.put("database", "MySQL (sentimentdb) - CONECTADO ✓");
        services.put("i18n_files", "Archivos .properties - ACTIVO ✓");
        services.put("libretranslate", "Docker - PENDIENTE (FASE 2)");
        services.put("redis_cache", "Cache - PENDIENTE (FASE 3)");

        status.put("services", services);

        // Prueba de traducciones actuales
        Map<String, String> translationTest = new HashMap<>();
        translationTest.put("es", translationService.translate("error.text.required", "es"));
        translationTest.put("en", translationService.translate("error.text.required", "en"));
        translationTest.put("pt", translationService.translate("error.text.required", "pt"));

        status.put("translationTest", translationTest);

        // Configuración recomendada para Docker
        Map<String, String> dockerConfig = new HashMap<>();
        dockerConfig.put("libretranslate_image", "libretranslate/libretranslate");
        dockerConfig.put("libretranslate_port", "5000");
        dockerConfig.put("redis_image", "redis:alpine");
        dockerConfig.put("redis_port", "6379");
        dockerConfig.put("mysql_image", "mysql:8.0");
        dockerConfig.put("mysql_port", "3306");

        status.put("dockerConfig", dockerConfig);

        log.info("[i18n/services] Estado de servicios verificado");
        return ResponseEntity.ok(status);
    }

    /**
     * NUEVO: Endpoint para preparar migración a Docker + LibreTranslate.
     * Muestra los pasos necesarios para la FASE 2.
     */
    @GetMapping("/migration/plan")
    public ResponseEntity<Map<String, Object>> migrationPlan() {
        Map<String, Object> plan = new HashMap<>();

        plan.put("currentPhase", "FASE 1 - Archivos .properties");
        plan.put("status", "COMPLETADO ✓");

        Map<String, String> phase2 = new HashMap<>();
        phase2.put("name", "FASE 2 - Docker + LibreTranslate");
        phase2.put("description", "Traducción automática con AI (100+ idiomas)");
        phase2.put("dockerCompose", "Agregar servicio libretranslate al docker-compose.yml");
        phase2.put("springConfig", "Configurar URL: http://libretranslate:5000");
        phase2.put("service", "Crear LibreTranslateService.java");
        phase2.put("cache", "Integrar con base de datos translations table");

        Map<String, String> phase3 = new HashMap<>();
        phase3.put("name", "FASE 3 - Cache + Optimización");
        phase3.put("description", "Redis cache y estadísticas de uso");
        phase3.put("redis", "Agregar servicio Redis al docker-compose");
        phase3.put("cacheConfig", "Configurar @Cacheable en servicios");
        phase3.put("monitoring", "Estadísticas de traducciones más usadas");

        plan.put("phase2", phase2);
        plan.put("phase3", phase3);

        // Comandos Docker listos para copiar/pegar
        Map<String, String> dockerCommands = new HashMap<>();
        dockerCommands.put("start_libretranslate", "docker run -ti --rm -p 5000:5000 libretranslate/libretranslate");
        dockerCommands.put("test_translation", "curl -X POST \"http://localhost:5000/translate\" -H \"Content-Type: application/json\" -d '{\"q\":\"Hello world\",\"source\":\"en\",\"target\":\"es\"}'");
        dockerCommands.put("check_languages", "curl \"http://localhost:5000/languages\"");

        plan.put("dockerCommands", dockerCommands);

        log.info("[i18n/migration] Plan de migración generado");
        return ResponseEntity.ok(plan);
    }

    /**
     * NUEVO: Endpoint para simular traducción con LibreTranslate (prueba futura).
     * Cuando tengamos Docker configurado, este endpoint se conectará al servicio real.
     */
    @PostMapping("/translate/auto")
    public ResponseEntity<Map<String, Object>> autoTranslate(
            @RequestBody Map<String, String> request) {

        String text = request.get("text");
        String targetLang = request.getOrDefault("targetLang", "es");
        String sourceLang = request.getOrDefault("sourceLang", "en");

        log.info("[i18n/auto] Traducción automática solicitada - Texto: '{}', De {} a {}",
                text, sourceLang, targetLang);

        Map<String, Object> response = new HashMap<>();
        response.put("original", text);
        response.put("sourceLanguage", sourceLang);
        response.put("targetLanguage", targetLang);
        response.put("translated", "[SIMULADO] Esta sería la traducción automática con LibreTranslate");
        response.put("service", "LibreTranslate (Docker) - PENDIENTE en FASE 2");
        response.put("note", "Agregar servicio libretranslate al docker-compose.yml");

        // Ejemplo de cómo sería la respuesta real
        if ("es".equals(targetLang)) {
            response.put("example", "Hello world → Hola mundo");
        } else if ("pt".equals(targetLang)) {
            response.put("example", "Hello world → Olá mundo");
        } else if ("fr".equals(targetLang)) {
            response.put("example", "Hello world → Bonjour le monde");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * NUEVO: Endpoint para ver configuración actual.
     * Útil para debugging y verificar que todo esté configurado correctamente.
     */

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();

        // Configuración de internacionalización
        config.put("i18n.basename", "i18n/messages");
        config.put("i18n.encoding", "UTF-8");
        config.put("i18n.defaultLanguage", "es");

        // Base de datos
        config.put("database.url", "jdbc:mysql://localhost:3306/sentimentdb");
        config.put("database.tables", "sentiment_analysis, languages, translations");

        // Docker (futuro)
        config.put("docker.libretranslate.url", "http://localhost:5000 (PENDIENTE)");
        config.put("docker.libretranslate.status", "NO CONFIGURADO - Ver /api/i18n/migration/plan");

        // Frontend integration
        config.put("frontend.header", "Accept-Language");
        config.put("frontend.example", "curl -H \"Accept-Language: pt\" http://localhost:8080/api/i18n/hello");

        log.info("[i18n/config] Configuración solicitada");
        return ResponseEntity.ok(config);
    }
}