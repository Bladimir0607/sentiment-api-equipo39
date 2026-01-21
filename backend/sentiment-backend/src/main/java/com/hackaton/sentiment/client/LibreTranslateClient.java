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
 * Cliente HTTP para la comunicación con el servicio externo LibreTranslate.
 *
 * <p>Este cliente utiliza {@link WebClient} para realizar peticiones HTTP
 * reactivas hacia la API de LibreTranslate, permitiendo:</p>
 *
 * <ul>
 *   <li>Traducción automática de textos entre múltiples idiomas</li>
 *   <li>Consulta de idiomas soportados por el servicio</li>
 *   <li>Verificación de disponibilidad del servicio (health check)</li>
 *   <li>Reintentos automáticos ante fallos temporales</li>
 * </ul>
 *
 * <p>Está diseñado como un componente Spring reutilizable y tolerante a fallos,
 * con mecanismos de timeout y retry configurables.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LibreTranslateClient {

    /**
     * Cliente reactivo utilizado para realizar las peticiones HTTP.
     */
    private final WebClient webClient;

    /**
     * Propiedades de configuración para LibreTranslate
     * (timeout, número de reintentos, URL base, etc.).
     */
    private final LibreTranslateProperties properties;

    /**
     * Traduce un texto desde un idioma origen hacia un idioma destino.
     *
     * <p>Si el texto es nulo, está vacío o el idioma origen y destino son iguales,
     * el método retorna el texto original sin realizar ninguna llamada externa.</p>
     *
     * <p>En caso de error durante la comunicación con LibreTranslate,
     * se aplica un mecanismo de tolerancia a fallos devolviendo el texto original.</p>
     *
     * <h3>Ejemplo de uso:</h3>
     * <pre>{@code
     * String result = translate("Hola mundo", "es", "en");
     * // Resultado esperado: "Hello world"
     * }</pre>
     *
     * @param text texto a traducir; no debe ser {@code null} ni vacío
     * @param sourceLanguage código ISO 639-1 del idioma origen
     *                       (es, en, pt o {@code auto} para detección automática)
     * @param targetLanguage código ISO 639-1 del idioma destino (es, en, pt)
     * @return texto traducido o el texto original si ocurre un error
     */
    public String translate(String text, String sourceLanguage, String targetLanguage) {

        if (text == null || text.isBlank()) {
            log.warn("Texto vacío recibido para traducción");
            return text;
        }

        if (sourceLanguage.equals(targetLanguage)) {
            log.debug("Idiomas origen y destino iguales ({}), no se realiza traducción", sourceLanguage);
            return text;
        }

        try {
            log.info(
                    "Iniciando traducción | Origen: {} | Destino: {} | Texto: {}",
                    sourceLanguage,
                    targetLanguage,
                    text.substring(0, Math.min(50, text.length()))
            );

            LibreTranslateDTO.TranslateRequest request =
                    LibreTranslateDTO.TranslateRequest.builder()
                            .text(text)
                            .sourceLanguage(sourceLanguage)
                            .targetLanguage(targetLanguage)
                            .format("text")
                            .build();

            LibreTranslateDTO.TranslateResponse response = webClient.post()
                    .uri("/translate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LibreTranslateDTO.TranslateResponse.class)
                    .timeout(Duration.ofMillis(properties.getTimeout()))
                    .retryWhen(
                            Retry.fixedDelay(
                                    properties.getRetryCount(),
                                    Duration.ofSeconds(1)
                            )
                    )
                    .block();

            if (response != null && response.getTranslatedText() != null) {
                log.info(
                        "Traducción completada correctamente: {}",
                        response.getTranslatedText()
                                .substring(0, Math.min(50, response.getTranslatedText().length()))
                );
                return response.getTranslatedText();
            }

            log.warn("Respuesta vacía recibida desde LibreTranslate");
            return text;

        } catch (Exception e) {
            log.error(
                    "Error durante traducción | {} → {} | Detalle: {}",
                    sourceLanguage,
                    targetLanguage,
                    e.getMessage()
            );
            return text;
        }
    }

    /**
     * Obtiene la lista de idiomas soportados por el servicio LibreTranslate.
     *
     * <p>Este método puede utilizarse para validar que el servicio esté activo
     * y respondiendo correctamente.</p>
     *
     * <p>LibreTranslate soporta más de 100 idiomas, entre ellos:</p>
     * <ul>
     *   <li>Es - Español</li>
     *   <li>en - Inglés</li>
     *   <li>pt - Portugués</li>
     *   <li>fr - Francés</li>
     *   <li>de - Alemán</li>
     *   <li>zh - Chino</li>
     * </ul>
     *
     * @return lista de idiomas soportados; lista vacía si ocurre un error
     */
    public List<LibreTranslateDTO.LanguageInfo> getAvailableLanguages() {

        try {
            log.info("Consultando idiomas disponibles en LibreTranslate");

            List<LibreTranslateDTO.LanguageInfo> languages = webClient.get()
                    .uri("/languages")
                    .retrieve()
                    .bodyToFlux(LibreTranslateDTO.LanguageInfo.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (languages != null && !languages.isEmpty()) {
                log.info("Idiomas obtenidos correctamente: {}", languages.size());
            }

            return languages;

        } catch (Exception e) {
            log.error("Error obteniendo idiomas de LibreTranslate: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Verifica si el servicio LibreTranslate se encuentra disponible.
     *
     * <p>Este método se apoya en la obtención de idiomas para determinar
     * la disponibilidad del servicio.</p>
     *
     * <p>Puede utilizarse en:</p>
     * <ul>
     *   <li>Health checks del sistema</li>
     *   <li>Validaciones de contenedores Docker</li>
     *   <li>Diagnóstico de fallos de red</li>
     * </ul>
     *
     * @return {@code true} si el servicio responde correctamente;
     *         {@code false} en caso contrario
     */
    public boolean isServiceAvailable() {

        try {
            List<LibreTranslateDTO.LanguageInfo> languages = getAvailableLanguages();
            boolean available = languages != null && !languages.isEmpty();

            if (available) {
                log.info("LibreTranslate disponible ({} idiomas)", languages.size());
            } else {
                log.warn("LibreTranslate no respondió correctamente");
            }

            return available;

        } catch (Exception e) {
            log.error("LibreTranslate no disponible: {}", e.getMessage());
            return false;
        }
    }
}
