package com.hackaton.sentiment.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleTranslationService {

    private final MessageSource messageSource;

    // Mapa de traducciones manuales como FALLBACK
    private final Map<String, String> manualTranslations = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("🚀 Initializing SimpleTranslationService with manual fallback");
        loadManualTranslations();
        log.info("✅ Loaded {} manual translations as fallback", manualTranslations.size());
    }

    /**
     * Carga traducciones manuales como fallback cuando Spring Messages falle
     */
    private void loadManualTranslations() {
        // ========== ESPAÑOL ==========
        manualTranslations.put("es_error.text.required", "El texto no puede estar vacío");
        manualTranslations.put("es_error.text.length", "El texto debe tener entre 5 y 500 caracteres");
        manualTranslations.put("es_sentiment.label.positive", "Positivo");
        manualTranslations.put("es_sentiment.label.negative", "Negativo");
        manualTranslations.put("es_sentiment.label.neutral", "Neutro");
        manualTranslations.put("es_sentiment.analyze.success", "Análisis de sentimiento completado");
        manualTranslations.put("es_error.internal.server", "Error interno del servidor");
        manualTranslations.put("es_sentiment.stats.total", "Total análisis");
        manualTranslations.put("es_sentiment.stats.positive", "Positivos");
        manualTranslations.put("es_sentiment.stats.negative", "Negativos");
        manualTranslations.put("es_sentiment.stats.neutral", "Neutros");

        // ========== INGLÉS ==========
        manualTranslations.put("en_error.text.required", "Text cannot be empty");
        manualTranslations.put("en_error.text.length", "Text must be between 5 and 500 characters");
        manualTranslations.put("en_sentiment.label.positive", "Positive");
        manualTranslations.put("en_sentiment.label.negative", "Negative");
        manualTranslations.put("en_sentiment.label.neutral", "Neutral");
        manualTranslations.put("en_sentiment.analyze.success", "Sentiment analysis completed");
        manualTranslations.put("en_error.internal.server", "Internal server error");
        manualTranslations.put("en_sentiment.stats.total", "Total analyses");
        manualTranslations.put("en_sentiment.stats.positive", "Positive");
        manualTranslations.put("en_sentiment.stats.negative", "Negative");
        manualTranslations.put("en_sentiment.stats.neutral", "Neutral");

        // ========== PORTUGUÉS ==========
        manualTranslations.put("pt_error.text.required", "O texto não pode estar vazio");
        manualTranslations.put("pt_error.text.length", "O texto deve ter entre 5 e 500 caracteres");
        manualTranslations.put("pt_sentiment.label.positive", "Positivo");
        manualTranslations.put("pt_sentiment.label.negative", "Negativo");
        manualTranslations.put("pt_sentiment.label.neutral", "Neutro");
        manualTranslations.put("pt_sentiment.analyze.success", "Análise de sentimento concluída");
        manualTranslations.put("pt_error.internal.server", "Erro interno do servidor");
        manualTranslations.put("pt_sentiment.stats.total", "Total de análises");
        manualTranslations.put("pt_sentiment.stats.positive", "Positivos");
        manualTranslations.put("pt_sentiment.stats.negative", "Negativos");
        manualTranslations.put("pt_sentiment.stats.neutral", "Neutros");

        // ========== MENSAGES DE VALIDACIÓN (los que usa GlobalExceptionHandler) ==========
        manualTranslations.put("es_El texto no puede estar vacío", "El texto no puede estar vacío");
        manualTranslations.put("en_El texto no puede estar vacío", "Text cannot be empty");
        manualTranslations.put("pt_El texto no puede estar vacío", "O texto não pode estar vazio");

        manualTranslations.put("es_El texto debe tener entre 5 y 500 caracteres", "El texto debe tener entre 5 y 500 caracteres");
        manualTranslations.put("en_El texto debe tener entre 5 y 500 caracteres", "Text must be between 5 and 500 characters");
        manualTranslations.put("pt_El texto debe tener entre 5 y 500 caracteres", "O texto deve ter entre 5 e 500 caracteres");
    }

    /**
     * Traduce una clave usando Spring MessageSource PRIMERO,
     * si falla, usa traducciones manuales.
     *
     * IMPORTANTE: Este método es usado por GlobalExceptionHandler
     * y I18nTestController, así que debe mantener compatibilidad.
     */
    public String translate(String key, String languageCode) {
        // PRIMERO: Intentar con Spring MessageSource
        try {
            Locale locale = Locale.forLanguageTag(languageCode);
            String springResult = messageSource.getMessage(key, null, locale);

            // Verificar si Spring encontró la traducción
            // Si devuelve la misma clave, significa que NO la encontró
            if (!springResult.equals(key)) {
                log.debug("✅ Spring found: '{}' -> '{}' for language '{}'",
                        key, springResult, languageCode);
                return springResult;
            }
        } catch (Exception e) {
            // Spring no pudo traducir, continuar con fallback
            log.debug("⚠️ Spring MessageSource failed for '{}' in '{}': {}",
                    key, languageCode, e.getMessage());
        }

        // SEGUNDO: Buscar en traducciones manuales
        String manualKey = languageCode + "_" + key;
        if (manualTranslations.containsKey(manualKey)) {
            log.debug("🔧 Using manual translation for '{}' in '{}'", key, languageCode);
            return manualTranslations.get(manualKey);
        }

        // CASO ESPECIAL: GlobalExceptionHandler pasa mensajes completos en español
        // como "El texto no puede estar vacío". Necesitamos traducir esos también.
        // Intentar encontrar el mensaje completo en el mapa
        for (Map.Entry<String, String> entry : manualTranslations.entrySet()) {
            if (entry.getKey().startsWith(languageCode + "_" + key)) {
                return entry.getValue();
            }
        }

        // TERCERO: Si el mensaje ES una oración completa (como los de validación),
        // intentar encontrarlo directamente
        String sentenceKey = languageCode + "_" + key;
        if (manualTranslations.containsKey(sentenceKey)) {
            return manualTranslations.get(sentenceKey);
        }

        // CUARTO: Fallback a inglés si no es inglés
        if (!"en".equals(languageCode)) {
            String englishKey = "en_" + key;
            if (manualTranslations.containsKey(englishKey)) {
                log.debug("🌐 Fallback to English for '{}'", key);
                return manualTranslations.get(englishKey);
            }
        }

        // ÚLTIMO: Devolver la clave original (compatibilidad con código existente)
        log.warn("❌ No translation found for '{}' in language '{}'", key, languageCode);
        return key;
    }

    /**
     * Traduce con parámetros - mantiene compatibilidad
     */
    public String translate(String key, String languageCode, Object... params) {
        try {
            // Primero obtener la traducción base
            String baseTranslation = translate(key, languageCode);

            // Si no se encontró traducción, devolver la clave
            if (baseTranslation.equals(key)) {
                return key;
            }

            // Aplicar parámetros si los hay
            if (params != null && params.length > 0) {
                return String.format(baseTranslation, params);
            }

            return baseTranslation;
        } catch (Exception e) {
            log.error("Error formatting translation for '{}' with params: {}", key, e.getMessage());
            return key;
        }
    }

    /**
     * NUEVO: Método para verificar si el servicio está funcionando correctamente
     * Útil para debugging
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();

        status.put("service", "SimpleTranslationService");
        status.put("manualTranslationsCount", manualTranslations.size());
        status.put("messageSourceAvailable", messageSource != null);

        // Probar algunas traducciones
        Map<String, String> testResults = new HashMap<>();
        testResults.put("es_error.text.required", translate("error.text.required", "es"));
        testResults.put("en_error.text.required", translate("error.text.required", "en"));
        testResults.put("pt_error.text.required", translate("error.text.required", "pt"));

        status.put("testTranslations", testResults);
        status.put("isWorking", !testResults.get("es_error.text.required").equals("error.text.required"));

        return status;
    }

    /**
     * NUEVO: Obtener todas las traducciones para un idioma
     * Útil para frontend
     */
    public Map<String, String> getAllTranslationsForLanguage(String languageCode) {
        Map<String, String> result = new HashMap<>();

        manualTranslations.forEach((key, value) -> {
            if (key.startsWith(languageCode + "_")) {
                // Extraer la clave real (sin prefijo de idioma)
                String realKey = key.substring(languageCode.length() + 1);
                result.put(realKey, value);
            }
        });

        return result;
    }
}