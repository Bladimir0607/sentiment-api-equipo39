package com.hackaton.sentiment.client;

import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.exception.MlServiceException;
import com.hackaton.sentiment.util.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente para la comunicación con el microservicio de Machine Learning
 * encargado del análisis de sentimientos.
 *
 * <p>Esta clase se encarga de enviar textos al servicio de ML y procesar
 * las respuestas obtenidas.</p>
 *
 * <p>Utiliza {@link RestClient} de Spring para realizar las llamadas HTTP
 * al servicio remoto. Maneja errores de comunicación y transforma
 * excepciones genéricas en excepciones específicas del dominio.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 * @see MlServiceException
 * @see SentimentResponseDTO
 * @see RestClient
 */
@Component
@RequiredArgsConstructor
public class SentimentMlClient {

    /**
     * Logger de la clase para el registro de eventos informativos y de error.
     */
    private static final Logger log =
            LoggerFactory.getLogger(SentimentMlClient.class);

    /**
     * Cliente REST utilizado para comunicarse con el microservicio de ML.
     */
    private final RestClient restClient;

    /**
     * Envía un texto al microservicio de Machine Learning para obtener
     * un análisis de sentimiento.
     *
     * <p>Este método realiza una llamada HTTP POST al endpoint de análisis
     * de sentimientos, enviando el texto proporcionado como parte del cuerpo
     * de la solicitud.</p>
     *
     * <p>La respuesta del servicio se mapea automáticamente a un objeto
     * {@link SentimentResponseDTO}.</p>
     *
     * <p>En caso de producirse un error durante la comunicación con el
     * microservicio de ML, se lanza una {@link MlServiceException} con
     * un mensaje descriptivo y la excepción original.</p>
     *
     * <p>Los eventos relevantes se registran mediante el logger de la clase.</p>
     *
     * @param text texto a analizar; no debe ser nulo ni vacío
     * @return objeto {@link SentimentResponseDTO} con el resultado del análisis
     * @throws MlServiceException si ocurre un error al comunicarse con el servicio ML
     */
    public SentimentResponseDTO predict(String text) {
        try {
            log.info("Enviando texto al microservicio ML");

            return restClient.post()
                    .uri("/sentiment-explain")
                    .body(Map.of("text", text))
                    .retrieve()
                    .body(SentimentResponseDTO.class);

        } catch (Exception ex) {
            log.error("Error llamando al microservicio ML", ex);
            throw new MlServiceException(
                    Constants.ML_SERVICE_ERROR, ex);
        }
    }
}
