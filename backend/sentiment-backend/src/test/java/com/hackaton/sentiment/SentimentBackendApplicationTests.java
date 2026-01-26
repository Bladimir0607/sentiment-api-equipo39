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
@SpringBootTest(properties = {
		"jwt.secret=5567586B3272357538782F413F4428472B4B6250645367566B59703373367639",
		"jwt.expiration=3600000"
})
@ActiveProfiles("test")
class SentimentBackendApplicationTests {

	/**
	 * Prueba que el contexto de la aplicación carga correctamente.
	 */
	@Test
	void contextLoads() {
	}
}