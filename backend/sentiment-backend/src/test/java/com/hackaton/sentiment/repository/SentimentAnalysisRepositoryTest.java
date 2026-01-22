package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.util.SentimentLabels;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para SentimentAnalysisRepository.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SentimentAnalysisRepositoryTest {

    // Esto levanta un MySQL real en Docker solo para este test
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private SentimentAnalysisRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void countByLabel_shouldWork() {
        // 1. Crear usuario (MySQL real creará la tabla 'users' por las entidades)
        User testUser = User.builder()
                .username("jhona_" + System.currentTimeMillis())
                .password("password")
                .email("test@example.com")
                .enabled(true)
                .build();

        testUser = entityManager.persistAndFlush(testUser);

        // 2. Crear análisis
        SentimentAnalysis analysis = SentimentAnalysis.builder()
                .text("Excelente")
                .label(SentimentLabels.POSITIVE)
                .probability(0.9)
                .user(testUser)
                .build();

        repository.save(analysis);
        entityManager.flush();

        // 3. Verificar
        Long count = repository.countByLabel(SentimentLabels.POSITIVE);
        assertThat(count).isEqualTo(1);
    }
}