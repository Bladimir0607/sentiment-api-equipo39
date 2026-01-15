package com.hackaton.sentiment.client;

import com.hackaton.sentiment.config.LibreTranslateProperties;
import com.hackaton.sentiment.dto.LibreTranslateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * Cliente HTTP para comunicación con el servicio LibreTranslate.
 * Usa WebClient (reactivo) para peticiones asíncronas.
 *
 * Este cliente se encarga de:
 * - Traducir textos entre idiomas usando IA
 * - Obtener la lista de idiomas disponibles
 * - Verificar la disponibilidad del servicio
 * - Reintentar automáticamente si falla una petición
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LibreTranslateClient {

    private final WebClient webClient;
    private final LibreTranslateProperties properties;

    /**
     * Traduce un texto de un idioma origen a un idioma destino.
     *
     * Ejemplo:
     *   translate("Hola mundo", "es", "en") → "Hello world"
     *
     * @param text Texto a traducir (no puede ser null o vacío)
     * @param sourceLanguage Código de idioma origen (es, en, pt, auto)
     *                       Usa "auto" para detectar automáticamente
     * @param targetLanguage Código de idioma destino (es, en, pt)
     * @return Texto traducido, o el texto original si falla
     */
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        // Validar que el texto no esté vacío
        if (text == null || text.isBlank()) {
            log.warn("⚠️ Texto vacío para traducir");
            return text;
        }

        // Si el idioma origen y destino son iguales, no traducir
        if (sourceLanguage.equals(targetLanguage)) {
            log.debug("✅ Idiomas iguales ({}), no se traduce", sourceLanguage);
            return text;
        }

        try {
            // Log de inicio de traducción (solo primeros 50 caracteres)
            log.info("🌐 Traduciendo: '{}' | {} → {}",
                    text.substring(0, Math.min(50, text.length())),
                    sourceLanguage,
                    targetLanguage);

            // Construir el request para LibreTranslate
            LibreTranslateDTO.TranslateRequest request = LibreTranslateDTO.TranslateRequest.builder()
                    .text(text)
                    .sourceLanguage(sourceLanguage)
                    .targetLanguage(targetLanguage)
                    .format("text")
                    .build();

            // Hacer la petición POST a /translate
            LibreTranslateDTO.TranslateResponse response = webClient.post()
                    .uri("/translate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LibreTranslateDTO.TranslateResponse.class)
                    .timeout(Duration.ofMillis(properties.getTimeout()))
                    .retryWhen(Retry.fixedDelay(properties.getRetryCount(), Duration.ofSeconds(1)))
                    .block(); // Bloqueamos para API síncrona

            // Verificar que la respuesta tenga contenido
            if (response != null && response.getTranslatedText() != null) {
                log.info("✅ Traducción exitosa: '{}'",
                        response.getTranslatedText().substring(0, Math.min(50, response.getTranslatedText().length())));
                return response.getTranslatedText();
            }

            // Si la respuesta está vacía
            log.warn("⚠️ Respuesta de LibreTranslate vacía");
            return text;

        } catch (Exception e) {
            // Si falla, devolver el texto original y loguear el error
            log.error("❌ Error traduciendo con LibreTranslate: {} → {} | Error: {}",
                    sourceLanguage, targetLanguage, e.getMessage());
            return text; // Fallback: devolver texto original
        }
    }

    /**
     * Obtiene la lista de idiomas disponibles en LibreTranslate.
     * Útil para verificar que el servicio está funcionando.
     *
     * LibreTranslate soporta más de 100 idiomas:
     * - es (Español)
     * - en (Inglés)
     * - pt (Portugués)
     * - fr (Francés)
     * - de (Alemán)
     * - ja (Japonés)
     * - zh (Chino)
     * ... y muchos más
     *
     * @return Lista de idiomas soportados con sus códigos y nombres
     */
    public List<LibreTranslateDTO.LanguageInfo> getAvailableLanguages() {
        try {
            log.info("📋 Obteniendo idiomas disponibles de LibreTranslate");

            // Hacer petición GET a /languages
            List<LibreTranslateDTO.LanguageInfo> languages = webClient.get()
                    .uri("/languages")
                    .retrieve()
                    .bodyToFlux(LibreTranslateDTO.LanguageInfo.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (languages != null && !languages.isEmpty()) {
                log.info("✅ Obtenidos {} idiomas de LibreTranslate", languages.size());
            }

            return languages;

        } catch (Exception e) {
            log.error("❌ Error obteniendo idiomas de LibreTranslate: {}", e.getMessage());
            return List.of(); // Devolver lista vacía si falla
        }
    }

    /**
     * Verifica si el servicio LibreTranslate está disponible y funcionando.
     *
     * Esto es útil para:
     * - Health checks del sistema
     * - Verificar que Docker esté corriendo
     * - Debugging de problemas de conexión
     *
     * @return true si el servicio responde correctamente, false en caso contrario
     */
    public boolean isServiceAvailable() {
        try {
            // Intentar obtener la lista de idiomas
            List<LibreTranslateDTO.LanguageInfo> languages = getAvailableLanguages();
            boolean available = languages != null && !languages.isEmpty();

            if (available) {
                log.info("✅ LibreTranslate disponible - {} idiomas soportados", languages.size());
            } else {
                log.warn("⚠️ LibreTranslate no respondió correctamente");
            }

            return available;

        } catch (Exception e) {
            log.error("❌ LibreTranslate no disponible: {}", e.getMessage());
            return false;
        }
    }
}