package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.util.SentimentLabels;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de repositorio para SentimentAnalysisRepository.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SentimentAnalysisRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private SentimentAnalysisRepository repository;

    /**
     * Prueba que countByLabel funciona correctamente.
     */
    @Test
    void countByLabel_shouldWork() {
        repository.save(
                SentimentAnalysis.builder()
                        .text("Excelente")
                        .label(SentimentLabels.POSITIVE)
                        .probability(0.9)
                        .build()
        );

        Long count = repository.countByLabel(SentimentLabels.POSITIVE);
        assertThat(count).isEqualTo(1);
    }
}