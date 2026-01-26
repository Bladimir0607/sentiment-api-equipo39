package com.hackaton.sentiment;

import org.springframework.boot.SpringApplication;

/**
 * Aplicación de prueba con Testcontainers.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public class TestSentimentBackendApplication {

	/**
	 * Método principal para iniciar la aplicación con Testcontainers.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.from(SentimentBackendApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}