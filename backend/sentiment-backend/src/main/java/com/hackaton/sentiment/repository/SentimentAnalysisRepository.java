package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repositorio para la entidad SentimentAnalysis.
 *
 * Proporciona métodos para acceder y gestionar los análisis de sentimiento
 * almacenados en la base de datos, incluyendo consultas personalizadas y
 * operaciones relacionadas con usuarios.
 *
 * Extiende {@link JpaRepository} para obtener operaciones CRUD básicas
 * y permite la definición de consultas personalizadas usando nombres de métodos
 * siguiendo las convenciones de Spring Data JPA.
 */
@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    /**
     * Cuenta la cantidad de análisis con una etiqueta específica.
     *
     * @param label Etiqueta del sentimiento a contar (ej: "POSITIVE", "NEGATIVE")
     * @return Número total de análisis con la etiqueta especificada
     */
    long countByLabel(String label);

    /**
     * Busca todos los análisis de sentimiento realizados por un usuario específico.
     *
     * @param user Usuario cuyos análisis se desean recuperar
     * @return Lista de análisis de sentimiento asociados al usuario
     */
    List<SentimentAnalysis> findByUser(User user);

    /**
     * Busca todos los análisis de sentimiento realizados por un usuario ID específico.
     *
     * @param userId ID del usuario cuyos análisis se desean recuperar
     * @return Lista de análisis de sentimiento asociados al usuario ID
     */
    List<SentimentAnalysis> findByUserId(Long userId);

    /**
     * Obtiene todos los análisis de sentimiento incluyendo la información del usuario
     * en una sola consulta para evitar el problema N+1.
     *
     * Utiliza JOIN FETCH para cargar eagermente la relación con el usuario,
     * optimizando el rendimiento cuando se necesita acceder a los datos del usuario
     * junto con los análisis.
     *
     * @return Lista de análisis de sentimiento ordenados por fecha de creación descendente
     */
    @Query("SELECT sa FROM SentimentAnalysis sa JOIN FETCH sa.user u ORDER BY sa.createdAt DESC")
    List<SentimentAnalysis> findAllWithUser();
}