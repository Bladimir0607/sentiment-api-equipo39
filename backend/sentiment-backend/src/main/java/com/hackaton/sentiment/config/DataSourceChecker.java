package com.hackaton.sentiment.config;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class DataSourceChecker {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void testConexion() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ Conexión exitosa: " + conn.getMetaData().getURL());
            System.out.println("🧠 Base de datos: " + conn.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}