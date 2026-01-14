package com.hackaton.sentiment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;




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


}

