package com.hackaton.sentiment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test") // Esto evita que busque la configuración de MySQL real
class SentimentBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
