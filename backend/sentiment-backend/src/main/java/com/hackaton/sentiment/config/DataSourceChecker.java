package com.hackaton.sentiment.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Componente de verificación de conexión a la base de datos.
 *
 * <p>Esta clase se encarga de validar que el {@link DataSource} configurado
 * en la aplicación esté correctamente inicializado y accesible al momento
 * de arrancar el contexto de Spring.</p>
 *
 * <p>Se ejecuta automáticamente durante el ciclo de vida de la aplicación
 * y es especialmente útil para:</p>
 * <ul>
 *   <li>Verificar la configuración de la base de datos</li>
 *   <li>Detectar errores de conexión en tiempo de arranque</li>
 *   <li>Confirmar el motor de base de datos utilizado</li>
 * </ul>
 *
 * <p>La información obtenida se imprime en consola únicamente con fines
 * informativos y de diagnóstico.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class DataSourceChecker {

    /**
     * Fuente de datos configurada en el contexto de Spring.
     *
     * <p>Proporciona las conexiones JDBC necesarias para acceder
     * a la base de datos definida en la aplicación.</p>
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Verifica la conexión a la base de datos al iniciar la aplicación.
     *
     * <p>Este método se ejecuta automáticamente después de que el bean
     * ha sido completamente inicializado gracias a la anotación
     * {@link PostConstruct}.</p>
     *
     * <p>Intenta obtener una conexión activa y muestra en consola:</p>
     * <ul>
     *   <li>La URL de conexión a la base de datos</li>
     *   <li>El nombre del motor de base de datos</li>
     * </ul>
     *
     * <p>Si ocurre un error, se imprime un mensaje descriptivo indicando
     * el fallo de conexión.</p>
     */
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
