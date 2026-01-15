package com.hackaton.sentiment.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración de WebClient para comunicación con LibreTranslate.
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final LibreTranslateProperties properties;

    /**
     * Bean de WebClient configurado para LibreTranslate.
     * Incluye headers y configuración base.
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