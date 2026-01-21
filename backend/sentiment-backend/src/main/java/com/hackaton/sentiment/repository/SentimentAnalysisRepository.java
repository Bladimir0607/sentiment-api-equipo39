package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link SentimentAnalysis}.
 *
 * <p>Esta interfaz proporciona métodos para acceder, consultar y gestionar
 * los análisis de sentimiento almacenados en la base de datos.</p>
 *
 * <p>Extiende {@link JpaRepository}, lo que permite disponer automáticamente
 * de operaciones CRUD básicas, paginación y ordenamiento.</p>
 *
 * <p>Incluye además consultas personalizadas optimizadas para casos de uso
 * frecuentes, como estadísticas globales, historial por usuario y
 * carga eficiente de relaciones.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Repository
public interface SentimentAnalysisRepository
        extends JpaRepository<SentimentAnalysis, Long> {

    /**
     * Cuenta la cantidad total de análisis de sentimiento que poseen
     * una etiqueta específica.
     *
     * <p>Este método es útil para generar estadísticas agregadas
     * como el número de comentarios positivos o negativos.</p>
     *
     * @param label etiqueta del sentimiento a contar
     *              (por ejemplo: {@code "POSITIVE"}, {@code "NEGATIVE"})
     * @return número total de análisis que coinciden con la etiqueta
     */
    long countByLabel(String label);

    /**
     * Recupera todos los análisis de sentimiento realizados por un usuario específico.
     *
     * <p>Utiliza la relación directa con la entidad {@link User}.</p>
     *
     * @param user usuario cuyos análisis se desean obtener
     * @return lista de análisis de sentimiento asociados al usuario
     */
    List<SentimentAnalysis> findByUser(User user);

    /**
     * Recupera todos los análisis de sentimiento realizados por un usuario
     * a partir de su identificador.
     *
     * <p>Este método resulta útil cuando no se dispone de la entidad
     * {@link User} completa.</p>
     *
     * @param userId identificador del usuario
     * @return lista de análisis de sentimiento asociados al usuario
     */
    List<SentimentAnalysis> findByUserId(Long userId);

    /**
     * Obtiene todos los análisis de sentimiento junto con la información
     * del usuario asociado en una sola consulta.
     *
     * <p>Se utiliza {@code JOIN FETCH} para evitar el problema N+1,
     * cargando de forma eager la relación con el usuario.</p>
     *
     * <p>Los resultados se ordenan por fecha de creación descendente,
     * mostrando primero los análisis más recientes.</p>
     *
     * @return lista de análisis de sentimiento con datos de usuario incluidos
     */
    @Query("""
            SELECT sa
            FROM SentimentAnalysis sa
            JOIN FETCH sa.user u
            ORDER BY sa.createdAt DESC
           """)
    List<SentimentAnalysis> findAllWithUser();

    /**
     * Elimina todos los análisis de sentimiento asociados a un usuario específico.
     *
     * <p>Este método es especialmente útil en procesos como:
     * <ul>
     *     <li>Eliminación de cuentas de usuario</li>
     *     <li>Limpieza de datos relacionados</li>
     *     <li>Cumplimiento de políticas de privacidad</li>
     * </ul>
     *
     * <p>La operación se ejecuta directamente en base de datos
     * mediante una consulta derivada.</p>
     *
     * @param user usuario cuyos análisis de sentimiento serán eliminados
     */
    void deleteByUser(User user);
}