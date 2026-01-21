package com.hackaton.sentiment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuración del cliente REST utilizado para comunicarse con el
 * servicio de Machine Learning.
 *
 * <p>Esta clase define un {@link RestClient} como un Bean de Spring,
 * permitiendo que sea inyectado en otros componentes del backend.</p>
 *
 * <p>La URL base del servicio externo se obtiene desde el archivo
 * {@code application.properties} o {@code application.yml}
 * mediante la propiedad {@code ml.service.url}.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Configuration
public class RestClientConfig {

    /**
     * Crea y configura un {@link RestClient} con una URL base definida
     * en la configuración de la aplicación.
     *
     * @param mlServiceUrl URL base del servicio de Machine Learning,
     *                     inyectada desde la propiedad
     *                     {@code ml.service.url}
     * @return instancia configurada de {@link RestClient}
     */
    @Bean
    public RestClient restClient(
            @Value("${ml.service.url}") String mlServiceUrl) {

        return RestClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }
}
