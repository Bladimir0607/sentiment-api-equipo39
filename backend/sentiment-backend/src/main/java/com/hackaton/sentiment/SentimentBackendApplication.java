package com.hackaton.sentiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot para análisis de sentimiento.
 * * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@SpringBootApplication
public class SentimentBackendApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(SentimentBackendApplication.class, args);
	}
}