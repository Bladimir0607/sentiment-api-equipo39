package com.hackaton.sentiment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO de respuesta para análisis de sentimiento.
 *
 * <p>Representa el resultado completo del análisis realizado por el sistema,
 * incluyendo la clasificación del sentimiento, la probabilidad asociada
 * y los textos original y traducido para garantizar transparencia.</p>
 *
 * <h2>Información devuelta:</h2>
 * <ul>
 *   <li><strong>Prediction:</strong> Clasificación del sentimiento (en el idioma del usuario)</li>
 *   <li><strong>Probability:</strong> Nivel de confianza del modelo (0.0 - 1.0)</li>
 *   <li><strong>originalText:</strong> Texto enviado originalmente por el usuario</li>
 *   <li><strong>translatedText:</strong> Texto traducido al español y analizado por el modelo</li>
 *   <li><strong>Language:</strong> Idioma seleccionado por el usuario</li>
 * </ul>
 *
 * <h2>Ejemplo de respuesta (usuario en inglés):</h2>
 * <pre>{@code
 * {
 *   "prediction": "Positive",
 *   "probability": 0.94,
 *   "originalText": "This service is amazing!",
 *   "translatedText": "¡Este servicio es increíble!",
 *   "language": "en"
 * }
 * }</pre>
 *
 * <h2>Interpretación de la probabilidad:</h2>
 * <table border="1">
 *   <tr>
 *     <th>Rango</th>
 *     <th>Interpretación</th>
 *     <th>Acción recomendada</th>
 *   </tr>
 *   <tr>
 *     <td>0.90 - 1.00</td>
 *     <td>Alta confianza</td>
 *     <td>Resultado muy confiable</td>
 *   </tr>
 *   <tr>
 *     <td>0.70 - 0.89</td>
 *     <td>Confianza moderada</td>
 *     <td>Resultado confiable</td>
 *   </tr>
 *   <tr>
 *     <td>0.50 - 0.69</td>
 *     <td>Baja confianza</td>
 *     <td>Validar manualmente si es crítico</td>
 *   </tr>
 * </table>
 *
 * @author Equipo Hackathon Oracle ONE
 * @version 1.0
 * @since 2026-01-21
 * @see com.hackaton.sentiment.dto.request.SentimentRequestDTO
 * @see com.hackaton.sentiment.service.SentimentService
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resultado del análisis de sentimiento")
public class SentimentResponseDTO {

    /**
     * Clasificación del sentimiento detectado.
     *
     * <p>El valor se devuelve <strong>en el idioma del usuario</strong>,
     * aunque el modelo de Machine Learning siempre predice inicialmente
     * en español.</p>
     *
     * <p><strong>Valores comunes:</strong></p>
     * <ul>
     *   <li>Español: {@code Positivo}, {@code Negativo}</li>
     *   <li>Inglés: {@code Positive}, {@code Negative}</li>
     *   <li>Portugués: {@code Positivo}, {@code Negativo}</li>
     * </ul>
     *
     * <p>La traducción del resultado se realiza en la capa de servicio
     * antes de enviar la respuesta al cliente.</p>
     */
    @Schema(
            description = "Clasificación del sentimiento en el idioma del usuario",
            example = "Positive",
            allowableValues = {"Positivo", "Negativo", "Positive", "Negative"}
    )
    @JsonProperty("prediction")
    private String prediction;

    /**
     * Probabilidad o nivel de confianza del modelo.
     *
     * <p>Indica qué tan seguro está el modelo respecto a la clasificación
     * devuelta.</p>
     *
     * <ul>
     *   <li><strong>1.0:</strong> Confianza absoluta</li>
     *   <li><strong>0.8:</strong> Alta confianza</li>
     *   <li><strong>0.6:</strong> Confianza moderada</li>
     *   <li><strong>&lt; 0.6:</strong> Resultado poco confiable</li>
     * </ul>
     *
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>{@code
     * if (probability >= 0.8) {
     *     // Aceptar resultado automáticamente
     * } else {
     *     // Revisión manual recomendada
     * }
     * }</pre>
     */
    @Schema(
            description = "Probabilidad de la predicción (rango 0.0 - 1.0)",
            example = "0.87",
            minimum = "0.0",
            maximum = "1.0"
    )
    @JsonProperty("probability")
    private Double probability;

    /**
     * Texto original enviado por el usuario.
     *
     * <p>Se incluye para fines de trazabilidad, auditoría
     * y validación del análisis realizado.</p>
     *
     * <p>Puede estar en cualquiera de los idiomas soportados por el sistema.</p>
     */
    @Schema(
            description = "Texto original enviado por el usuario",
            example = "This service is amazing!"
    )
    @JsonProperty("originalText")
    private String originalText;

    /**
     * Texto traducido al español que fue procesado por el modelo ML.
     *
     * <p>El modelo de Machine Learning trabaja exclusivamente con texto
     * en español, por lo que cualquier texto en otro idioma es traducido
     * previamente.</p>
     *
     * <p><strong>Regla:</strong></p>
     * <ul>
     *   <li>Si el idioma es español, coincide con {@code originalText}</li>
     *   <li>Si el idioma es distinto, contiene la traducción al español</li>
     * </ul>
     */
    @Schema(
            description = "Texto en español utilizado por el modelo ML",
            example = "¡Este servicio es increíble!"
    )
    @JsonProperty("translatedText")
    private String translatedText;

    /**
     * Código de idioma del usuario según el estándar ISO 639-1.
     *
     * <p>Este valor determina en qué idioma se devuelven
     * las etiquetas y mensajes al frontend.</p>
     *
     * <p><strong>Idiomas soportados:</strong></p>
     * <ul>
     *   <li>{@code es} - Español</li>
     *   <li>{@code en} - Inglés</li>
     *   <li>{@code pt} - Portugués</li>
     * </ul>
     */
    @Schema(
            description = "Código de idioma del usuario",
            example = "en",
            allowableValues = {"es", "en", "pt"}
    )
    @JsonProperty("language")
    private String language;
}
