package com.hackaton.sentiment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sentimentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentiment Analysis API")
                        .description("""
                                API para análisis de sentimiento de textos.
                                Integra un microservcio de Machine Learning
                                para clasificar comentarios como Positivo,
                                Negativo o Neutro.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                        .name("Hackathon")
                        )
                );
    }
}
