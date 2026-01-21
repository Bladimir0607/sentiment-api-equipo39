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
 * Cliente para comunicación con el microservicio de Machine Learning de análisis de sentimientos.
 * Esta clase se encarga de enviar textos al servicio ML y procesar las respuestas.
 *
 * <p>Utiliza {@link RestClient} de Spring para realizar las llamadas HTTP al servicio remoto.
 * Maneja errores de comunicación y transforma excepciones en excepciones específicas del dominio.</p>
 *
 * @see MlServiceException
 * @see SentimentResponseDTO
 * @see RestClient
 */
@Component
@RequiredArgsConstructor
public class SentimentMlClient {

    private static final Logger log =
            LoggerFactory.getLogger(SentimentMlClient.class);

    private final RestClient restClient;

    /**
     * Envía un texto al microservicio ML para obtener un análisis de sentimiento.
     *
     * Este método realiza una llamada HTTP POST al endpoint de análisis de sentimientos,
     * enviando el texto proporcionado como parte del cuerpo de la solicitud. La respuesta
     * se mapea automáticamente a un objeto {@link SentimentResponseDTO}.</p>
     *
     * <p>En caso de error en la comunicación con el servicio ML, se lanza una
     * {@link MlServiceException} con un mensaje descriptivo y la excepción original.</p>
     *
     * <p>Se registran eventos informativos y de error mediante el logger de la clase.</p>
     *
     * @param text El texto a analizar. No debe ser nulo o vacío.
     * @return {@link SentimentResponseDTO} que contiene el resultado del análisis de sentimiento.
     * @throws MlServiceException si ocurre un error al comunicarse con el servicio ML.
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
