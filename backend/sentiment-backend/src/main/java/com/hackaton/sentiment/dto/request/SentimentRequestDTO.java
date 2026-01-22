package com.hackaton.sentiment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Schema(example = "El servicio fue excelente", description = "Texto a analizar")
    @NotBlank(message = "{error.text.required}")
    @Size(min = 5, max = 500, message = "{error.text.size}")
    private String text;

    // ✅ NUEVO CAMPO: Idioma del usuario
    @Schema(
            example = "es",
            description = "Código de idioma ISO 639-1 (es, en, pt)",
            defaultValue = "es",
            allowableValues = {"es", "en", "pt"}
    )
    @Pattern(
            regexp = "^(es|en|pt)$",
            message = "El idioma debe ser: es (Español), en (Inglés) o pt (Portugués)"
    )
    private String language = "es";  // Valor por defecto: español
}