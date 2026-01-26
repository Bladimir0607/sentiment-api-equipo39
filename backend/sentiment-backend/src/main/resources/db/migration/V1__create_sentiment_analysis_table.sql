/**
 * Tabla para almacenar análisis de sentimiento.
 */
CREATE TABLE sentiment_analysis (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    text VARCHAR(500) NOT NULL,
                                    label VARCHAR(50) NOT NULL,
                                    probability DOUBLE NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);