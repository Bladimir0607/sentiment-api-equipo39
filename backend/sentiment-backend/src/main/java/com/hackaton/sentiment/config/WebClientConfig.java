package com.hackaton.sentiment.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Clase de configuración responsable de definir el {@link WebClient}
 * utilizado para la comunicación con el servicio externo LibreTranslate.
 *
 * <p>Este cliente HTTP reactivo permite realizar peticiones REST no bloqueantes,
 * centralizando la URL base y los encabezados comunes requeridos para el
 * intercambio de información en formato JSON.</p>
 *
 * <p>La configuración se apoya en {@link LibreTranslateProperties} para obtener
 * los valores externos definidos en {@code application.yml}, facilitando
 * la flexibilidad entre entornos (local, Docker, producción).</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    /**
     * Propiedades de configuración de LibreTranslate.
     * Contiene la URL base y parámetros relacionados al servicio.
     */
    private final LibreTranslateProperties properties;

    /**
     * Crea y expone un {@link WebClient} configurado para LibreTranslate.
     *
     * <p>El cliente se inicializa con:</p>
     * <ul>
     *   <li>URL base del servicio LibreTranslate</li>
     *   <li>Encabezado {@code Content-Type} en formato JSON</li>
     *   <li>Encabezado {@code Accept} en formato JSON</li>
     * </ul>
     *
     * @return instancia configurada de {@link WebClient}
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
