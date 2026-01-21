package com.hackaton.sentiment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO para la comunicación con la API de LibreTranslate.
 *
 * <p>Esta clase agrupa todos los objetos de transferencia de datos necesarios
 * para interactuar con el servicio de traducción LibreTranslate, incluyendo
 * solicitudes de traducción, respuestas, detección automática de idioma
 * e información de idiomas disponibles.</p>
 *
 * <p>Las clases internas están alineadas con la estructura JSON oficial
 * de la API de LibreTranslate.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public class LibreTranslateDTO {

    /**
     * DTO de solicitud para traducción de texto.
     *
     * <p>Representa el cuerpo del request enviado a LibreTranslate para traducir
     * un texto desde un idioma origen a un idioma destino.</p>
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslateRequest {

        /**
         * Texto a traducir.
         */
        @JsonProperty("q")
        private String text;

        /**
         * Idioma de origen del texto.
         *
         * <p>Puede especificarse explícitamente (por ejemplo: "en", "es") o
         * utilizar "auto" para detección automática.</p>
         */
        @JsonProperty("source")
        private String sourceLanguage;

        /**
         * Idioma destinó al cual se traducirá el texto.
         *
         * <p>Ejemplo: "en", "es", "fr".</p>
         */
        @JsonProperty("target")
        private String targetLanguage;

        /**
         * Formato del texto a traducir.
         *
         * <p>Valores admitidos por LibreTranslate:
         * <ul>
         *     <li>{@code text} – texto plano (valor por defecto)</li>
         *     <li>{@code html} – contenido HTML</li>
         * </ul>
         * </p>
         */
        @JsonProperty("format")
        private String format = "text";
    }

    /**
     * DTO de respuesta de traducción.
     *
     * <p>Contiene el texto traducido y, opcionalmente, información sobre el
     * idioma detectado automáticamente por el servicio.</p>
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslateResponse {

        /**
         * Texto resultante luego de la traducción.
         */
        @JsonProperty("translatedText")
        private String translatedText;

        /**
         * Información del idioma detectado automáticamente.
         *
         * <p>Este campo puede ser {@code null} si el idioma de origen fue
         * especificado explícitamente.</p>
         */
        @JsonProperty("detectedLanguage")
        private DetectedLanguage detectedLanguage;
    }

    /**
     * Información del idioma detectado automáticamente.
     *
     * <p>LibreTranslate devuelve este objeto cuando el idioma de origen
     * se establece como "auto".</p>
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectedLanguage {

        /**
         * Nivel de confianza de la detección automática.
         *
         * <p>Valor entre 0 y 1, donde valores cercanos a 1 indican
         * mayor certeza.</p>
         */
        @JsonProperty("confidence")
        private Double confidence;

        /**
         * Código del idioma detectado.
         *
         * <p>Ejemplo: "en", "es", "fr".</p>
         */
        @JsonProperty("language")
        private String language;
    }

    /**
     * Información de un idioma soportado por LibreTranslate.
     *
     * <p>Este DTO se utiliza para listar los idiomas disponibles y los idiomas
     * a los que puede traducirse cada uno.</p>
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LanguageInfo {

        /**
         * Código del idioma.
         *
         * <p>Ejemplo: "en", "es", "de".</p>
         */
        @JsonProperty("code")
        private String code;

        /**
         * Nombre legible del idioma.
         *
         * <p>Ejemplo: "English", "Español".</p>
         */
        @JsonProperty("name")
        private String name;

        /**
         * Lista de códigos de idiomas a los que puede traducirse.
         */
        @JsonProperty("targets")
        private String[] targets;
    }
}
