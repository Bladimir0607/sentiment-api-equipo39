package com.hackaton.sentiment.service;

import com.hackaton.sentiment.client.LibreTranslateClient;
import com.hackaton.sentiment.config.LibreTranslateProperties;
import com.hackaton.sentiment.entity.Translation;
import com.hackaton.sentiment.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio COMPLETO de traducción con estrategia de 4 niveles.
 *
 * ESTRATEGIA DE TRADUCCIÓN (en orden de prioridad):
 *
 * 1️⃣ ARCHIVOS .PROPERTIES (Más rápido - textos estáticos)
 *    - Busca en: messages_es.properties, messages_en.properties, etc.
 *    - Ventaja: Traducción instantánea sin consultas externas
 *    - Uso: Textos fijos de la aplicación (botones, labels, errores)
 *
 * 2️⃣ BASE DE DATOS (Traducciones dinámicas persistentes)
 *    - Busca en: tabla "translations"
 *    - Ventaja: Traducciones personalizadas y editables
 *    - Uso: Contenido dinámico o traducciones generadas automáticamente
 *
 * 3️⃣ LIBRETRANSLATE (Traducción automática con IA)
 *    - Traduce con: Servicio LibreTranslate en Docker
 *    - Ventaja: Traduce CUALQUIER texto en tiempo real (100+ idiomas)
 *    - Uso: Textos nuevos que no están en .properties ni DB
 *    - Bonus: Guarda automáticamente en DB para futuros usos
 *
 * 4️⃣ FALLBACK (Última opción)
 *    - Si nada funciona: Intenta inglés o devuelve la clave original
 *
 * CACHÉ CON REDIS:
 * - Todas las traducciones se cachean automáticamente con @Cacheable
 * - TTL: 1 hora (configurable en CacheConfig)
 * - Mejora el rendimiento evitando llamadas repetidas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final MessageSource messageSource;
    private final TranslationRepository translationRepository;
    private final LibreTranslateClient libreTranslateClient;
    private final LibreTranslateProperties libreTranslateProperties;

    /**
     * Traduce una clave o texto siguiendo la estrategia de 4 niveles.
     * Usa @Cacheable para evitar traducciones repetidas (Redis).
     *
     * FLUJO DE EJECUCIÓN:
     * 1. Busca en .properties
     * 2. Si no encuentra, busca en base de datos
     * 3. Si no encuentra, traduce con LibreTranslate (IA)
     * 4. Si falla todo, intenta inglés o devuelve la clave
     *
     * EJEMPLOS DE USO:
     *
     * Caso 1: Texto estático (existe en .properties)
     *   translate("error.text.required", "es")
     *   → "El texto no puede estar vacío"
     *
     * Caso 2: Texto dinámico (existe en DB)
     *   translate("welcome.message", "pt")
     *   → "Bem-vindo" (desde base de datos)
     *
     * Caso 3: Texto nuevo (traduce con IA)
     *   translate("Esta es una frase nueva", "en")
     *   → "This is a new sentence" (LibreTranslate + guarda en DB)
     *
     * @param key Clave de traducción o texto a traducir
     * @param targetLanguage Idioma destino (es, en, pt)
     * @return Texto traducido
     */
    @Cacheable(value = "translations", key = "#key + '_' + #targetLanguage")
    public String translate(String key, String targetLanguage) {
        // Validar entrada
        if (key == null || key.isBlank()) {
            return key;
        }

        log.debug("🔍 Buscando traducción: '{}' en '{}'", key, targetLanguage);

        // ============================================================
        // NIVEL 1: Spring MessageSource (archivos .properties)
        // ============================================================
        String translation = tryMessageSource(key, targetLanguage);
        if (translation != null && !translation.equals(key)) {
            log.debug("✅ [PROPERTIES] Traducción encontrada");
            return translation;
        }

        // ============================================================
        // NIVEL 2: Base de datos (tabla translations)
        // ============================================================
        translation = tryDatabase(key, targetLanguage);
        if (translation != null) {
            log.debug("✅ [DATABASE] Traducción encontrada");
            return translation;
        }

        // ============================================================
        // NIVEL 3: LibreTranslate (si está habilitado)
        // ============================================================
        if (libreTranslateProperties.isEnabled()) {
            translation = tryLibreTranslate(key, targetLanguage);
            if (translation != null && !translation.equals(key)) {
                log.debug("✅ [LIBRETRANSLATE] Traducción automática exitosa");

                // Guardar en base de datos para futuros usos
                saveTranslationToDatabase(key, targetLanguage, translation);

                return translation;
            }
        }

        // ============================================================
        // NIVEL 4: Fallback a inglés
        // ============================================================
        if (!"en".equals(targetLanguage)) {
            translation = tryMessageSource(key, "en");
            if (translation != null && !translation.equals(key)) {
                log.debug("🌐 [FALLBACK] Usando traducción en inglés");
                return translation;
            }
        }

        // ============================================================
        // ÚLTIMO RECURSO: Devolver la clave original
        // ============================================================
        log.warn("⚠️ No se encontró traducción para '{}' en '{}'", key, targetLanguage);
        return key;
    }

    /**
     * Traduce múltiples textos en batch.
     * Útil para traducir toda la interfaz del frontend de una vez.
     *
     * EJEMPLO DE USO:
     * Map<String, String> textos = Map.of(
     *     "button.analyze", "Analizar",
     *     "button.clear", "Limpiar"
     * );
     * translateBatch(textos, "en")
     * → {"button.analyze": "Analyze", "button.clear": "Clear"}
     *
     * @param texts Mapa de clave → texto a traducir
     * @param targetLanguage Idioma destino
     * @return Mapa de clave → texto traducido
     */
    public Map<String, String> translateBatch(Map<String, String> texts, String targetLanguage) {
        log.info("📦 Traduciendo {} textos en batch a '{}'", texts.size(), targetLanguage);

        Map<String, String> translations = new HashMap<>();

        texts.forEach((key, text) -> {
            String translated = translate(text, targetLanguage);
            translations.put(key, translated);
        });

        return translations;
    }

    /**
     * Obtiene TODAS las traducciones para un idioma.
     * El frontend puede cargar esto al inicio para tener todos los textos.
     *
     * EJEMPLO DE USO:
     * getAllTranslationsForLanguage("es")
     * → {
     *     "error.text.required": "El texto no puede estar vacío",
     *     "button.analyze": "Analizar",
     *     ...
     *   }
     *
     * @param languageCode Código de idioma (es, en, pt)
     * @return Mapa con todas las traducciones
     */
    public Map<String, String> getAllTranslationsForLanguage(String languageCode) {
        Map<String, String> allTranslations = new HashMap<>();

        // Cargar traducciones de base de datos
        translationRepository.findByLanguageCode(languageCode)
                .forEach(t -> allTranslations.put(t.getKey(), t.getTranslatedText()));

        log.info("📚 Cargadas {} traducciones de base de datos para '{}'",
                allTranslations.size(), languageCode);

        return allTranslations;
    }

    /**
     * Verifica el estado del servicio de traducción.
     * Útil para endpoints de health check y debugging.
     *
     * RESPUESTA EJEMPLO:
     * {
     *   "messageSourceAvailable": true,
     *   "databaseAvailable": true,
     *   "libreTranslateEnabled": true,
     *   "libreTranslateAvailable": true,
     *   "cacheEnabled": true,
     *   "databaseTranslationsCount": 45
     * }
     *
     * @return Información del estado de cada nivel
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();

        status.put("messageSourceAvailable", messageSource != null);
        status.put("databaseAvailable", translationRepository != null);
        status.put("libreTranslateEnabled", libreTranslateProperties.isEnabled());
        status.put("libreTranslateAvailable", libreTranslateClient.isServiceAvailable());
        status.put("cacheEnabled", libreTranslateProperties.isCacheEnabled());

        // Contar traducciones en base de datos
        long dbTranslations = translationRepository.count();
        status.put("databaseTranslationsCount", dbTranslations);

        return status;
    }

    // ========== MÉTODOS PRIVADOS DE AYUDA ==========

    /**
     * NIVEL 1: Intenta obtener traducción de archivos .properties
     * Busca en: messages_es.properties, messages_en.properties, etc.
     */
    private String tryMessageSource(String key, String language) {
        try {
            Locale locale = Locale.forLanguageTag(language);
            String result = messageSource.getMessage(key, null, locale);
            // Si devuelve la misma clave, significa que no la encontró
            return result.equals(key) ? null : result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * NIVEL 2: Intenta obtener traducción de base de datos
     * Busca en: tabla "translations"
     */
    private String tryDatabase(String key, String language) {
        Optional<Translation> translation =
                translationRepository.findByKeyAndLanguageCode(key, language);
        return translation.map(Translation::getTranslatedText).orElse(null);
    }

    /**
     * NIVEL 3: Traduce usando LibreTranslate (IA)
     * Detecta automáticamente el idioma origen con "auto"
     */
    private String tryLibreTranslate(String text, String targetLanguage) {
        try {
            // Detectar idioma automáticamente (auto)
            return libreTranslateClient.translate(text, "auto", targetLanguage);
        } catch (Exception e) {
            log.error("❌ Error en LibreTranslate: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Guarda una traducción en la base de datos para futuros usos.
     * Esto permite que las traducciones automáticas se reutilicen.
     */
    private void saveTranslationToDatabase(String key, String languageCode, String translatedText) {
        try {
            // Verificar si ya existe para no duplicar
            if (translationRepository.existsByKeyAndLanguageCode(key, languageCode)) {
                return;
            }

            Translation translation = Translation.builder()
                    .key(key)
                    .languageCode(languageCode)
                    .translatedText(translatedText)
                    .module("auto") // Marcamos como traducción automática
                    .build();

            translationRepository.save(translation);
            log.debug("💾 Traducción guardada en base de datos: {} → {}", key, languageCode);

        } catch (Exception e) {
            log.warn("⚠️ No se pudo guardar traducción en base de datos: {}", e.getMessage());
        }
    }
}