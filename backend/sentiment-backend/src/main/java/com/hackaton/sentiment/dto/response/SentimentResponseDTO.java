package com.hackaton.sentiment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentimentResponseDTO {

    @Schema(example = "Positivo")
    @JsonProperty("prevision")  // ← Cambiado a "prevision"
    private String prediction;

    @Schema(example = "0.87")
    @JsonProperty("probabilidad")
    private Double probability;

    // NUEVO CAMPO: Para recibir las palabras clave
    @Schema(example = "[\"excelente\", \"bueno\", \"rápido\"]")
    @JsonProperty("palabras_clave")  // ← Mapea con "palabras_clave" de FastAPI
    private List<String> keyWords;

    // Constructor para mantener compatibilidad
    public SentimentResponseDTO(String prediction, Double probability) {
        this.prediction = prediction;
        this.probability = probability;
    }
}

