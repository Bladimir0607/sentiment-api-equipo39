package com.hackaton.sentiment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO de respuesta para estadísticas de análisis de sentimiento.
 *
 * <p>Proporciona métricas agregadas sobre todos los análisis realizados
 * en el sistema, incluyendo conteos absolutos y porcentajes.</p>
 *
 * <h2>Modelo de clasificación:</h2>
 * <p>El sistema utiliza clasificación <strong>BINARIA</strong>:</p>
 * <ul>
 *   <li><strong>Positivo:</strong> Sentimientos favorables, elogios, satisfacción</li>
 *   <li><strong>Negativo:</strong> Quejas, críticas, insatisfacción</li>
 * </ul>
 *
 * <h2>Ejemplo de respuesta JSON:</h2>
 * <pre>{@code
 * {
 *   "total": 150,
 *   "positive": 95,
 *   "negative": 55,
 *   "positivePercentage": 63.33,
 *   "negativePercentage": 36.67
 * }
 * }</pre>
 *
 * <h2>Casos de uso:</h2>
 * <ul>
 *   <li>Dashboard de métricas de satisfacción del cliente</li>
 *   <li>Reportes de análisis de sentimiento a lo largo del tiempo</li>
 *   <li>Indicadores KPI para equipos de atención al cliente</li>
 *   <li>Análisis de campañas de marketing</li>
 * </ul>
 *
 * @author Equipo Hackathon Oracle ONE
 * @version 1.0
 * @since 2026-01-21
 * @see com.hackaton.sentiment.service.SentimentService#getStats()
 */
@Getter
@Builder
@Schema(description = "Estadísticas agregadas de análisis de sentimiento")
public class SentimentStatsResponseDTO {

    /**
     * Número total de análisis realizados en el sistema.
     *
     * <p>Incluye todos los análisis históricos almacenados en la
     * base de datos, independientemente de su clasificación.</p>
     *
     * @return Total de análisis realizados
     */
    @Schema(
            description = "Número total de análisis realizados",
            example = "150",
            required = true
    )
    private Long total;

    /**
     * Cantidad de análisis clasificados como positivos.
     *
     * <p>Representa textos con sentimiento favorable, como:</p>
     * <ul>
     *   <li>"El servicio fue excelente"</li>
     *   <li>"Muy satisfecho con la atención"</li>
     *   <li>"Producto de alta calidad"</li>
     * </ul>
     *
     * @return Conteo de sentimientos positivos
     */
    @Schema(
            description = "Cantidad de análisis con sentimiento positivo",
            example = "95",
            required = true
    )
    private Long positive;

    /**
     * Cantidad de análisis clasificados como negativos.
     *
     * <p>Representa textos con sentimiento desfavorable, como:</p>
     * <ul>
     *   <li>"Muy decepcionado con el servicio"</li>
     *   <li>"Producto de mala calidad"</li>
     *   <li>"Atención al cliente deficiente"</li>
     * </ul>
     *
     * @return Conteo de sentimientos negativos
     */
    @Schema(
            description = "Cantidad de análisis con sentimiento negativo",
            example = "55",
            required = true
    )
    private Long negative;

    /**
     * Calcula el porcentaje de sentimientos positivos sobre el total.
     *
     * <p>Útil para determinar la tasa de satisfacción general:</p>
     * <ul>
     *   <li><strong>&gt;80%:</strong> Excelente satisfacción</li>
     *   <li><strong>60-80%:</strong> Buena satisfacción</li>
     *   <li><strong>40-60%:</strong> Satisfacción moderada</li>
     *   <li><strong>&lt;40%:</strong> Baja satisfacción - requiere atención</li>
     * </ul>
     *
     * <p><strong>Fórmula:</strong> {@code (positive / total) * 100}</p>
     *
     * @return Porcentaje de sentimientos positivos (0.0 - 100.0)
     *         Retorna 0.0 si no hay análisis registrados
     */
    @Schema(
            description = "Porcentaje de sentimientos positivos",
            example = "63.33",
            minimum = "0.0",
            maximum = "100.0"
    )
    public Double getPositivePercentage() {
        if (total == null || total == 0) {
            return 0.0;
        }
        return Math.round((positive * 10000.0) / total) / 100.0;  // Redondeo a 2 decimales
    }

    /**
     * Calcula el porcentaje de sentimientos negativos sobre el total.
     *
     * <p>Útil para identificar áreas de mejora:</p>
     * <ul>
     *   <li><strong>&lt;20%:</strong> Bajo nivel de quejas</li>
     *   <li><strong>20-40%:</strong> Nivel aceptable de quejas</li>
     *   <li><strong>&gt;40%:</strong> Alto nivel de quejas - acción requerida</li>
     * </ul>
     *
     * <p><strong>Fórmula:</strong> {@code (negative / total) * 100}</p>
     *
     * @return Porcentaje de sentimientos negativos (0.0 - 100.0)
     *         Retorna 0.0 si no hay análisis registrados
     */
    @Schema(
            description = "Porcentaje de sentimientos negativos",
            example = "36.67",
            minimum = "0.0",
            maximum = "100.0"
    )
    public Double getNegativePercentage() {
        if (total == null || total == 0) {
            return 0.0;
        }
        return Math.round((negative * 10000.0) / total) / 100.0;  // Redondeo a 2 decimales
    }
}