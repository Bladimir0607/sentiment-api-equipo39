package com.hackaton.sentiment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing).
 *
 * <p>Esta clase define las reglas que permiten a clientes externos
 * (por ejemplo, aplicaciones frontend) consumir los endpoints del backend
 * sin restricciones de origen.</p>
 *
 * <p>Se utiliza principalmente durante el desarrollo para facilitar
 * la comunicación entre frontend y backend alojados en distintos dominios
 * o puertos.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Configuration
public class CorsConfig {

    /**
     * Define un {@link WebMvcConfigurer} personalizado para configurar CORS.
     *
     * <p>La configuración aplicada permite:</p>
     * <ul>
     *   <li>Acceso a todas las rutas del backend ({@code /**})</li>
     *   <li>Solicitudes desde cualquier origen</li>
     *   <li>Métodos HTTP comunes (GET, POST, PUT, DELETE, OPTIONS)</li>
     *   <li>Todos los headers</li>
     * </ul>
     *
     * <p><strong>Nota:</strong> El uso de {@code "*"} como origen permitido
     * es adecuado para entornos de desarrollo. En producción se recomienda
     * restringir los orígenes a dominios específicos.</p>
     *
     * @return configurador MVC con reglas CORS personalizadas
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            /**
             * Registra las configuraciones de CORS para la aplicación.
             *
             * @param registry registro de configuraciones CORS
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
