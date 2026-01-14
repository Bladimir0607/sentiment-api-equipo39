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

@Component
@RequiredArgsConstructor
public class SentimentMlClient {

    private static final Logger log =
            LoggerFactory.getLogger(SentimentMlClient.class);

    private final RestClient restClient;

    public SentimentResponseDTO predict(String text) {
        try {
            log.info("Enviando texto al microservicio ML");

            return restClient.post()
                    .uri("/sentiment")
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
