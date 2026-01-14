package com.hackaton.sentiment.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentiment Analysis API")
                        .version("1.0.0")
                        .description("API para análisis de sentimiento de textos. Integra un microservicio de Machine Learning para clasificar comentarios como Positivo, Negativo o Neutro."))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"));
    }
}
