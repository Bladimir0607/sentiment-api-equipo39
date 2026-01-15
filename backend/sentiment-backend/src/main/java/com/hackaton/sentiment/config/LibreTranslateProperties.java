package com.hackaton.sentiment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuración para LibreTranslate.
 * Lee los valores desde application.yml bajo el prefijo 'libretranslate'
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "libretranslate")
public class LibreTranslateProperties {

    /**
     * URL del servicio LibreTranslate (Docker o externo)
     * Por defecto: http://localhost:5000
     */
    private String url = "http://localhost:5000";

    /**
     * Habilitar o deshabilitar el servicio de traducción automática
     * Si está en false (falso), solo usará properties y base de datos
     */
    private boolean enabled = true;

    /**
     * Habilitar caché de traducciones en Redis
     * Mejora el rendimiento evitando llamadas repetidas
     */
    private boolean cacheEnabled = true;

    /**
     * Timeout en milisegundos para las peticiones HTTP
     * LibreTranslate puede tardar unos segundos en traducir
     */
    private int timeout = 10000;

    /**
     * Número de reintentos si falla una traducción
     */
    private int retryCount = 2;
}