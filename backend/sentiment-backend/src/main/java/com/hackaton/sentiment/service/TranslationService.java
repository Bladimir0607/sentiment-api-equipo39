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
 * Servicio de traducción con estrategia de niveles.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
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
     * Traduce una clave o texto.
     *
     * @param key clave o texto a traducir
     * @param targetLanguage idioma destino
     * @return texto traducido
     */
    @Cacheable(value = "translations", key = "#key + '_' + #targetLanguage")
    public String translate(String key, String targetLanguage) {
        if (key == null || key.isBlank()) {
            return key;
        }

        log.debug("Buscando traducción: '{}' en '{}'", key, targetLanguage);

        String translation = tryMessageSource(key, targetLanguage);
        if (translation != null && !translation.equals(key)) {
            log.debug("[PROPERTIES] Traducción encontrada");
            return translation;
        }

        translation = tryDatabase(key, targetLanguage);
        if (translation != null) {
            log.debug("[DATABASE] Traducción encontrada");
            return translation;
        }

        if (libreTranslateProperties.isEnabled()) {
            translation = tryLibreTranslate(key, targetLanguage);
            if (translation != null && !translation.equals(key)) {
                log.debug("[LIBRETRANSLATE] Traducción automática exitosa");
                saveTranslationToDatabase(key, targetLanguage, translation);
                return translation;
            }
        }

        if (!"en".equals(targetLanguage)) {
            translation = tryMessageSource(key, "en");
            if (translation != null && !translation.equals(key)) {
                log.debug("[FALLBACK] Usando traducción en inglés");
                return translation;
            }
        }

        log.warn("No se encontró traducción para '{}' en '{}'", key, targetLanguage);
        return key;
    }

    /**
     * Traduce múltiples textos en batch.
     *
     * @param texts mapa de textos a traducir
     * @param targetLanguage idioma destino
     * @return mapa con textos traducidos
     */
    public Map<String, String> translateBatch(Map<String, String> texts, String targetLanguage) {
        log.info("Traduciendo {} textos en batch a '{}'", texts.size(), targetLanguage);

        Map<String, String> translations = new HashMap<>();

        texts.forEach((key, text) -> {
            String translated = translate(text, targetLanguage);
            translations.put(key, translated);
        });

        return translations;
    }

    /**
     * Obtiene todas las traducciones para un idioma.
     *
     * @param languageCode código de idioma
     * @return mapa con todas las traducciones
     */
    public Map<String, String> getAllTranslationsForLanguage(String languageCode) {
        Map<String, String> allTranslations = new HashMap<>();

        translationRepository.findByLanguageCode(languageCode)
                .forEach(t -> allTranslations.put(t.getKey(), t.getTranslatedText()));

        log.info("Cargadas {} traducciones de base de datos para '{}'",
                allTranslations.size(), languageCode);

        return allTranslations;
    }

    /**
     * Verifica el estado del servicio de traducción.
     *
     * @return información del estado del servicio
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();

        status.put("messageSourceAvailable", messageSource != null);
        status.put("databaseAvailable", translationRepository != null);
        status.put("libreTranslateEnabled", libreTranslateProperties.isEnabled());
        status.put("libreTranslateAvailable", libreTranslateClient.isServiceAvailable());
        status.put("cacheEnabled", libreTranslateProperties.isCacheEnabled());

        long dbTranslations = translationRepository.count();
        status.put("databaseTranslationsCount", dbTranslations);

        return status;
    }

    /**
     * Intenta obtener traducción de archivos .properties.
     *
     * @param key clave de traducción
     * @param language idioma destino
     * @return traducción o null
     */
    private String tryMessageSource(String key, String language) {
        try {
            Locale locale = Locale.forLanguageTag(language);
            String result = messageSource.getMessage(key, null, locale);
            return result.equals(key) ? null : result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Intenta obtener traducción de base de datos.
     *
     * @param key clave de traducción
     * @param language idioma destino
     * @return traducción o null
     */
    private String tryDatabase(String key, String language) {
        Optional<Translation> translation =
                translationRepository.findByKeyAndLanguageCode(key, language);
        return translation.map(Translation::getTranslatedText).orElse(null);
    }

    /**
     * Traduce usando LibreTranslate.
     *
     * @param text texto a traducir
     * @param targetLanguage idioma destino
     * @return texto traducido o null
     */
    private String tryLibreTranslate(String text, String targetLanguage) {
        try {
            return libreTranslateClient.translate(text, "auto", targetLanguage);
        } catch (Exception e) {
            log.error("Error en LibreTranslate: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Guarda una traducción en la base de datos.
     *
     * @param key clave de traducción
     * @param languageCode código de idioma
     * @param translatedText texto traducido
     */
    private void saveTranslationToDatabase(String key, String languageCode, String translatedText) {
        try {
            if (translationRepository.existsByKeyAndLanguageCode(key, languageCode)) {
                return;
            }

            Translation translation = Translation.builder()
                    .key(key)
                    .languageCode(languageCode)
                    .translatedText(translatedText)
                    .module("auto")
                    .build();

            translationRepository.save(translation);
            log.debug("Traducción guardada en base de datos: {} → {}", key, languageCode);

        } catch (Exception e) {
            log.warn("No se pudo guardar traducción en base de datos: {}", e.getMessage());
        }
    }
}