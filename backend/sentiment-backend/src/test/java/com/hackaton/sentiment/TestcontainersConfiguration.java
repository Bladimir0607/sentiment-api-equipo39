package com.hackaton.sentiment;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configuración de Testcontainers para pruebas.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	/**
	 * Contenedor de MySQL para pruebas.
	 *
	 * @return contenedor MySQL configurado
	 */
	@Bean
	@ServiceConnection
	MySQLContainer mysqlContainer() {
		return new MySQLContainer(DockerImageName.parse("mysql:latest"));
	}
}