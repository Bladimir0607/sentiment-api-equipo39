package com.hackaton.sentiment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de contexto de la aplicación.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class SentimentBackendApplicationTests {

	/**
	 * Prueba que el contexto de la aplicación carga correctamente.
	 */
	@Test
	void contextLoads() {
	}
}