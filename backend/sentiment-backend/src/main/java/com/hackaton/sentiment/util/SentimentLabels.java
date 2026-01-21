package com.hackaton.sentiment.util;

/**
 * Constantes para los labels canónicos de clasificación de sentimiento.
 *
 * <p>Este sistema utiliza un modelo de clasificación <strong>BINARIO</strong>
 * que solo distingue entre sentimientos positivos y negativos.</p>
 *
 * <h2>Uso en el sistema:</h2>
 * <ul>
 *   <li><strong>Base de datos:</strong> Los labels se almacenan en formato canónico (POSITIVE/NEGATIVE)</li>
 *   <li><strong>Modelo ML:</strong> Recibe predicciones en español ("Positivo"/"Negativo")</li>
 *   <li><strong>Normalización:</strong> realizada por la capa de servicio antes de persistir</li>
 *   <li><strong>Estadísticas:</strong> {@link com.hackaton.sentiment.repository.SentimentAnalysisRepository#countByLabel(String)}</li>
 * </ul>
 *
 * <h2>Ejemplo de uso:</h2>
 * <pre>{@code
 * // Guardar en base de datos
 * SentimentAnalysis analysis = SentimentAnalysis.builder()
 *     .label(SentimentLabels.POSITIVE)
 *     .build();
 *
 * // Consultar estadísticas
 * long positiveCount = repository.countByLabel(SentimentLabels.POSITIVE);
 * }</pre>
 *
 * @author Equipo Hackathon Oracle ONE
 * @version 1.0
 * @since 2026-01-14
 */
public final class SentimentLabels {

    /**
     * Constructor privado para prevenir instanciación.
     * Esta clase solo contiene constantes estáticas.
     */
    private SentimentLabels() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    /**
     * Label canónico para sentimiento positivo.
     *
     * <p>Se usa para almacenar en base de datos todos los textos
     * clasificados como positivos por el modelo de Machine Learning.</p>
     *
     * <p><strong>Valores equivalentes del modelo ML:</strong></p>
     * <ul>
     *   <li>"Positivo" (español - lo que devuelve el modelo)</li>
     *   <li>"Positive" (inglés - después de i18n)</li>
     *   <li>"Positivo" (portugués - después de i18n)</li>
     * </ul>
     */
    public static final String POSITIVE = "POSITIVE";

    /**
     * Label canónico para sentimiento negativo.
     *
     * <p>Se usa para almacenar en base de datos todos los textos
     * clasificados como negativos por el modelo de Machine Learning.</p>
     *
     * <p><strong>Valores equivalentes del modelo ML:</strong></p>
     * <ul>
     *   <li>"Negativo" (español - lo que devuelve el modelo)</li>
     *   <li>"Negative" (inglés - después de i18n)</li>
     *   <li>"Negativo" (portugués - después de i18n)</li>
     * </ul>
     *
     * <p><strong>Fallback:</strong> Si el modelo devuelve un valor
     * desconocido, se clasifica como NEGATIVE por defecto.</p>
     */
    public static final String NEGATIVE = "NEGATIVE";
}