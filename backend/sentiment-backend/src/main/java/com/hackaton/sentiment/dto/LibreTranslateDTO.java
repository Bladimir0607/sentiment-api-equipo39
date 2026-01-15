package com.hackaton.sentiment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO para comunicación con LibreTranslate API
 */
public class LibreTranslateDTO {

    /**
     * Request DTO para traducción
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslateRequest {

        @JsonProperty("q")
        private String text;

        @JsonProperty("source")
        private String sourceLanguage;

        @JsonProperty("target")
        private String targetLanguage;

        @JsonProperty("format")
        private String format = "text"; // text o html
    }

    /**
     * Response DTO de traducción
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslateResponse {

        @JsonProperty("translatedText")
        private String translatedText;

        @JsonProperty("detectedLanguage")
        private DetectedLanguage detectedLanguage;
    }

    /**
     * Idioma detectado automáticamente
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectedLanguage {

        @JsonProperty("confidence")
        private Double confidence;

        @JsonProperty("language")
        private String language;
    }

    /**
     * Información de idioma disponible
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LanguageInfo {

        @JsonProperty("code")
        private String code;

        @JsonProperty("name")
        private String name;

        @JsonProperty("targets")
        private String[] targets;
    }
}