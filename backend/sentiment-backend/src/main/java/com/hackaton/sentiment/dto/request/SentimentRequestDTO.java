package com.hackaton.sentiment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de solicitud para análisis de sentimiento.
 *
 * <p>Contiene el texto a analizar y el idioma en el que está escrito.
 * El sistema soporta traducción automática para garantizar que el modelo
 * de Machine Learning siempre reciba el texto en español.</p>
 *
 * <h2>Flujo de procesamiento:</h2>
 * <ol>
 *   <li><strong>Frontend</strong> envía texto en idioma seleccionado (es, en, pt)</li>
 *   <li><strong>Backend</strong> traduce a español si es necesario</li>
 *   <li><strong>Modelo ML</strong> analiza el texto en español</li>
 *   <li><strong>Backend</strong> traduce resultado al idioma original</li>
 *   <li><strong>Frontend</strong> muestra resultado en idioma del usuario</li>
 * </ol>
 *
 * <h2>Validaciones aplicadas:</h2>
 * <ul>
 *   <li><strong>text:</strong> No vacío, entre 5 y 500 caracteres</li>
 *   <li><strong>language:</strong> Debe ser 'es', 'en' o 'pt'</li>
 * </ul>
 *
 * <h2>Ejemplo de uso desde el frontend:</h2>
 * <pre>{@code
 * // JavaScript (React)
 * const response = await fetch('http://localhost:8080/sentiment', {
 *   method: 'POST',
 *   headers: { 'Content-Type': 'application/json' },
 *   body: JSON.stringify({
 *     text: "This service is amazing!",
 *     language: "en"
 *   })
 * });
 * }</pre>
 *
 * @author Equipo Hackathon Oracle ONE
 * @version 1.0
 * @since 2026-01-21
 * @see com.hackaton.sentiment.controller.SentimentController#analyze(SentimentRequestDTO)
 * @see com.hackaton.sentiment.service.SentimentService#analyzeSentiment(SentimentRequestDTO)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de análisis de sentimiento")
public class SentimentRequestDTO {

    /**
     * Texto a analizar para determinar su sentimiento.
     *
     * <p><strong>Restricciones:</strong></p>
     * <ul>
     *   <li>No puede estar vacío</li>
     *   <li>Mínimo 5 caracteres (para análisis significativo)</li>
     *   <li>Máximo 500 caracteres (limitación del modelo)</li>
     * </ul>
     *
     * <p><strong>Ejemplos válidos:</strong></p>
     * <ul>
     *   <li>"El servicio fue excelente"</li>
     *   <li>"This product is terrible"</li>
     *   <li>"O atendimento foi muito bom"</li>
     * </ul>
     *
     * <p><strong>Nota:</strong> El texto puede estar en cualquiera de los
     * idiomas soportados (es, en, pt). El sistema lo traducirá automáticamente
     * al español antes de enviarlo al modelo de ML.</p>
     *
     * @see #language
     */
    @Schema(
            example = "El servicio fue excelente",
            description = "Texto a analizar (5-500 caracteres)",
            required = true,
            minLength = 5,
            maxLength = 500
    )
    @NotBlank(message = "{error.text.required}")
    @Size(min = 5, max = 500, message = "{error.text.size}")
    private String text;

    /**
     * Código de idioma del texto enviado por el usuario.
     *
     * <p><strong>Valores permitidos:</strong></p>
     * <ul>
     *   <li><strong>es</strong> - Español (idioma del modelo ML)</li>
     *   <li><strong>en</strong> - Inglés (se traduce a español)</li>
     *   <li><strong>pt</strong> - Portugués (se traduce a español)</li>
     * </ul>
     *
     * <p><strong>Comportamiento del sistema:</strong></p>
     * <table border="1">
     *   <tr>
     *     <th>Idioma</th>
     *     <th>Acción del Backend</th>
     *   </tr>
     *   <tr>
     *     <td>es</td>
     *     <td>Envía directamente al modelo ML (sin traducción)</td>
     *   </tr>
     *   <tr>
     *     <td>en</td>
     *     <td>Traduce texto a español → Analiza → Traduce resultado a inglés</td>
     *   </tr>
     *   <tr>
     *     <td>pt</td>
     *     <td>Traduce texto a español → Analiza → Traduce resultado a portugués</td>
     *   </tr>
     * </table>
     *
     * <p><strong>Valor por defecto:</strong> "es" (Español)</p>
     *
     * @see com.hackaton.sentiment.client.LibreTranslateClient#translate(String, String, String)
     */
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
    private String language = "es";  // Por defecto español
}