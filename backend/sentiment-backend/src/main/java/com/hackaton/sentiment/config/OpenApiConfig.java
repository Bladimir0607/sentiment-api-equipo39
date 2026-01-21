package com.hackaton.sentiment.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global de OpenAPI (Swagger) para la API de análisis de sentimiento.
 *
 * <p>Esta clase define:</p>
 * <ul>
 *   <li>La información general de la API (título, versión y descripción)</li>
 *   <li>El esquema de seguridad basado en JWT (Bearer Token)</li>
 *   <li>La aplicación global del requisito de autenticación</li>
 * </ul>
 *
 * <p>La configuración permite que Swagger UI muestre correctamente
 * el botón <strong>Authorize</strong> para enviar tokens JWT en las
 * solicitudes protegidas.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 * @see OpenAPI
 * @see SecurityScheme
 */
@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    /**
     * Configura la definición principal de OpenAPI para la aplicación.
     *
     * <p>Incluye información básica de la API como:</p>
     * <ul>
     *   <li>Título</li>
     *   <li>Versión</li>
     *   <li>Descripción funcional</li>
     * </ul>
     *
     * <p>Además, establece el esquema de seguridad JWT como requisito
     * global para los endpoints protegidos.</p>
     *
     * @return instancia personalizada de {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentiment Analysis API")
                        .version("1.0.0")
                        .description(
                                "API para análisis de sentimiento de textos. " +
                                        "Integra un microservicio de Machine Learning para " +
                                        "clasificar comentarios como Positivo o Negativo."
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("Bearer Authentication")
                );
    }
}
