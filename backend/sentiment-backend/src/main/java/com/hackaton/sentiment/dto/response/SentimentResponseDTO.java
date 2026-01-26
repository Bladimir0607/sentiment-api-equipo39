package com.hackaton.sentiment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resultado del análisis de sentimiento")
public class SentimentResponseDTO {

    @Schema(example = "Positivo", description = "Clasificación del sentimiento")
    @JsonProperty("prediction")  // Este es el nombre que enviaremos al frontend
    private String prediction;

    @Schema(example = "0.87", description = "Probabilidad de la predicción (0.0-1.0)")
    @JsonProperty("probability")
    private Double probability;

    // ✅ NUEVO: Keywords traducidas al idioma del usuario
    @Schema(example = "[\"excelente\", \"bueno\"]", description = "Palabras clave identificadas")
    @JsonProperty("keywords")
    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    // ✅ OPCIONAL: Para debugging - keywords en español
    @JsonProperty("keywords_es")
    @Schema(hidden = true)
    @Builder.Default
    private List<String> keywordsEs = new ArrayList<>();

    // ✅ OPCIONAL: Texto original y traducido para transparencia
    @JsonProperty("original_text")
    private String originalText;

    @JsonProperty("translated_text")
    private String translatedText;

    @JsonProperty("language")
    private String language;

    // Constructor para mantener compatibilidad
    public SentimentResponseDTO(String prediction, Double probability) {
        this.prediction = prediction;
        this.probability = probability;
    }
}