package com.hackaton.sentiment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SentimentRequestDTO {

    @Schema(
            example = "El servicio fue excelente",
            description = "Texto a analizar"
    )
    @NotBlank(message = "{error.text.required}") // Spring buscará esta llave en messages.properties
    @Size(min = 5, max = 500, message = "{error.text.size}")
    private String text;
}
